/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth.impl;

import java.util.ArrayList;

import waffle.windows.auth.IWindowsComputer;

import com.sun.jna.platform.win32.LMJoin;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Netapi32Util.LocalGroup;

/**
 * Windows Computer.
 * @author dblock[at]dblock[dot]org
 */
public class WindowsComputerImpl implements IWindowsComputer {

	private String computerName;
	private String domainName;
	
	public String getComputerName() {
		return computerName;
	}

	public String[] getGroups() {
		ArrayList<String> groupNames = new ArrayList<String>();
		LocalGroup[] groups = Netapi32Util.getLocalGroups(computerName);
		for(LocalGroup group : groups) {
			groupNames.add(group.name);
		}
		return groupNames.toArray(new String[0]);
	}

	public String getJoinStatus() {
		int joinStatus = Netapi32Util.getJoinStatus(computerName);
		switch(joinStatus) {
		case LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName:
			return "NetSetupDomainName";
		case LMJoin.NETSETUP_JOIN_STATUS.NetSetupUnjoined:
			return "NetSetupUnjoined";
		case LMJoin.NETSETUP_JOIN_STATUS.NetSetupWorkgroupName:
			return "NetSetupWorkgroupName";
		case LMJoin.NETSETUP_JOIN_STATUS.NetSetupUnknownStatus:
			return "NetSetupUnknownStatus";
		}
		throw new RuntimeException("Unsupported join status: " + joinStatus);
	}

	public String getMemberOf() {
		return domainName;
	}
	
	public WindowsComputerImpl(String computerName) {
		this.computerName = computerName;
		this.domainName = Netapi32Util.getDomainName(computerName);
	}
}
