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
package waffle.jaas;

import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import waffle.mock.MockWindowsAuthProvider;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * The Class WindowsLoginModuleTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsLoginModuleTests {

    /** The login module. */
    WindowsLoginModule      loginModule;

    /** The provider. */
    MockWindowsAuthProvider provider;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        this.provider = new MockWindowsAuthProvider();
        this.loginModule = new WindowsLoginModule();
        this.loginModule.setAuth(this.provider);
    }

    /**
     * Test initialize.
     */
    @Test
    public void testInitialize() {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("", "");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assert.assertTrue(this.loginModule.isDebug());
    }

    /**
     * Test get set auth.
     */
    @Test
    public void testGetSetAuth() {
        Assert.assertNotNull(this.loginModule.getAuth());
        this.loginModule.setAuth(null);
        Assert.assertNull(this.loginModule.getAuth());
    }

    /**
     * Test login.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void testLogin() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assert.assertTrue(this.loginModule.login());
        Assert.assertEquals(0, subject.getPrincipals().size());
        Assert.assertTrue(this.loginModule.commit());
        Assert.assertEquals(2, subject.getPrincipals().size());
        Assert.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                Assert.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Everyone")));
                Assert.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Users")));
            }
        }
        Assert.assertTrue(subject.getPrincipals().contains(new UserPrincipal(WindowsAccountImpl.getCurrentUsername())));
        Assert.assertTrue(this.loginModule.logout());
        Assert.assertSame(Integer.valueOf(subject.getPrincipals().size()), Integer.valueOf(0));
    }

    /**
     * Test no callback handler.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test(expected = LoginException.class)
    public void testNoCallbackHandler() throws LoginException {
        final Subject subject = new Subject();
        final Map<String, String> options = new HashMap<>();
        this.loginModule.initialize(subject, null, null, options);
        this.loginModule.login();
    }

    /**
     * Test login no username.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test(expected = LoginException.class)
    public void testLoginNoUsername() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("", "");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assert.assertFalse(this.loginModule.login());
        Assert.fail("Expected LoginException");
    }

    /**
     * Test role format none.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void testRoleFormatNone() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        options.put("roleFormat", "none");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assert.assertTrue(this.loginModule.login());
        Assert.assertTrue(this.loginModule.commit());
        Assert.assertEquals(1, subject.getPrincipals().size());
    }

    /**
     * Test role format both.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void testRoleFormatBoth() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        options.put("roleFormat", "both");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assert.assertTrue(this.loginModule.login());
        Assert.assertTrue(this.loginModule.commit());

        Assert.assertEquals(2, subject.getPrincipals().size());
        Assert.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                int size = 0;
                int sidSize = 0;
                Enumeration<? extends Principal> groupPrincipal = ((GroupPrincipal) principal).members();
                while (groupPrincipal.hasMoreElements()) {
                    if (groupPrincipal.nextElement().getName().startsWith("S-")) {
                        sidSize++;
                    }
                    size++;
                }
                Assert.assertEquals(4, size);
                Assert.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Everyone")));
                Assert.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Users")));
                Assert.assertEquals(2, sidSize);
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
    public void testPrincipalFormatBoth() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        options.put("principalFormat", "both");
        options.put("roleFormat", "none");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assert.assertTrue(this.loginModule.login());
        Assert.assertTrue(this.loginModule.commit());
        Assert.assertEquals(2, subject.getPrincipals().size());
    }

    /**
     * Test role format sid.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void testRoleFormatSid() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        options.put("roleFormat", "sid");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assert.assertTrue(this.loginModule.login());
        Assert.assertTrue(this.loginModule.commit());

        Assert.assertEquals(2, subject.getPrincipals().size());
        Assert.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                int size = 0;
                Enumeration<? extends Principal> groupPrincipal = ((GroupPrincipal) principal).members();
                while (groupPrincipal.hasMoreElements()) {
                    groupPrincipal.nextElement().getName().startsWith("S-");
                    size++;
                }
                Assert.assertEquals(2, size);
            } else {
                Assert.assertTrue(principal.getName().equals(WindowsAccountImpl.getCurrentUsername()));
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
    public void testRoleUnique() throws LoginException {
        final Subject subject = new Subject();
        // the mock has an "Everyone" group
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
                WindowsAccountImpl.getCurrentUsername(), "password");
        this.provider.addGroup("Group 1");
        this.provider.addGroup("Group 1");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assert.assertTrue(this.loginModule.login());
        Assert.assertTrue(this.loginModule.commit());

        Assert.assertEquals(2, subject.getPrincipals().size());
        Assert.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                int size = 0;
                Enumeration<? extends Principal> groupPrincipal = ((GroupPrincipal) principal).members();
                while (groupPrincipal.hasMoreElements()) {
                    groupPrincipal.nextElement();
                    size++;
                }
                Assert.assertEquals(3, size);
                Assert.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Everyone")));
                Assert.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Users")));
                Assert.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Group 1")));
            }
        }
    }

    /**
     * Test guest login.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test(expected = LoginException.class)
    public void testGuestLogin() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("Guest", "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        Assert.assertTrue(this.loginModule.login());
        Assert.assertEquals(0, subject.getPrincipals().size());
        Assert.assertTrue(this.loginModule.commit());
        Assert.assertEquals(2, subject.getPrincipals().size());
        Assert.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                Assert.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Everyone")));
                Assert.assertTrue(((GroupPrincipal) principal).isMember(new RolePrincipal("Users")));
            }
        }
        this.loginModule.setAllowGuestLogin(false);
        Assert.assertTrue(this.loginModule.login());
        Assert.fail("expected LoginException");
    }

    /**
     * Test abort.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void testAbort() throws LoginException {
        final Subject subject = new Subject();
        final UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("Guest", "password");
        final Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        this.loginModule.initialize(subject, callbackHandler, null, options);
        Assert.assertTrue(this.loginModule.login());
        this.loginModule.abort();
        Assertions.assertThat(subject.getPrincipals().size()).isEqualTo(0);
    }
}
