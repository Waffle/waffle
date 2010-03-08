package waffle.windows.auth.impl;

import waffle.windows.auth.IWindowsAccount;

import com.sun.jna.contrib.win32.w32util.Advapi32Util;
import com.sun.jna.contrib.win32.w32util.Secur32Util;
import com.sun.jna.examples.win32.Secur32.EXTENDED_NAME_FORMAT;

public class WindowsAccountImpl implements IWindowsAccount {

	private String accountName;
	private String sidString;
	private String fqn;
	
	@Override
	public String getFqn() {
		return fqn;
	}

	@Override
	public String getSidString() {
		return sidString;
	}
		
	public String getAccountName() {
		return accountName;
	}
	
	/**
	 * Get the SAM-compatible username of the currently logged-on user.
	 * @return
	 * @throws Exception 
	 */
	public static String getCurrentUsername() {
		return Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible);
	}
	
	public WindowsAccountImpl(String userName) throws Exception {
		this(userName, "");
	}
	
	public WindowsAccountImpl(String accountName, String systemName) throws Exception {
		sidString = Advapi32Util.getAccountSidString(systemName, accountName);
		fqn = Advapi32Util.getAccountName(sidString);
		
        String[] accountNamePartsBs = accountName.split("\\\\", 2);
        String[] accountNamePartsAt = accountName.split("@", 2);

        if (accountNamePartsBs.length == 2)
        	this.accountName = accountNamePartsBs[1];
        else if (accountNamePartsAt.length == 2)
        	this.accountName = accountNamePartsAt[0];
        else 
            this.accountName = accountName;
	}
}
