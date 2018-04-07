/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.platform.win32.Secur32Util;

import waffle.windows.auth.IWindowsAccount;

/**
 * Windows Account.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccountImpl implements IWindowsAccount {

    /** The account. */
    private final Account account;

    /**
     * Windows Account.
     *
     * @param newAccount
     *            Account.
     */
    public WindowsAccountImpl(final Account newAccount) {
        this.account = newAccount;
    }

    /**
     * Windows Account.
     *
     * @param userName
     *            Fully qualified username.
     */
    public WindowsAccountImpl(final String userName) {
        this(userName, null);
    }

    /**
     * Windows Account.
     *
     * @param accountName
     *            Username, without a domain or machine.
     * @param systemName
     *            Machine name.
     */
    public WindowsAccountImpl(final String accountName, final String systemName) {
        this(Advapi32Util.getAccountByName(systemName, accountName));
    }

    /**
     * Get the SAM-compatible username of the currently logged-on user.
     *
     * @return String.
     */
    public static String getCurrentUsername() {
        return Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible);
    }

    /**
     * Account domain.
     *
     * @return String.
     */
    @Override
    public String getDomain() {
        return this.account.domain;
    }

    @Override
    public String getFqn() {
        return this.account.fqn;
    }

    /**
     * Account name.
     *
     * @return String.
     */
    @Override
    public String getName() {
        return this.account.name;
    }

    @Override
    public String getSidString() {
        return this.account.sidString;
    }

}
