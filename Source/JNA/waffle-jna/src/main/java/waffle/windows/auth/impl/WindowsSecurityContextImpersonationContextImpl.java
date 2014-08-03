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

import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.WinError;

import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsSecurityContextImpersonationContextImpl implements IWindowsImpersonationContext {

    private CtxtHandle ctx;

    public WindowsSecurityContextImpersonationContextImpl(final CtxtHandle ctx) {
        final int rc = Secur32.INSTANCE.ImpersonateSecurityContext(ctx);
        if (rc != WinError.SEC_E_OK) {
            throw new Win32Exception(rc);
        }

        this.ctx = ctx;
    }

    @Override
    public void revertToSelf() {
        final int rc = Secur32.INSTANCE.RevertSecurityContext(this.ctx);
        if (rc != WinError.SEC_E_OK) {
            throw new Win32Exception(rc);
        }
    }
}
