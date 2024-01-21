/*
 * MIT License
 *
 * Copyright (c) 2010-2024 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Netapi32Util.DomainTrust;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.SspiUtil.ManagedSecBufferDesc;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import waffle.util.cache.Cache;
import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * Windows Auth Provider.
 */
public class WindowsAuthProviderImpl implements IWindowsAuthProvider {

    /** The Continue Context Timeout. */
    public static final int CONTINUE_CONTEXT_TIMEOUT = 30;

    /**
     * The Class ContinueContext.
     */
    private static class ContinueContext {
        /** The continue handle. */
        CtxtHandle continueHandle;

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
        this(WindowsAuthProviderImpl.CONTINUE_CONTEXT_TIMEOUT);
    }

    /**
     * A Windows authentication provider.
     *
     * @param continueContextsTimeout
     *            Timeout for security contexts in seconds.
     */
    public WindowsAuthProviderImpl(final int continueContextsTimeout) {
        this.continueContexts = Cache.newCache(continueContextsTimeout);
    }

    @Override
    public IWindowsSecurityContext acceptSecurityToken(final String connectionId, final byte[] token,
            final String securityPackage) {

        if (token == null || token.length == 0) {
            this.resetSecurityToken(connectionId);
            throw new Win32Exception(WinError.SEC_E_INVALID_TOKEN);
        }

        CtxtHandle continueHandle = null;
        IWindowsCredentialsHandle serverCredential;
        ContinueContext continueContext = this.continueContexts.get(connectionId);
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
            final ManagedSecBufferDesc pbServerToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, tokenSize);
            final ManagedSecBufferDesc pbClientToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, token);
            final IntByReference pfClientContextAttr = new IntByReference();

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
                    this.resetSecurityToken(connectionId);
                    // if an output token was generated by the function, it must be sent to the client process
                    if (pbServerToken.pBuffers != null && pbServerToken.cBuffers == 1
                            && pbServerToken.getBuffer(0).cbBuffer > 0) {
                        sc.setToken(pbServerToken.getBuffer(0).getBytes() == null ? new byte[0]
                                : pbServerToken.getBuffer(0).getBytes().clone());
                    }
                    sc.setContinue(false);
                    break;
                case WinError.SEC_I_CONTINUE_NEEDED:
                    // the server must send the output token to the client and wait for a returned token
                    continueContext = new ContinueContext(phNewServerContext, serverCredential);
                    this.continueContexts.put(connectionId, continueContext);
                    sc.setToken(pbServerToken.getBuffer(0).getBytes() == null ? new byte[0]
                            : pbServerToken.getBuffer(0).getBytes().clone());
                    sc.setContinue(true);
                    break;
                default:
                    sc.dispose();
                    WindowsSecurityContextImpl.dispose(continueHandle);
                    this.resetSecurityToken(connectionId);
                    throw new Win32Exception(rc);
            }
        } while (rc == WinError.SEC_E_BUFFER_TOO_SMALL);

        return sc;
    }

    @Override
    public IWindowsComputer getCurrentComputer() {
        try {
            return new WindowsComputerImpl(InetAddress.getLocalHost().getHostName());
        } catch (final UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IWindowsDomain[] getDomains() {
        final List<IWindowsDomain> domains = new ArrayList<>();
        final DomainTrust[] trusts = Netapi32Util.getDomainTrusts();
        for (final DomainTrust trust : trusts) {
            domains.add(new WindowsDomainImpl(trust));
        }
        return domains.toArray(new IWindowsDomain[0]);
    }

    @Override
    public IWindowsIdentity logonDomainUser(final String username, final String domain, final String password) {
        return this.logonDomainUserEx(username, domain, password, WinBase.LOGON32_LOGON_NETWORK,
                WinBase.LOGON32_PROVIDER_DEFAULT);
    }

    @Override
    public IWindowsIdentity logonDomainUserEx(final String username, final String domain, final String password,
            final int logonType, final int logonProvider) {
        final HANDLEByReference phUser = new HANDLEByReference();
        if (!Advapi32.INSTANCE.LogonUser(username, domain, password, logonType, logonProvider, phUser)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return new WindowsIdentityImpl(phUser.getValue());
    }

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

    @Override
    public IWindowsAccount lookupAccount(final String username) {
        return new WindowsAccountImpl(username);
    }

    @Override
    public void resetSecurityToken(final String connectionId) {
        this.continueContexts.remove(connectionId);
    }

    /**
     * Number of elements in the continue contexts map.
     *
     * @return Number of elements in the hash map.
     */
    public int getContinueContextsSize() {
        return this.continueContexts.size();
    }
}
