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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.platform.win32.Secur32Util;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * The Class MockWindowsAuthProvider.
 *
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsAuthProvider implements IWindowsAuthProvider {

    /** The Constant GUEST. */
    private static final String GUEST  = "Guest";

    /** The groups. */
    private final List<String>  groups = new ArrayList<>();

    /**
     * Instantiates a new mock windows auth provider.
     */
    public MockWindowsAuthProvider() {
        this.groups.add("Users");
        this.groups.add("Everyone");
    }

    /**
     * Adds the group.
     *
     * @param name
     *            the name
     */
    public void addGroup(final String name) {
        this.groups.add(name);
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#acceptSecurityToken(java.lang.String, byte[], java.lang.String)
     */
    @Override
    public IWindowsSecurityContext acceptSecurityToken(final String connectionId, final byte[] token,
            final String securityPackage) {
        return new MockWindowsSecurityContext(new String(token, StandardCharsets.UTF_8));
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#getCurrentComputer()
     */
    @Override
    public IWindowsComputer getCurrentComputer() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#getDomains()
     */
    @Override
    public IWindowsDomain[] getDomains() {
        return new IWindowsDomain[0];
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#logonDomainUser(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public IWindowsIdentity logonDomainUser(final String username, final String domain, final String password) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#logonDomainUserEx(java.lang.String, java.lang.String,
     * java.lang.String, int, int)
     */
    @Override
    public IWindowsIdentity logonDomainUserEx(final String username, final String domain, final String password,
            final int logonType, final int logonProvider) {
        return null;
    }

    /**
     * Will login the current user with any password. Will logon a "Guest" user as guest.
     *
     * @param username
     *            the username
     * @param password
     *            the password
     * @return the i windows identity
     */
    @Override
    public IWindowsIdentity logonUser(final String username, final String password) {
        final String currentUsername = Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible);
        if (username.equals(currentUsername)) {
            return new MockWindowsIdentity(currentUsername, this.groups);
        } else if (username.equals(MockWindowsAuthProvider.GUEST)) {
            return new MockWindowsIdentity(MockWindowsAuthProvider.GUEST, this.groups);
        } else {
            throw new RuntimeException("Mock error: " + username);
        }
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#lookupAccount(java.lang.String)
     */
    @Override
    public IWindowsAccount lookupAccount(final String username) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsAuthProvider#resetSecurityToken(java.lang.String)
     */
    @Override
    public void resetSecurityToken(final String connectionId) {
        // Do Nothing
    }
}
