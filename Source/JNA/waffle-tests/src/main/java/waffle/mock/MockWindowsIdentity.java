/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
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
    private final String       fqn;

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

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsIdentity#getFqn()
     */
    @Override
    public String getFqn() {
        return this.fqn;
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsIdentity#getGroups()
     */
    @Override
    public IWindowsAccount[] getGroups() {
        final List<MockWindowsAccount> groupsList = new ArrayList<>();
        for (final String group : this.groups) {
            groupsList.add(new MockWindowsAccount(group));
        }
        return groupsList.toArray(new IWindowsAccount[0]);
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsIdentity#getSid()
     */
    @Override
    public byte[] getSid() {
        return new byte[0];
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsIdentity#getSidString()
     */
    @Override
    public String getSidString() {
        return "S-" + this.fqn.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsIdentity#dispose()
     */
    @Override
    public void dispose() {
        // Do Nothing
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsIdentity#isGuest()
     */
    @Override
    public boolean isGuest() {
        return this.fqn.equals("Guest");
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsIdentity#impersonate()
     */
    @Override
    public IWindowsImpersonationContext impersonate() {
        return new MockWindowsImpersonationContext();
    }
}
