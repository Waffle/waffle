package waffle.windows.auth.impl;

import waffle.windows.auth.IWindowsAccount;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Secur32Util;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;

public class WindowsAccountImpl implements IWindowsAccount {

	private Account _account;
	
	@Override
	public String getFqn() {
		return _account.fqn;
	}

	@Override
	public String getSidString() {
		return _account.sidString;
	}
		
	@Override
	public String getName() {
		return _account.name;
	}

	@Override
	public String getDomain() {
		return _account.domain;
	}
	
	/**
	 * Get the SAM-compatible username of the currently logged-on user.
	 * @return
	 */
	public static String getCurrentUsername() {
		return Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible);
	}
	
	public WindowsAccountImpl(String userName) {
		this(userName, null);
	}
	
	public WindowsAccountImpl(String accountName, String systemName) {
		this(Advapi32Util.getAccountByName(systemName, accountName));
	}
	
	public WindowsAccountImpl(Account account) {
		_account = account;
	}
}
