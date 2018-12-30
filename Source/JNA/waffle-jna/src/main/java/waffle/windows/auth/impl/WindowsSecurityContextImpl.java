/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.SspiUtil.ManagedSecBufferDesc;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;

import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * Windows Security Context.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsSecurityContextImpl implements IWindowsSecurityContext {

    /** The principal name. */
    private String principalName;

    /** The security package. */
    private String securityPackage;

    /** The token. */
    private ManagedSecBufferDesc token;

    /** The ctx. */
    private CtxtHandle ctx;

    /** The credentials. */
    private IWindowsCredentialsHandle credentials;

    /** The continue flag. */
    private boolean continueFlag;

    @Override
    public IWindowsImpersonationContext impersonate() {
        return new WindowsSecurityContextImpersonationContextImpl(this.ctx);
    }

    @Override
    public IWindowsIdentity getIdentity() {
        final HANDLEByReference phContextToken = new HANDLEByReference();
        final int rc = Secur32.INSTANCE.QuerySecurityContextToken(this.ctx, phContextToken);
        if (WinError.SEC_E_OK != rc) {
            throw new Win32Exception(rc);
        }
        return new WindowsIdentityImpl(phContextToken.getValue());
    }

    @Override
    public String getSecurityPackage() {
        return this.securityPackage;
    }

    @Override
    public byte[] getToken() {
        return this.token == null || this.token.getBuffer(0).getBytes() == null ? null
                : this.token.getBuffer(0).getBytes().clone();
    }

    /**
     * Get the current Windows security context for a given SSPI package.
     *
     * @param securityPackage
     *            SSPI package.
     * @param targetName
     *            The target of the context. The string contents are security-package specific.
     * @return Windows security context.
     */
    public static IWindowsSecurityContext getCurrent(final String securityPackage, final String targetName) {
        IWindowsCredentialsHandle credentialsHandle = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
        credentialsHandle.initialize();
        try {
            final WindowsSecurityContextImpl ctx = new WindowsSecurityContextImpl();
            ctx.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
            ctx.setCredentialsHandle(credentialsHandle);
            ctx.setSecurityPackage(securityPackage);
            ctx.initialize(null, null, targetName);

            // Starting from here ctx 'owns' the credentials handle, so let's null out the
            // variable. This will prevent the finally block below from disposing it right away.
            credentialsHandle = null;

            return ctx;
        } finally {
            if (credentialsHandle != null) {
                credentialsHandle.dispose();
            }
        }
    }

    @Override
    public void initialize(final CtxtHandle continueCtx, final SecBufferDesc continueToken, final String targetName) {
        final IntByReference attr = new IntByReference();
        this.ctx = new CtxtHandle();
        int tokenSize = Sspi.MAX_TOKEN_SIZE;
        int rc;
        do {
            this.token = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, tokenSize);
            rc = Secur32.INSTANCE.InitializeSecurityContext(this.credentials.getHandle(), continueCtx, targetName,
                    Sspi.ISC_REQ_CONNECTION, 0, Sspi.SECURITY_NATIVE_DREP, continueToken, 0, this.ctx, this.token, attr,
                    null);
            switch (rc) {
                case WinError.SEC_E_INSUFFICIENT_MEMORY:
                case WinError.SEC_E_BUFFER_TOO_SMALL:
                    tokenSize += Sspi.MAX_TOKEN_SIZE;
                    break;
                case WinError.SEC_I_CONTINUE_NEEDED:
                    this.continueFlag = true;
                    break;
                case WinError.SEC_E_OK:
                    this.continueFlag = false;
                    break;
                default:
                    throw new Win32Exception(rc);
            }
        } while (rc == WinError.SEC_E_INSUFFICIENT_MEMORY || rc == WinError.SEC_E_BUFFER_TOO_SMALL);
    }

    @Override
    public void dispose() {
        WindowsSecurityContextImpl.dispose(this.ctx);

        if (this.credentials != null) {
            this.credentials.dispose();
        }
    }

    /**
     * Dispose a security context.
     *
     * @param ctx
     *            Security context.
     * @return True if a context was disposed.
     */
    public static boolean dispose(final CtxtHandle ctx) {
        if (ctx != null && !ctx.isNull()) {
            final int rc = Secur32.INSTANCE.DeleteSecurityContext(ctx);
            if (WinError.SEC_E_OK != rc) {
                throw new Win32Exception(rc);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getPrincipalName() {
        return this.principalName;
    }

    /**
     * Sets the principal name.
     *
     * @param value
     *            the new principal name
     */
    public void setPrincipalName(final String value) {
        this.principalName = value;
    }

    @Override
    public CtxtHandle getHandle() {
        return this.ctx;
    }

    /**
     * Sets the credentials handle.
     *
     * @param handle
     *            the new credentials handle
     */
    public void setCredentialsHandle(final IWindowsCredentialsHandle handle) {
        this.credentials = handle;
    }

    /**
     * Sets the token.
     *
     * @param bytes
     *            the new token
     */
    public void setToken(final byte[] bytes) {
        this.token = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, bytes);
    }

    /**
     * Sets the security package.
     *
     * @param value
     *            the new security package
     */
    public void setSecurityPackage(final String value) {
        this.securityPackage = value;
    }

    /**
     * Sets the security context.
     *
     * @param phNewServerContext
     *            the new security context
     */
    public void setSecurityContext(final CtxtHandle phNewServerContext) {
        this.ctx = phNewServerContext;
    }

    @Override
    public boolean isContinue() {
        return this.continueFlag;
    }

    /**
     * Sets the continue.
     *
     * @param b
     *            the new continue
     */
    public void setContinue(final boolean b) {
        this.continueFlag = b;
    }

}
