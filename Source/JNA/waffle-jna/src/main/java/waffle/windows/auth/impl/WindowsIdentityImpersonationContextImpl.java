/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * The Class WindowsIdentityImpersonationContextImpl.
 *
 * @author dblock[at]dblock[dot]org
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
