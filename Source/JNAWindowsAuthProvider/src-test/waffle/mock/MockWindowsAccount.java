/*******************************************************************************
* Waffle (http://waffle.codeplex.com)
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

import waffle.windows.auth.IWindowsAccount;

/**
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsAccount implements IWindowsAccount {

	public static final String TEST_USER_NAME = "WaffleTestUser";
	public static final String TEST_PASSWORD = "!WAFFLEP$$Wrd0";
	
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
