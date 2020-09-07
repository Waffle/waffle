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
package waffle.jaas;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.mock.MockWindowsAuthProvider;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * The Class WindowsLoginModuleTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsLoginModuleTests {

    /** The login module. */
    WindowsLoginModule loginModule;

    /** The provider. */
    MockWindowsAuthProvider provider;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.provider = new MockWindowsAuthProvider();
        this.loginModule = new WindowsLoginModule();
        this.loginModule.setAuth(this.provider);
    }

    /**
     * Test initialize.
     */
    @Test
    void testInitialize() {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("", "");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assertions.assertTrue(this.loginModule.isDebug());
    }

    /**
     * Test get set auth.
     */
    @Test
    void testGetSetAuth() {
        Assertions.assertNotNull(this.loginModule.getAuth());
        this.loginModule.setAuth(null);
        Assertions.assertNull(this.loginModule.getAuth());
    }

    /**
     * Test login.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testLogin() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertEquals(0, subject.getPrincipals().size());
        Assertions.assertTrue(this.loginModule.commit());
        Assertions.assertEquals(2, subject.getPrincipals().size());
        Assertions.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                Assertions.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Everyone")));
                Assertions.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Users")));
            }
        }
        Assertions.assertTrue(
                subject.getPrincipals().contains(new UserPrincipal(WindowsAccountImpl.getCurrentUsername())));
        Assertions.assertTrue(this.loginModule.logout());
        Assertions.assertSame(Integer.valueOf(subject.getPrincipals().size()), Integer.valueOf(0));
    }

    /**
     * Test no callback handler.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testNoCallbackHandler() throws LoginException {
        final Subject subject = new Subject();
        final Map<String, String> options = new HashMap<>();
        this.loginModule.initialize(subject, null, null, options);
        Assertions.assertThrows(LoginException.class, () -> {
            this.loginModule.login();
        });
    }

    /**
     * Test login no username.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testLoginNoUsername() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("", "");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        final Throwable exception = Assertions.assertThrows(LoginException.class, () -> {
            Assertions.assertFalse(this.loginModule.login());
        });
        Assertions.assertEquals("Mock error: ", exception.getMessage());
    }

    /**
     * Test role format none.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testRoleFormatNone() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        options.put("roleFormat", "none");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());
        Assertions.assertEquals(1, subject.getPrincipals().size());
    }

    /**
     * Test role format both.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testRoleFormatBoth() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        options.put("roleFormat", "both");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());

        Assertions.assertEquals(2, subject.getPrincipals().size());
        Assertions.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                int size = 0;
                int sidSize = 0;
                final List<? extends Principal> groupPrincipals = Collections
                        .list(((GroupPrincipal) principal).members());
                for (Principal groupPrincipal : groupPrincipals) {
                    if (groupPrincipal.getName().startsWith("S-")) {
                        sidSize++;
                    }
                    size++;
                }
                Assertions.assertEquals(4, size);
                Assertions.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Everyone")));
                Assertions.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Users")));
                Assertions.assertEquals(2, sidSize);
            }
        }
    }

    /**
     * Test principal format both.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testPrincipalFormatBoth() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        options.put("principalFormat", "both");
        options.put("roleFormat", "none");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());
        Assertions.assertEquals(2, subject.getPrincipals().size());
    }

    /**
     * Test role format sid.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testRoleFormatSid() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        options.put("roleFormat", "sid");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());

        Assertions.assertEquals(2, subject.getPrincipals().size());
        Assertions.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                int size = 0;
                final List<? extends Principal> groupPrincipals = Collections
                        .list(((GroupPrincipal) principal).members());
                for (Principal groupPrincipal : groupPrincipals) {
                    if (groupPrincipal.getName().startsWith("S-")) {
                        size++;
                    }
                }
                Assertions.assertEquals(2, size);
            } else {
                Assertions.assertTrue(principal.getName().equals(WindowsAccountImpl.getCurrentUsername()));
            }
        }
    }

    /**
     * Test role unique.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testRoleUnique() throws LoginException {
        final Subject subject = new Subject();
        // the mock has an "Everyone" group
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        this.provider.addGroup("Group 1");
        this.provider.addGroup("Group 1");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());

        Assertions.assertEquals(2, subject.getPrincipals().size());
        Assertions.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                int size = Collections.list(((GroupPrincipal) principal).members()).size();
                Assertions.assertEquals(3, size);
                Assertions.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Everyone")));
                Assertions.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Users")));
                Assertions.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Group 1")));
            }
        }
    }

    /**
     * Test guest login.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testGuestLogin() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("Guest",
                "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assertions.assertTrue(this.loginModule.isAllowGuestLogin());
        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertEquals(0, subject.getPrincipals().size());
        Assertions.assertTrue(this.loginModule.commit());
        Assertions.assertEquals(2, subject.getPrincipals().size());
        Assertions.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                Assertions.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Everyone")));
                Assertions.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Users")));
            }
        }
        this.loginModule.setAllowGuestLogin(false);
        final Throwable exception = Assertions.assertThrows(LoginException.class, () -> {
            Assertions.assertTrue(this.loginModule.login());
        });
        Assertions.assertEquals("Guest login disabled", exception.getMessage());
    }

    /**
     * Test abort.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testAbort() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("Guest",
                "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assertions.assertTrue(this.loginModule.login());
        this.loginModule.abort();
        assertThat(subject.getPrincipals().size()).isEqualTo(0);
    }

}
