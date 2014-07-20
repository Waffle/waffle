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

import waffle.windows.auth.IWindowsComputer;

import com.sun.jna.platform.win32.LMJoin;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Netapi32Util.LocalGroup;

/**
 * Windows Computer.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class WindowsComputerImpl implements IWindowsComputer {

	private String	computerName;
	private String	domainName;

	@Override
	public String getComputerName() {
		return computerName;
	}

	@Override
	public String[] getGroups() {
		List<String> groupNames = new ArrayList<String>();
		LocalGroup[] groups = Netapi32Util.getLocalGroups(computerName);
		for (LocalGroup group : groups) {
			groupNames.add(group.name);
		}
		return groupNames.toArray(new String[0]);
	}

	@Override
	public String getJoinStatus() {
		int joinStatus = Netapi32Util.getJoinStatus(computerName);
		switch (joinStatus) {
			case LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName:
				return "NetSetupDomainName";
			case LMJoin.NETSETUP_JOIN_STATUS.NetSetupUnjoined:
				return "NetSetupUnjoined";
			case LMJoin.NETSETUP_JOIN_STATUS.NetSetupWorkgroupName:
				return "NetSetupWorkgroupName";
			case LMJoin.NETSETUP_JOIN_STATUS.NetSetupUnknownStatus:
				return "NetSetupUnknownStatus";
			default:
				throw new RuntimeException("Unsupported join status: " + joinStatus);
		}
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
