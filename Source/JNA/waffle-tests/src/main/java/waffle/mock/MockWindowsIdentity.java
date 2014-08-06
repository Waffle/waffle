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

import java.util.ArrayList;
import java.util.List;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * A Mock windows identity.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsIdentity implements IWindowsIdentity {

    private String       fqn;
    private List<String> groups;

    public MockWindowsIdentity(final String fqn, final List<String> groups) {
        this.fqn = fqn;
        this.groups = groups;
    }

    @Override
    public String getFqn() {
        return this.fqn;
    }

    @Override
    public IWindowsAccount[] getGroups() {
        final List<MockWindowsAccount> groups = new ArrayList<MockWindowsAccount>();
        for (String group : this.groups) {
            groups.add(new MockWindowsAccount(group));
        }
        return groups.toArray(new IWindowsAccount[0]);
    }

    @Override
    public byte[] getSid() {
        return new byte[0];
    }

    @Override
    public String getSidString() {
        return "S-" + this.fqn.hashCode();
    }

    @Override
    public void dispose() {
        // Do Nothing
    }

    @Override
    public boolean isGuest() {
        return this.fqn.equals("Guest");
    }

    @Override
    public IWindowsImpersonationContext impersonate() {
        return new MockWindowsImpersonationContext();
    }
}
