/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * The Class WindowsIdentityImpersonationContextImpl.
 */
public class WindowsIdentityImpersonationContextImpl implements IWindowsImpersonationContext {

    /**
     * Impersonate a logged on user.
     *
     * @param windowsIdentity
     *            Windows identity obtained via LogonUser.
     */
    public WindowsIdentityImpersonationContextImpl(final HANDLE windowsIdentity) {
        if (!Advapi32.INSTANCE.ImpersonateLoggedOnUser(windowsIdentity)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    @Override
    public void revertToSelf() {
        Advapi32.INSTANCE.RevertToSelf();
    }
}
