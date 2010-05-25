/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import waffle.windows.auth.IWindowsAccount;

/**
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsAccount implements IWindowsAccount {

	private String _fqn;
	
	public MockWindowsAccount(String fqn) {
		_fqn = fqn;
	}
	
	@Override
	public String getDomain() {
		return null;
	}

	@Override
	public String getFqn() {
		return _fqn;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getSidString() {
		return "S-" + _fqn.hashCode();
	}
}
