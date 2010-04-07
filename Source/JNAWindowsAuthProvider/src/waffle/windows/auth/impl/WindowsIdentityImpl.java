package waffle.windows.auth.impl;

import java.util.ArrayList;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.W32API.HANDLE;

public class WindowsIdentityImpl implements IWindowsIdentity {

	private Account[] userGroups;
	private Account windowsAccount;
	
	@Override
	public String getFqn() {
		return windowsAccount.fqn;
	}

	@Override
	public IWindowsAccount[] getGroups() {
		ArrayList<IWindowsAccount> result = new ArrayList<IWindowsAccount>(userGroups.length);
		for(Account userGroup : userGroups) {
			WindowsAccountImpl account = new WindowsAccountImpl(userGroup);
			result.add(account);			
		}
		return result.toArray(new IWindowsAccount[0]);
	}

	@Override
	public byte[] getSid() {
		return windowsAccount.sid;
	}

	@Override
	public String getSidString() {
		return windowsAccount.sidString;
	}

	public WindowsIdentityImpl(HANDLE windowsIdentity) {
		// don't keep the handle, it may be closed by the caller
		userGroups = Advapi32Util.getTokenGroups(windowsIdentity);
		windowsAccount = Advapi32Util.getTokenAccount(windowsIdentity);
	}
}
