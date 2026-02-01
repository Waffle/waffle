/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.shiro;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.platform.win32.Secur32Util;

import java.util.Collections;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.mock.MockWindowsAuthProvider;

/**
 * The Class GroupMappingWaffleRealmTest.
 */
class GroupMappingWaffleRealmTest {

    /** The Constant ROLE_NAME. */
    private static final String ROLE_NAME = "ShiroUsers";

    /** The windows auth provider. */
    private MockWindowsAuthProvider windowsAuthProvider;

    /** The realm. */
    private GroupMappingWaffleRealm realm;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.windowsAuthProvider = new MockWindowsAuthProvider();
        this.realm = new GroupMappingWaffleRealm();
        this.realm.setProvider(this.windowsAuthProvider);
        this.realm.setGroupRolesMap(Collections.singletonMap("Users", GroupMappingWaffleRealmTest.ROLE_NAME));
    }

    /**
     * Test valid username password.
     */
    @Test
    void testValidUsernamePassword() {
        final AuthenticationToken token = new UsernamePasswordToken(this.getCurrentUserName(), "somePassword");
        final AuthenticationInfo authcInfo = this.realm.getAuthenticationInfo(token);
        final PrincipalCollection principals = authcInfo.getPrincipals();
        Assertions.assertFalse(principals.isEmpty());
        final Object primaryPrincipal = principals.getPrimaryPrincipal();
        Assertions.assertNotNull(primaryPrincipal);
        assertThat(primaryPrincipal).isInstanceOf(WaffleFqnPrincipal.class);
        final WaffleFqnPrincipal fqnPrincipal = (WaffleFqnPrincipal) primaryPrincipal;
        assertThat(fqnPrincipal.getFqn()).isEqualTo(this.getCurrentUserName());
        assertThat(fqnPrincipal.getGroupFqns()).contains("Users", "Everyone");
        final Object credentials = authcInfo.getCredentials();
        assertThat(credentials).isInstanceOf(char[].class).isEqualTo("somePassword".toCharArray());
        Assertions.assertTrue(this.realm.hasRole(principals, GroupMappingWaffleRealmTest.ROLE_NAME));
    }

    /**
     * Test invalid username password.
     */
    @Test
    void testInvalidUsernamePassword() {
        final AuthenticationToken token = new UsernamePasswordToken("InvalidUser", "somePassword");
        Assertions.assertThrows(AuthenticationException.class, () -> {
            this.realm.getAuthenticationInfo(token);
        });
    }

    /**
     * Test guest username password.
     */
    @Test
    void testGuestUsernamePassword() {
        final AuthenticationToken token = new UsernamePasswordToken("Guest", "somePassword");
        Assertions.assertThrows(AuthenticationException.class, () -> {
            this.realm.getAuthenticationInfo(token);
        });
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
