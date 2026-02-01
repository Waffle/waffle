/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;

import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * The Class WindowsSecurityContextImpersonationContextImpl.
 */
public class WindowsSecurityContextImpersonationContextImpl implements IWindowsImpersonationContext {

    /** The ctx. */
    private final CtxtHandle ctx;

    /**
     * Instantiates a new windows security context impersonation context impl.
     *
     * @param newCtx
     *            the new ctx
     */
    public WindowsSecurityContextImpersonationContextImpl(final CtxtHandle newCtx) {
        final int rc = Secur32.INSTANCE.ImpersonateSecurityContext(newCtx);
        if (rc != WinError.SEC_E_OK) {
            throw new Win32Exception(rc);
        }

        this.ctx = newCtx;
    }

    @Override
    public void revertToSelf() {
        final int rc = Secur32.INSTANCE.RevertSecurityContext(this.ctx);
        if (rc != WinError.SEC_E_OK) {
            throw new Win32Exception(rc);
        }
    }
}
