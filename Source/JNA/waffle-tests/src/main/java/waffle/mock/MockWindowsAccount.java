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
package waffle.mock;

import waffle.windows.auth.IWindowsAccount;

/**
 * The Class MockWindowsAccount.
 *
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsAccount implements IWindowsAccount {

    /** The Constant TEST_USER_NAME. */
    public static final String TEST_USER_NAME = "WaffleTestUser";

    /** The Constant TEST_PASSWORD. */
    public static final String TEST_PASSWORD = "!WAFFLEP$$Wrd0";

    /** The fqn. */
    private final String fqn;

    /** The name. */
    private String name;

    /** The domain. */
    private String domain;

    /** The sid. */
    private final String sid;

    /**
     * Instantiates a new mock windows account.
     *
     * @param newFqn
     *            the new fqn
     */
    public MockWindowsAccount(final String newFqn) {
        this(newFqn, "S-" + newFqn.hashCode());
    }

    /**
     * Instantiates a new mock windows account.
     *
     * @param newFqn
     *            the new fqn
     * @param newSid
     *            the new sid
     */
    public MockWindowsAccount(final String newFqn, final String newSid) {
        this.fqn = newFqn;
        this.sid = newSid;
        final String[] userNameDomain = newFqn.split("\\\\", 2);
        if (userNameDomain.length == 2) {
            this.name = userNameDomain[1];
            this.domain = userNameDomain[0];
        } else {
            this.name = newFqn;
        }
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override
    public String getFqn() {
        return this.fqn;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getSidString() {
        return this.sid;
    }

}
