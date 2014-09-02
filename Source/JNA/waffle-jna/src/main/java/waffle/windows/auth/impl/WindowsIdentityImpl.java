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

import java.util.ArrayList;
import java.util.List;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.WELL_KNOWN_SID_TYPE;

/**
 * Windows Identity.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class WindowsIdentityImpl implements IWindowsIdentity {

    private HANDLE    windowsIdentity;
    private Account[] userGroups;
    private Account   windowsAccount;

    public WindowsIdentityImpl(final HANDLE newWindowsIdentity) {
        this.windowsIdentity = newWindowsIdentity;
    }

    private Account getWindowsAccount() {
        if (this.windowsAccount == null) {
            this.windowsAccount = Advapi32Util.getTokenAccount(this.windowsIdentity);
        }
        return this.windowsAccount;
    }

    private Account[] getUserGroups() {
        if (this.userGroups == null) {
            this.userGroups = Advapi32Util.getTokenGroups(this.windowsIdentity);
        }
        return this.userGroups.clone();
    }

    @Override
    public String getFqn() {
        return getWindowsAccount().fqn;
    }

    @Override
    public IWindowsAccount[] getGroups() {

        final Account[] groups = getUserGroups();

        final List<IWindowsAccount> result = new ArrayList<IWindowsAccount>(groups.length);
        for (Account userGroup : groups) {
            WindowsAccountImpl account = new WindowsAccountImpl(userGroup);
            result.add(account);
        }

        return result.toArray(new IWindowsAccount[0]);
    }

    @Override
    public byte[] getSid() {
        return getWindowsAccount().sid;
    }

    @Override
    public String getSidString() {
        return getWindowsAccount().sidString;
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
        for (Account userGroup : getUserGroups()) {
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

        if (Advapi32Util.isWellKnownSid(getSid(), WELL_KNOWN_SID_TYPE.WinAnonymousSid)) {
            return true;
        }

        return false;
    }
}
