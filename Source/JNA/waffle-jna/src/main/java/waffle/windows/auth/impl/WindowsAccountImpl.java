/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
