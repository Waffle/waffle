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
package waffle.shiro;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import waffle.mock.MockWindowsAuthProvider;

import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.platform.win32.Secur32Util;

public class GroupMappingWaffleRealmTests {
    private static final String     ROLE_NAME = "ShiroUsers";
    private MockWindowsAuthProvider windowsAuthProvider;
    private GroupMappingWaffleRealm realm;

    @Before
    public void setUp() {
        this.windowsAuthProvider = new MockWindowsAuthProvider();
        this.realm = new GroupMappingWaffleRealm();
        this.realm.setProvider(this.windowsAuthProvider);
        this.realm.setGroupRolesMap(Collections.singletonMap("Users", ROLE_NAME));
    }

    @Test
    public void testValidUsernamePassword() {
        AuthenticationToken token = new UsernamePasswordToken(getCurrentUserName(), "somePassword");
        AuthenticationInfo authcInfo = this.realm.getAuthenticationInfo(token);
        PrincipalCollection principals = authcInfo.getPrincipals();
        assertFalse(principals.isEmpty());
        Object primaryPrincipal = principals.getPrimaryPrincipal();
        assertThat(primaryPrincipal, instanceOf(WaffleFqnPrincipal.class));
        WaffleFqnPrincipal fqnPrincipal = (WaffleFqnPrincipal) primaryPrincipal;
        assertThat(fqnPrincipal.getFqn(), equalTo(getCurrentUserName()));
        assertThat(fqnPrincipal.getGroupFqns(), CoreMatchers.hasItems("Users", "Everyone"));
        Object credentials = authcInfo.getCredentials();
        assertThat(credentials, instanceOf(char[].class));
        assertThat((char[]) credentials, equalTo("somePassword".toCharArray()));
        assertTrue(this.realm.hasRole(principals, ROLE_NAME));
    }

    @Test(expected = AuthenticationException.class)
    public void testInvalidUsernamePassword() {
        AuthenticationToken token = new UsernamePasswordToken("InvalidUser", "somePassword");
        this.realm.getAuthenticationInfo(token);
    }

    @Test(expected = AuthenticationException.class)
    public void testGuestUsernamePassword() {
        AuthenticationToken token = new UsernamePasswordToken("Guest", "somePassword");
        this.realm.getAuthenticationInfo(token);
    }

    private String getCurrentUserName() {
        return Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible);
    }
}
