/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock;

import waffle.windows.auth.IWindowsAccount;

/**
 * The Class MockWindowsAccount.
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
