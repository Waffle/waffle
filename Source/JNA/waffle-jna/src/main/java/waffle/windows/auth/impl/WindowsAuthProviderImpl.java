/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.windows.auth.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Netapi32Util.DomainTrust;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * Windows Auth Provider.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthProviderImpl implements IWindowsAuthProvider {

    /**
     * The Class ContinueContext.
     */
    private static class ContinueContext {
        /** The continue handle. */
        CtxtHandle                continueHandle;

        /** The server credential. */
        IWindowsCredentialsHandle serverCredential;

        /**
         * Instantiates a new continue context.
         *
         * @param handle
         *            the handle
         * @param windowsCredential
         *            the windows credential
         */
        public ContinueContext(final CtxtHandle handle, final IWindowsCredentialsHandle windowsCredential) {
            this.continueHandle = handle;
            this.serverCredential = windowsCredential;
        }
    }

    /** The continue contexts. */
    private final Cache<String, ContinueContext> continueContexts;

    /**
     * Instantiates a new windows auth provider impl.
     */
    public WindowsAuthProviderImpl() {
        this(30);
    }

    /**
     * A Windows authentication provider.
     *
     * @param continueContextsTimeout
     *            Timeout for security contexts in seconds.
     */
    public WindowsAuthProviderImpl(final int continueContextsTimeout) {
        this.continueContexts = CacheBuilder.newBuilder().expireAfterWrite(continueContextsTimeout, TimeUnit.SECONDS)
                .build();
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#acceptSecurityToken(java.lang.String, byte[], java.lang.String)
     */
    @Override
    public IWindowsSecurityContext acceptSecurityToken(final String connectionId, final byte[] token,
            final String securityPackage) {

        if (token == null || token.length == 0) {
            this.continueContexts.asMap().remove(connectionId);
            throw new Win32Exception(WinError.SEC_E_INVALID_TOKEN);
        }

        CtxtHandle continueHandle = null;
        IWindowsCredentialsHandle serverCredential;
        ContinueContext continueContext = this.continueContexts.asMap().get(connectionId);
        if (continueContext != null) {
            continueHandle = continueContext.continueHandle;
            serverCredential = continueContext.serverCredential;
        } else {
            serverCredential = new WindowsCredentialsHandleImpl(null, Sspi.SECPKG_CRED_INBOUND, securityPackage);
            serverCredential.initialize();
        }

        WindowsSecurityContextImpl sc;

        int rc;
        int tokenSize = Sspi.MAX_TOKEN_SIZE;

        do {
            final SecBufferDesc pbServerToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, tokenSize);
            final SecBufferDesc pbClientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, token);
            final IntByReference pfClientContextAttr = new IntByReference();

            // TODO This is a dead store...do we have a bug?
            continueContext = this.continueContexts.asMap().get(connectionId);

            final CtxtHandle phNewServerContext = new CtxtHandle();
            rc = Secur32.INSTANCE.AcceptSecurityContext(serverCredential.getHandle(), continueHandle, pbClientToken,
                    Sspi.ISC_REQ_CONNECTION, Sspi.SECURITY_NATIVE_DREP, phNewServerContext, pbServerToken,
                    pfClientContextAttr, null);

            sc = new WindowsSecurityContextImpl();
            sc.setCredentialsHandle(serverCredential);
            sc.setSecurityPackage(securityPackage);
            sc.setSecurityContext(phNewServerContext);

            switch (rc) {
                case WinError.SEC_E_BUFFER_TOO_SMALL:
                    tokenSize += Sspi.MAX_TOKEN_SIZE;
                    sc.dispose();
                    WindowsSecurityContextImpl.dispose(continueHandle);
                    break;
                case WinError.SEC_E_OK:
                    // the security context received from the client was accepted
                    this.continueContexts.asMap().remove(connectionId);
                    // if an output token was generated by the function, it must be sent to the client process
                    if (pbServerToken.pBuffers != null && pbServerToken.cBuffers == 1
                            && pbServerToken.pBuffers[0].cbBuffer > 0) {
                        sc.setToken(pbServerToken.getBytes() == null ? new byte[0] : pbServerToken.getBytes().clone());
                    }
                    sc.setContinue(false);
                    break;
                case WinError.SEC_I_CONTINUE_NEEDED:
                    // the server must send the output token to the client and wait for a returned token
                    continueContext = new ContinueContext(phNewServerContext, serverCredential);
                    this.continueContexts.put(connectionId, continueContext);
                    sc.setToken(pbServerToken.getBytes() == null ? new byte[0] : pbServerToken.getBytes().clone());
                    sc.setContinue(true);
                    break;
                default:
                    sc.dispose();
                    WindowsSecurityContextImpl.dispose(continueHandle);
                    this.continueContexts.asMap().remove(connectionId);
                    throw new Win32Exception(rc);
            }
        } while (rc == WinError.SEC_E_BUFFER_TOO_SMALL);

        return sc;
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#getCurrentComputer()
     */
    @Override
    public IWindowsComputer getCurrentComputer() {
        try {
            return new WindowsComputerImpl(InetAddress.getLocalHost().getHostName());
        } catch (final UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#getDomains()
     */
    @Override
    public IWindowsDomain[] getDomains() {
        final List<IWindowsDomain> domains = new ArrayList<>();
        final DomainTrust[] trusts = Netapi32Util.getDomainTrusts();
        for (final DomainTrust trust : trusts) {
            domains.add(new WindowsDomainImpl(trust));
        }
        return domains.toArray(new IWindowsDomain[0]);
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#logonDomainUser(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public IWindowsIdentity logonDomainUser(final String username, final String domain, final String password) {
        return this.logonDomainUserEx(username, domain, password, WinBase.LOGON32_LOGON_NETWORK,
                WinBase.LOGON32_PROVIDER_DEFAULT);
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#logonDomainUserEx(java.lang.String, java.lang.String,
     * java.lang.String, int, int)
     */
    @Override
    public IWindowsIdentity logonDomainUserEx(final String username, final String domain, final String password,
            final int logonType, final int logonProvider) {
        final HANDLEByReference phUser = new HANDLEByReference();
        if (!Advapi32.INSTANCE.LogonUser(username, domain, password, logonType, logonProvider, phUser)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return new WindowsIdentityImpl(phUser.getValue());
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#logonUser(java.lang.String, java.lang.String)
     */
    @Override
    public IWindowsIdentity logonUser(final String username, final String password) {
        // username@domain UPN format is natively supported by the
        // Windows LogonUser API process domain\\username format
        final String[] userNameDomain = username.split("\\\\", 2);
        if (userNameDomain.length == 2) {
            return this.logonDomainUser(userNameDomain[1], userNameDomain[0], password);
        }
        return this.logonDomainUser(username, null, password);
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#lookupAccount(java.lang.String)
     */
    @Override
    public IWindowsAccount lookupAccount(final String username) {
        return new WindowsAccountImpl(username);
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#resetSecurityToken(java.lang.String)
     */
    @Override
    public void resetSecurityToken(final String connectionId) {
        this.continueContexts.asMap().remove(connectionId);
    }

    /**
     * Number of elements in the continue contexts map.
     *
     * @return Number of elements in the hash map.
     */
    public int getContinueContextsSize() {
        return this.continueContexts.asMap().size();
    }
}
