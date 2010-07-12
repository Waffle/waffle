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
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * A Mock windows identity.
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsIdentity implements IWindowsIdentity {

	private String _fqn;
	private List<String> _groups;

	public MockWindowsIdentity(String fqn, List<String> groups) {
		_fqn = fqn;
		_groups = groups;
	}
	
	public String getFqn() {
		return _fqn;
	}

	public IWindowsAccount[] getGroups() {
		List<MockWindowsAccount> groups = new ArrayList<MockWindowsAccount>();
		for(String group : _groups) {
			groups.add(new MockWindowsAccount(group));
		}
		return groups.toArray(new IWindowsAccount[0]);
	}

	public byte[] getSid() {
		return null;
	}

	public String getSidString() {
		return "S-" + _fqn.hashCode();
	}
	
	public void dispose() {
		
	}
	
	public boolean isGuest() {
		return _fqn.equals("Guest");
	}
	
	public IWindowsImpersonationContext impersonate() {
		return new MockWindowsImpersonationContext();
	}
}
