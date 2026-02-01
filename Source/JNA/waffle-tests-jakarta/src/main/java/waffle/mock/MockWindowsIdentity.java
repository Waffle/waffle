/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock;

import java.util.ArrayList;
import java.util.List;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * A Mock windows identity.
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
