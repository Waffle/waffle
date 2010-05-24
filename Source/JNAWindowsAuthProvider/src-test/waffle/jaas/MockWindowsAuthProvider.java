/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import java.util.ArrayList;
import java.util.List;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.impl.WindowsAccountImpl;

public class MockWindowsAuthProvider implements IWindowsAuthProvider {

	@Override
	public IWindowsSecurityContext acceptSecurityToken(String connectionId,
			byte[] token, String securityPackage) {
		return null;
	}

	@Override
	public IWindowsComputer getCurrentComputer() {
		return null;
	}

	@Override
	public IWindowsDomain[] getDomains() {
		return null;
	}

	@Override
	public IWindowsIdentity logonDomainUser(String username, String domain,
			String password) {
		return null;
	}

	@Override
	public IWindowsIdentity logonDomainUserEx(String username, String domain,
			String password, int logonType, int logonProvider) {
		return null;
	}

	/**
	 * Will login the current user with any password.
	 */
	@Override
	public IWindowsIdentity logonUser(String username, String password) {
		String currentUsername = WindowsAccountImpl.getCurrentUsername(); 
		if (username.equals(currentUsername)) {
			List<String> groups = new ArrayList<String>();
			groups.add("Users");
			groups.add("Everyone");
			return new MockWindowsIdentity(currentUsername, groups);
		}
		return null;
	}

	@Override
	public IWindowsAccount lookupAccount(String username) {
		return null;
	}

	@Override
	public void resetSecurityToken(String connectionId) {		
	}
}
