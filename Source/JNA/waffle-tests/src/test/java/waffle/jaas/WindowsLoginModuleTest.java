/*
 * MIT License
 *
 * Copyright (c) 2010-2021 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.mock.MockWindowsAuthProvider;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * The Class WindowsLoginModuleTest.
 *
 * @author dblock[at]dblock[dot]org
 */
class WindowsLoginModuleTest {

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
        Assertions.assertTrue(subject.getPrincipals().size() >= 3);
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Everyone")));
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Users")));
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

        Assertions.assertTrue(subject.getPrincipals().size() >= 5);
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Everyone")));
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Users")));
        int roleSize = 0;
        int roleSidSize = 0;
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof RolePrincipal) {
                if (principal.getName().startsWith("S-")) {
                    roleSidSize++;
                }
                roleSize++;
            }
        }
        Assertions.assertEquals(4, roleSize);
        Assertions.assertEquals(2, roleSidSize);
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

        Assertions.assertTrue(subject.getPrincipals().size() >= 3);
        Assertions.assertTrue(
                subject.getPrincipals().contains(new UserPrincipal(WindowsAccountImpl.getCurrentUsername())));
        int size = 0;
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof RolePrincipal) {
                if (principal.getName().startsWith("S-")) {
                    size++;
                }
            }
        }
        Assertions.assertEquals(2, size);
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

        Assertions.assertEquals(4, subject.getPrincipals().size());
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Everyone")));
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Users")));
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Group 1")));
    }

    /**
     * Test role from domain groups.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void testRoleFromDomainGroupNames() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        this.provider.addGroup("TestDomain\\Role 1");
        this.provider.addGroup("TestDomain\\Role 2");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        options.put("principalFormat", "fqn");
        options.put("roleFormat", "none");
        options.put("mapRolesFromDomainGroups", "DummyDomain, TestDomain ");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());

        Assertions.assertEquals(3, subject.getPrincipals().size());
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Role 1")));
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Role 2")));
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
        Assertions.assertTrue(subject.getPrincipals().size() >= 3);
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Everyone")));
        Assertions.assertTrue(subject.getPrincipals().contains(new RolePrincipal("Users")));
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
        Assertions.assertEquals(0, subject.getPrincipals().size());
    }

}
