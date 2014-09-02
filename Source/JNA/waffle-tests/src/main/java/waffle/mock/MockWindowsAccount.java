/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.mock;

import waffle.windows.auth.IWindowsAccount;

/**
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsAccount implements IWindowsAccount {

    public static final String TEST_USER_NAME = "WaffleTestUser";
    public static final String TEST_PASSWORD  = "!WAFFLEP$$Wrd0";

    private String             fqn;
    private String             name;
    private String             domain;
    private String             sid;

    public MockWindowsAccount(final String newFqn) {
        this(newFqn, "S-" + newFqn.hashCode());
    }

    public MockWindowsAccount(final String newFqn, final String newSid) {
        this.fqn = newFqn;
        this.sid = newSid;
        String[] userNameDomain = newFqn.split("\\\\", 2);
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
