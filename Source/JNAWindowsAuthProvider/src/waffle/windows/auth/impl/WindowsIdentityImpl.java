package waffle.windows.auth.impl;

import java.util.ArrayList;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.Advapi32Util.Group;
import com.sun.jna.platform.win32.W32API.HANDLE;

import waffle.windows.auth.IWindowsIdentity;

public class WindowsIdentityImpl implements IWindowsIdentity {

	private Group[] userGroups;
	private Account windowsAccount;
	
	@Override
	public String getFqn() {
		return windowsAccount.fqn;
	}

	@Override
	public String[] getGroups() {
		ArrayList<String> result = new ArrayList<String>(userGroups.length);
		for(Group userGroup : userGroups) {
			result.add(userGroup.name);			
		}
		return result.toArray(new String[0]);
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
