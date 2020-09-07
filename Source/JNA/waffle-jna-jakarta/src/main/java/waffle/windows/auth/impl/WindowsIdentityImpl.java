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

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.WELL_KNOWN_SID_TYPE;

import java.util.ArrayList;
import java.util.List;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * Windows Identity.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsIdentityImpl implements IWindowsIdentity {

    /** The windows identity. */
    private final HANDLE windowsIdentity;

    /** The user groups. */
    private Account[] userGroups;

    /** The windows account. */
    private Account windowsAccount;

    /**
     * Instantiates a new windows identity impl.
     *
     * @param newWindowsIdentity
     *            the new windows identity
     */
    public WindowsIdentityImpl(final HANDLE newWindowsIdentity) {
        this.windowsIdentity = newWindowsIdentity;
    }

    /**
     * Gets the windows account.
     *
     * @return the windows account
     */
    private Account getWindowsAccount() {
        if (this.windowsAccount == null) {
            this.windowsAccount = Advapi32Util.getTokenAccount(this.windowsIdentity);
        }
        return this.windowsAccount;
    }

    /**
     * Gets the user groups.
     *
     * @return the user groups
     */
    private Account[] getUserGroups() {
        if (this.userGroups == null) {
            this.userGroups = Advapi32Util.getTokenGroups(this.windowsIdentity);
        }
        return this.userGroups.clone();
    }

    @Override
    public String getFqn() {
        return this.getWindowsAccount().fqn;
    }

    @Override
    public IWindowsAccount[] getGroups() {

        final Account[] groups = this.getUserGroups();

        final List<IWindowsAccount> result = new ArrayList<>(groups.length);
        for (final Account userGroup : groups) {
            final WindowsAccountImpl account = new WindowsAccountImpl(userGroup);
            result.add(account);
        }

        return result.toArray(new IWindowsAccount[0]);
    }

    @Override
    public byte[] getSid() {
        return this.getWindowsAccount().sid;
    }

    @Override
    public String getSidString() {
        return this.getWindowsAccount().sidString;
    }

    @Override
    public void dispose() {
        if (this.windowsIdentity != null) {
            Kernel32.INSTANCE.CloseHandle(this.windowsIdentity);
        }
    }

    @Override
    public IWindowsImpersonationContext impersonate() {
        return new WindowsIdentityImpersonationContextImpl(this.windowsIdentity);
    }

    @Override
    public boolean isGuest() {
        for (final Account userGroup : this.getUserGroups()) {
            if (Advapi32Util.isWellKnownSid(userGroup.sid, WELL_KNOWN_SID_TYPE.WinBuiltinGuestsSid)) {
                return true;
            }
            if (Advapi32Util.isWellKnownSid(userGroup.sid, WELL_KNOWN_SID_TYPE.WinAccountDomainGuestsSid)) {
                return true;
            }
            if (Advapi32Util.isWellKnownSid(userGroup.sid, WELL_KNOWN_SID_TYPE.WinAccountGuestSid)) {
                return true;
            }
        }
        return Advapi32Util.isWellKnownSid(this.getSid(), WELL_KNOWN_SID_TYPE.WinAnonymousSid);
    }
}
