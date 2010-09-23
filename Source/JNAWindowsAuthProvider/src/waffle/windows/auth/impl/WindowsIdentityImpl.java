/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth.impl;

import java.util.ArrayList;

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
		return _userGroups;		
	}
	
	public String getFqn() {
		return getWindowsAccount().fqn;
	}

	public IWindowsAccount[] getGroups() {

		Account[] userGroups = getUserGroups();
		
		ArrayList<IWindowsAccount> result = new ArrayList<IWindowsAccount>(userGroups.length);
		for(Account userGroup : userGroups) {
			WindowsAccountImpl account = new WindowsAccountImpl(userGroup);
			result.add(account);			
		}
		
		return result.toArray(new IWindowsAccount[0]);
	}
	
	public byte[] getSid() {
		return getWindowsAccount().sid;
	}

	public String getSidString() {
		return getWindowsAccount().sidString;
	}
	
	public void dispose() {
		if (_windowsIdentity != null) {
			Kernel32.INSTANCE.CloseHandle(_windowsIdentity);
			_windowsIdentity = null;
		}
	}
	
	public IWindowsImpersonationContext impersonate() {
		return new WindowsIdentityImpersonationContextImpl(_windowsIdentity);
	}

	public WindowsIdentityImpl(HANDLE windowsIdentity) {
		_windowsIdentity = windowsIdentity;
	}

	public boolean isGuest() {
		for(Account userGroup : getUserGroups()) {
			if (Advapi32Util.isWellKnownSid(userGroup.sid, WELL_KNOWN_SID_TYPE.WinBuiltinGuestsSid))
				return true;
			if (Advapi32Util.isWellKnownSid(userGroup.sid, WELL_KNOWN_SID_TYPE.WinAccountDomainGuestsSid))
				return true;
			if (Advapi32Util.isWellKnownSid(userGroup.sid, WELL_KNOWN_SID_TYPE.WinAccountGuestSid))
				return true;
		}
		
		if (Advapi32Util.isWellKnownSid(getSid(), WELL_KNOWN_SID_TYPE.WinAnonymousSid))
			return true;
		
		return false;
	}
}
