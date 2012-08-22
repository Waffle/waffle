/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
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

	private HANDLE _windowsIdentity = null;
	private Account[] _userGroups = null;
	private Account _windowsAccount = null;

	private Account getWindowsAccount() {
		if (_windowsAccount == null) {
			_windowsAccount = Advapi32Util.getTokenAccount(_windowsIdentity);
		}
		return _windowsAccount;
	}

	private Account[] getUserGroups() {
		if (_userGroups == null) {
			_userGroups = Advapi32Util.getTokenGroups(_windowsIdentity);
		}
		return _userGroups.clone();
	}

	@Override
	public String getFqn() {
		return getWindowsAccount().fqn;
	}

	@Override
	public IWindowsAccount[] getGroups() {

		Account[] userGroups = getUserGroups();

		List<IWindowsAccount> result = new ArrayList<IWindowsAccount>(
				userGroups.length);
		for (Account userGroup : userGroups) {
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
		if (_windowsIdentity != null) {
			Kernel32.INSTANCE.CloseHandle(_windowsIdentity);
		}
	}

	@Override
	public IWindowsImpersonationContext impersonate() {
		return new WindowsIdentityImpersonationContextImpl(_windowsIdentity);
	}

	public WindowsIdentityImpl(HANDLE windowsIdentity) {
		_windowsIdentity = windowsIdentity;
	}

	@Override
	public boolean isGuest() {
		for (Account userGroup : getUserGroups()) {
			if (Advapi32Util.isWellKnownSid(userGroup.sid,
					WELL_KNOWN_SID_TYPE.WinBuiltinGuestsSid)) {
				return true;
			}
			if (Advapi32Util.isWellKnownSid(userGroup.sid,
					WELL_KNOWN_SID_TYPE.WinAccountDomainGuestsSid)) {
				return true;
			}
			if (Advapi32Util.isWellKnownSid(userGroup.sid,
					WELL_KNOWN_SID_TYPE.WinAccountGuestSid)) {
				return true;
			}
		}

		if (Advapi32Util.isWellKnownSid(getSid(),
				WELL_KNOWN_SID_TYPE.WinAnonymousSid)) {
			return true;
		}

		return false;
	}
}
