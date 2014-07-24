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

import com.sun.jna.platform.win32.Secur32Util;
import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsAuthProvider implements IWindowsAuthProvider {

    private List<String> _groups = new ArrayList<String>();

    public MockWindowsAuthProvider() {
        _groups.add("Users");
        _groups.add("Everyone");
    }

    public void addGroup(String name) {
        _groups.add(name);
    }

    @Override
    public IWindowsSecurityContext acceptSecurityToken(String connectionId, byte[] token, String securityPackage) {

        return new MockWindowsSecurityContext(new String(token));
    }

    @Override
    public IWindowsComputer getCurrentComputer() {
        return null;
    }

    @Override
    public IWindowsDomain[] getDomains() {
        return new IWindowsDomain[0];
    }

    @Override
    public IWindowsIdentity logonDomainUser(String username, String domain, String password) {
        return null;
    }

    @Override
    public IWindowsIdentity logonDomainUserEx(String username, String domain, String password, int logonType,
            int logonProvider) {
        return null;
    }

    /**
     * Will login the current user with any password. Will logon a "Guest" user as guest.
     */
    @Override
    public IWindowsIdentity logonUser(String username, String password) {
        String currentUsername = Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible);
        if (username.equals(currentUsername)) {
            return new MockWindowsIdentity(currentUsername, _groups);
        } else if (username.equals("Guest")) {
            return new MockWindowsIdentity("Guest", _groups);
        } else {
            throw new RuntimeException("Mock error: " + username);
        }
    }

    @Override
    public IWindowsAccount lookupAccount(String username) {
        return null;
    }

    @Override
    public void resetSecurityToken(String connectionId) {
        // Do Nothing
    }
}
