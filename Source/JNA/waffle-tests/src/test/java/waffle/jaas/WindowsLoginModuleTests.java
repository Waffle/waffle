/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.jaas;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.Principal;
import java.util.Enumeration;
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
        Assertions.assertTrue(this.loginModule.isDebug());
    }

    /**
     * Test get set auth.
     */
    @Test
    public void testGetSetAuth() {
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
    public void testLogin() throws LoginException {
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
    public void testNoCallbackHandler() throws LoginException {
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
    public void testLoginNoUsername() throws LoginException {
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
    public void testRoleFormatNone() throws LoginException {
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
    public void testRoleFormatBoth() throws LoginException {
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
                final Enumeration<? extends Principal> groupPrincipal = ((GroupPrincipal) principal).members();
                while (groupPrincipal.hasMoreElements()) {
                    if (groupPrincipal.nextElement().getName().startsWith("S-")) {
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
    public void testPrincipalFormatBoth() throws LoginException {
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
    public void testRoleFormatSid() throws LoginException {
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
                final Enumeration<? extends Principal> groupPrincipal = ((GroupPrincipal) principal).members();
                while (groupPrincipal.hasMoreElements()) {
                    if (groupPrincipal.nextElement().getName().startsWith("S-")) {
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
        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());

        Assertions.assertEquals(2, subject.getPrincipals().size());
        Assertions.assertTrue(subject.getPrincipals().contains(new GroupPrincipal("Roles")));
        for (final Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                int size = 0;
                final Enumeration<? extends Principal> groupPrincipal = ((GroupPrincipal) principal).members();
                while (groupPrincipal.hasMoreElements()) {
                    groupPrincipal.nextElement();
                    size++;
                }
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
    public void testGuestLogin() throws LoginException {
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
    public void testAbort() throws LoginException {
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
