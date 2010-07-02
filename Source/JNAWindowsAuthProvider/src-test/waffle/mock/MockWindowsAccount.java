/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.mock;

import waffle.windows.auth.IWindowsAccount;

/**
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsAccount implements IWindowsAccount {

	private String _fqn;
	private String _name;
	private String _domain;
	
	public MockWindowsAccount(String fqn) {
		_fqn = fqn;
        String[] userNameDomain = fqn.split("\\\\", 2);
        if (userNameDomain.length == 2) {
            _name = userNameDomain[1];
            _domain = userNameDomain[0];
        } else {
        	_name = fqn;        	
        }
	}
	
	public String getDomain() {
		return _domain;
	}

	public String getFqn() {
		return _fqn;
	}

	public String getName() {
		return _name;
	}

	public String getSidString() {
		return "S-" + _fqn.hashCode();
	}
}
