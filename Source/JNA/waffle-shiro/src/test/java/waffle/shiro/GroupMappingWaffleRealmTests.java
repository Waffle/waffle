/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
 * The Class GroupMappingWaffleRealmTests.
 */
public class GroupMappingWaffleRealmTests {

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
        this.realm.setGroupRolesMap(Collections.singletonMap("Users", GroupMappingWaffleRealmTests.ROLE_NAME));
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
        assertThat(credentials).isInstanceOf(char[].class);
        assertThat(credentials).isEqualTo("somePassword".toCharArray());
        Assertions.assertTrue(this.realm.hasRole(principals, GroupMappingWaffleRealmTests.ROLE_NAME));
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
