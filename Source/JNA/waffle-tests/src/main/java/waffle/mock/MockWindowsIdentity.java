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

    /** The fqn. */
    private final String fqn;

    /** The groups. */
    private final List<String> groups;

    /**
     * Instantiates a new mock windows identity.
     *
     * @param newFqn
     *            the new fqn
     * @param newGroups
     *            the new groups
     */
    public MockWindowsIdentity(final String newFqn, final List<String> newGroups) {
        this.fqn = newFqn;
        this.groups = newGroups;
    }

    @Override
    public String getFqn() {
        return this.fqn;
    }

    @Override
    public IWindowsAccount[] getGroups() {
        final List<MockWindowsAccount> groupsList = new ArrayList<>();
        for (final String group : this.groups) {
            groupsList.add(new MockWindowsAccount(group));
        }
        return groupsList.toArray(new IWindowsAccount[0]);
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
        return "Guest".equals(this.fqn);
    }

    @Override
    public IWindowsImpersonationContext impersonate() {
        return new MockWindowsImpersonationContext();
    }

}
