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
package waffle.mock;

import java.util.ArrayList;
import java.util.List;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * A Mock windows identity.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsIdentity implements IWindowsIdentity {

	private String _fqn;
	private List<String> _groups;

	public MockWindowsIdentity(String fqn, List<String> groups) {
		_fqn = fqn;
		_groups = groups;
	}

	@Override
	public String getFqn() {
		return _fqn;
	}

	@Override
	public IWindowsAccount[] getGroups() {
		List<MockWindowsAccount> groups = new ArrayList<MockWindowsAccount>();
		for (String group : _groups) {
			groups.add(new MockWindowsAccount(group));
		}
		return groups.toArray(new IWindowsAccount[0]);
	}

	@Override
	public byte[] getSid() {
		return null;
	}

	@Override
	public String getSidString() {
		return "S-" + _fqn.hashCode();
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isGuest() {
		return _fqn.equals("Guest");
	}

	@Override
	public IWindowsImpersonationContext impersonate() {
		return new MockWindowsImpersonationContext();
	}
}
