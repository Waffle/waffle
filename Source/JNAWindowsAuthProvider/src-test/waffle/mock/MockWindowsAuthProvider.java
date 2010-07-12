/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.mock;

import java.util.ArrayList;
import java.util.List;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsAuthProvider implements IWindowsAuthProvider {

	private List<String> _groups = new ArrayList<String>();
	
	public MockWindowsAuthProvider() {
		_groups.add("Users");
		_groups.add("Everyone");		
	}
	
	public void addGroup(String name) {
		_groups.add(name);
	}
	
	public IWindowsSecurityContext acceptSecurityToken(String connectionId,
			byte[] token, String securityPackage) {
		
		return new MockWindowsSecurityContext(new String(token));
	}

	public IWindowsComputer getCurrentComputer() {
		return null;
	}

	public IWindowsDomain[] getDomains() {
		return null;
	}

	public IWindowsIdentity logonDomainUser(String username, String domain,
			String password) {
		return null;
	}

	public IWindowsIdentity logonDomainUserEx(String username, String domain,
			String password, int logonType, int logonProvider) {
		return null;
	}

	/**
	 * Will login the current user with any password.
	 * Will logon a "Guest" user as guest.
	 */
	public IWindowsIdentity logonUser(String username, String password) {
		String currentUsername = WindowsAccountImpl.getCurrentUsername(); 
		if (username.equals(currentUsername)) {
			return new MockWindowsIdentity(currentUsername, _groups);
		} else if (username.equals("Guest")) {
			MockWindowsIdentity identity = new MockWindowsIdentity("Guest", _groups);
			return identity;
		} else {
			throw new RuntimeException("Mock error: " + username);
		}
	}

	public IWindowsAccount lookupAccount(String username) {
		return null;
	}

	public void resetSecurityToken(String connectionId) {		
		
	}
}
