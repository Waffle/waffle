/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
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
