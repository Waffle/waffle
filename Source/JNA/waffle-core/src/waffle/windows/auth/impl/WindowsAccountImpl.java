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

import waffle.windows.auth.IWindowsAccount;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Secur32Util;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;

/**
 * Windows Account.
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccountImpl implements IWindowsAccount {

	private Account _account;
	
	@Override
	public String getFqn() {
		return _account.fqn;
	}

	@Override
	public String getSidString() {
		return _account.sidString;
	}
		
	/**
	 * Account name.
	 */
	@Override
	public String getName() {
		return _account.name;
	}

	/**
	 * Account domain.
	 */
	@Override
	public String getDomain() {
		return _account.domain;
	}
	
	/**
	 * Get the SAM-compatible username of the currently logged-on user.
	 * @return
	 *  String.
	 */
	public static String getCurrentUsername() {
		return Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible);
	}
	
	/**
	 * Windows Account.
	 * @param userName
	 *  Fully qualified username.
	 */
	public WindowsAccountImpl(String userName) {
		this(userName, null);
	}
	
	/**
	 * Windows Account
	 * @param accountName
	 *  Username, without a domain or machine.
	 * @param systemName
	 *  Machine name.
	 */
	public WindowsAccountImpl(String accountName, String systemName) {
		this(Advapi32Util.getAccountByName(systemName, accountName));
	}

	/**
	 * Windows Account.
	 * @param account
	 *  Account.
	 */
	public WindowsAccountImpl(Account account) {
		_account = account;
	}
}
