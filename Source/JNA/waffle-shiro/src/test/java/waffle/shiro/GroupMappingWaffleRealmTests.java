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
package waffle.shiro;

import java.util.Collections;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.platform.win32.Secur32Util;

import waffle.mock.MockWindowsAuthProvider;

/**
 * The Class GroupMappingWaffleRealmTests.
 */
public class GroupMappingWaffleRealmTests {

    /** The Constant ROLE_NAME. */
    private static final String     ROLE_NAME = "ShiroUsers";

    /** The windows auth provider. */
    private MockWindowsAuthProvider windowsAuthProvider;

    /** The realm. */
    private GroupMappingWaffleRealm realm;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        this.windowsAuthProvider = new MockWindowsAuthProvider();
        this.realm = new GroupMappingWaffleRealm();
        this.realm.setProvider(this.windowsAuthProvider);
        this.realm.setGroupRolesMap(Collections.singletonMap("Users", GroupMappingWaffleRealmTests.ROLE_NAME));
    }

    /**
     * Test valid username password.
     */
    @Test
    public void testValidUsernamePassword() {
        final AuthenticationToken token = new UsernamePasswordToken(this.getCurrentUserName(), "somePassword");
        final AuthenticationInfo authcInfo = this.realm.getAuthenticationInfo(token);
        final PrincipalCollection principals = authcInfo.getPrincipals();
        Assert.assertFalse(principals.isEmpty());
        final Object primaryPrincipal = principals.getPrimaryPrincipal();
        Assert.assertNotNull(primaryPrincipal);
        Assertions.assertThat(primaryPrincipal).isInstanceOf(WaffleFqnPrincipal.class);
        final WaffleFqnPrincipal fqnPrincipal = (WaffleFqnPrincipal) primaryPrincipal;
        Assertions.assertThat(fqnPrincipal.getFqn()).isEqualTo(this.getCurrentUserName());
        Assertions.assertThat(fqnPrincipal.getGroupFqns()).contains("Users", "Everyone");
        final Object credentials = authcInfo.getCredentials();
        Assertions.assertThat(credentials).isInstanceOf(char[].class);
        Assertions.assertThat(credentials).isEqualTo("somePassword".toCharArray());
        Assert.assertTrue(this.realm.hasRole(principals, GroupMappingWaffleRealmTests.ROLE_NAME));
    }

    /**
     * Test invalid username password.
     */
    @Test(expected = AuthenticationException.class)
    public void testInvalidUsernamePassword() {
        final AuthenticationToken token = new UsernamePasswordToken("InvalidUser", "somePassword");
        this.realm.getAuthenticationInfo(token);
    }

    /**
     * Test guest username password.
     */
    @Test(expected = AuthenticationException.class)
    public void testGuestUsernamePassword() {
        final AuthenticationToken token = new UsernamePasswordToken("Guest", "somePassword");
        this.realm.getAuthenticationInfo(token);
    }

    /**
     * Gets the current user name.
     *
     * @return the current user name
     */
    private String getCurrentUserName() {
        return Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible);
    }
}
