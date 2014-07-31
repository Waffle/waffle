/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.windows.auth.impl;

import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;
import waffle.windows.auth.IWindowsSecurityContext;

import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;

/**
 * Windows Security Context.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class WindowsSecurityContextImpl implements IWindowsSecurityContext {

    private String         principalName;
    private String         securityPackage;
    private SecBufferDesc  token;
    private CtxtHandle     ctx;
    private IntByReference attr;
    private CredHandle     credentials;
    private boolean        continueFlag;

    @Override
    public IWindowsIdentity getIdentity() {
        HANDLEByReference phContextToken = new HANDLEByReference();
        int rc = Secur32.INSTANCE.QuerySecurityContextToken(this.ctx, phContextToken);
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
        return this.token == null ? null : this.token.getBytes().clone();
    }

    /**
     * Get the current Windows security context for a given SSPI package.
     * 
     * @param securityPackage
     *            SSPI package.
     * @return Windows security context.
     */
    public static IWindowsSecurityContext getCurrent(String securityPackage, String targetName) {
        IWindowsCredentialsHandle credentialsHandle = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
        credentialsHandle.initialize();
        try {
            WindowsSecurityContextImpl ctx = new WindowsSecurityContextImpl();
            ctx.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
            ctx.setCredentialsHandle(credentialsHandle.getHandle());
            ctx.setSecurityPackage(securityPackage);
            ctx.initialize(null, null, targetName);
            return ctx;
        } finally {
            credentialsHandle.dispose();
        }
    }

    @Override
    public void initialize(CtxtHandle continueCtx, SecBufferDesc continueToken, String targetName) {
        this.attr = new IntByReference();
        this.ctx = new CtxtHandle();
        int tokenSize = Sspi.MAX_TOKEN_SIZE;
        int rc = 0;
        do {
            this.token = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, tokenSize);
            rc = Secur32.INSTANCE.InitializeSecurityContext(this.credentials, continueCtx, targetName,
                    Sspi.ISC_REQ_CONNECTION, 0, Sspi.SECURITY_NATIVE_DREP, continueToken, 0, this.ctx, this.token,
                    this.attr, null);
            switch (rc) {
                case WinError.SEC_E_INSUFFICIENT_MEMORY:
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
        } while (rc == WinError.SEC_E_INSUFFICIENT_MEMORY);
    }

    @Override
    public void dispose() {
        dispose(this.ctx);
    }

    /**
     * Dispose a security context.
     * 
     * @param ctx
     *            Security context.
     * @return True if a context was disposed.
     */
    public static boolean dispose(CtxtHandle ctx) {
        if (ctx != null && !ctx.isNull()) {
            int rc = Secur32.INSTANCE.DeleteSecurityContext(ctx);
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

    public void setPrincipalName(String value) {
        this.principalName = value;
    }

    @Override
    public CtxtHandle getHandle() {
        return this.ctx;
    }

    public void setCredentialsHandle(CredHandle handle) {
        this.credentials = handle;
    }

    public void setToken(byte[] bytes) {
        this.token = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, bytes);
    }

    public void setSecurityPackage(String value) {
        this.securityPackage = value;
    }

    public void setSecurityContext(CtxtHandle phNewServerContext) {
        this.ctx = phNewServerContext;
    }

    @Override
    public boolean isContinue() {
        return this.continueFlag;
    }

    public void setContinue(boolean b) {
        this.continueFlag = b;
    }

    @Override
    public IWindowsImpersonationContext impersonate() {
        return new WindowsSecurityContextImpersonationContextImpl(this.ctx);
    }
}
