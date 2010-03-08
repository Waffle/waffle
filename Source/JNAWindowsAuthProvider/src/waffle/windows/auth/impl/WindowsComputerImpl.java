package waffle.windows.auth.impl;

import waffle.windows.auth.IWindowsComputer;

import com.sun.jna.contrib.win32.w32util.Netapi32Util;

public class WindowsComputerImpl implements IWindowsComputer {

	private String computerName;
	private String domainName;
	
	@Override
	public String getComputerName() {
		return computerName;
	}

	@Override
	public String[] getGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinStatus() {
		// TODO
		return null;
	}

	@Override
	public String getMemberOf() {
		return domainName;
	}
	
	public WindowsComputerImpl(String computerName) {
		this.computerName = computerName;
		this.domainName = Netapi32Util.getDomainName(computerName);
	}
}
