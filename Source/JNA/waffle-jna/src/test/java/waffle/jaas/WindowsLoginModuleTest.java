/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.jaas;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

/**
 * The Class WindowsLoginModuleTest.
 */
class WindowsLoginModuleTest {

    /** The login module. */
    private WindowsLoginModule loginModule;

    /** The subject. */
    private Subject subject;

    /** The callback handler. */
    @Mocked
    private CallbackHandler callbackHandler;

    /** The options. */
    private Map<String, String> options;

    /**
     * Check auth.
     */
    @Test
    void checkAuth() {
        Assertions.assertNotNull(this.loginModule.getAuth());
        this.loginModule.setAuth(null);
        Assertions.assertNull(this.loginModule.getAuth());
    }

    /**
     * Check guest login.
     */
    @Test
    void checkGuestLogin() {
        Assertions.assertTrue(this.loginModule.isAllowGuestLogin());
        this.loginModule.setAllowGuestLogin(false);
        Assertions.assertFalse(this.loginModule.isAllowGuestLogin());
    }

    /**
     * Commit_no principal.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void commit_noPrincipal() throws LoginException {
        Assertions.assertFalse(this.loginModule.commit());
    }

    /**
     * Commit_subject read only.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void commit_subjectReadOnly() throws LoginException {
        this.subject.setReadOnly();
        Whitebox.setInternalState(this.loginModule, new LinkedHashSet<Principal>());
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertThrows(LoginException.class, () -> {
            this.loginModule.commit();
        });
    }

    /**
     * Commit_success.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void commit_success() throws LoginException {
        Whitebox.setInternalState(this.loginModule, new LinkedHashSet<Principal>());
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.commit());
    }

    /**
     * Commit_with debug.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void commit_withDebug() throws LoginException {
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        final Set<Principal> principals = new LinkedHashSet<>();
        principals.add(new UserPrincipal("FQN"));
        Whitebox.setInternalState(this.loginModule, principals);
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.commit());
    }

    /**
     * Commit_with Roles.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void commit_withRoles() throws LoginException {
        this.options.put("debug", "true");
        final Set<Principal> principals = new LinkedHashSet<>();
        principals.add(new UserPrincipal("FQN"));
        principals.add(new RolePrincipal("WindowsGroup"));
        Whitebox.setInternalState(this.loginModule, principals);
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.commit());
    }

    /**
     * Inits the.
     */
    @BeforeEach
    void init() {
        this.loginModule = new WindowsLoginModule();
        this.subject = new Subject();
        this.options = new HashMap<>();
    }

    /**
     * Initialize_with options.
     */
    @Test
    void initialize_withOptions() {
        this.options.put("debug", "true");
        this.options.put("principalFormat", "sid");
        this.options.put("roleFormat", "none");
        this.options.put("junk", "junk");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.isDebug());
        Assertions.assertEquals(PrincipalFormat.SID, Whitebox.getInternalState(this.loginModule, "principalFormat"));
        Assertions.assertEquals(PrincipalFormat.NONE, Whitebox.getInternalState(this.loginModule, "roleFormat"));
    }

    /**
     * Login_invalid guest login.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void login_invalidGuestLogin() throws LoginException {
        this.callbackHandler = new UsernamePasswordCallbackHandler("Guest", "password");
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.isAllowGuestLogin());
        Assertions.assertThrows(LoginException.class, () -> {
            this.loginModule.login();
        });
    }

    /**
     * Login_null password.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void login_nullPassword() throws LoginException {
        this.callbackHandler = new UsernamePasswordCallbackHandler("Guest", null);
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.isAllowGuestLogin());
        Assertions.assertThrows(LoginException.class, () -> {
            this.loginModule.login();
        });
    }

    /**
     * Login_throw io exception.
     *
     * @throws LoginException
     *             the login exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws UnsupportedCallbackException
     *             the unsupported callback exception
     */
    @Test
    void login_throwIOException() throws LoginException, IOException, UnsupportedCallbackException {
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.isAllowGuestLogin());
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsLoginModuleTest.this.callbackHandler.handle(this.withInstanceOf(Callback[].class));
                this.result = new IOException();
            }
        });
        Assertions.assertThrows(LoginException.class, () -> {
            this.loginModule.login();
        });
    }

    /**
     * Login_throw unsupported callback exception.
     *
     * @throws LoginException
     *             the login exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws UnsupportedCallbackException
     *             the unsupported callback exception
     */
    @Test
    void login_throwUnsupportedCallbackException() throws LoginException, IOException, UnsupportedCallbackException {
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.isAllowGuestLogin());
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsLoginModuleTest.this.callbackHandler.handle(this.withInstanceOf(Callback[].class));
                this.result = new UnsupportedCallbackException(new NameCallback("Callback Exception"));
            }
        });
        Assertions.assertThrows(LoginException.class, () -> {
            this.loginModule.login();
        });
    }

    /**
     * Logon_no callback handler.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void logon_noCallbackHandler() throws LoginException {
        Assertions.assertThrows(LoginException.class, () -> {
            this.loginModule.login();
        });
    }

    /**
     * Logout_abort no user.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void logout_abortNoUser() throws LoginException {
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.abort());
    }

    /**
     * Logout_no user.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void logout_noUser() throws LoginException {
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.logout());
    }

    /**
     * Logout_subject read only.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void logout_subjectReadOnly() throws LoginException {
        this.subject.setReadOnly();
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertThrows(LoginException.class, () -> {
            this.loginModule.logout();
        });
    }

    /**
     * Logout_valid user.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    void logout_validUser() throws LoginException {
        Whitebox.setInternalState(this.loginModule, "username", "waffle-user");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.logout());
    }

    /**
     * Test login success with mocked auth provider returns true.
     *
     * @param mockAuth
     *            the mocked auth provider
     * @param mockIdentity
     *            the mocked identity
     * @param mockGroup
     *            the mocked group
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void login_successWithMockedAuth(@Mocked final IWindowsAuthProvider mockAuth,
            @Mocked final IWindowsIdentity mockIdentity, @Mocked final IWindowsAccount mockGroup) throws Exception {
        Assertions.assertNotNull(new Expectations() {
            {
                mockAuth.logonUser("testuser", "password");
                this.result = mockIdentity;
                mockIdentity.isGuest();
                this.result = false;
                this.minTimes = 0;
                mockIdentity.getGroups();
                this.result = new IWindowsAccount[] { mockGroup };
                mockGroup.getFqn();
                this.result = "DOMAIN\\Users";
                this.minTimes = 0;
                mockGroup.getSidString();
                this.result = "S-1-5-32-545";
                this.minTimes = 0;
                mockIdentity.getFqn();
                this.result = "DOMAIN\\testuser";
                mockIdentity.getSidString();
                this.result = "S-1-5-21-999-testuser";
                this.minTimes = 0;
                mockIdentity.dispose();
                this.minTimes = 1;
            }
        });

        this.loginModule.setAuth(mockAuth);
        this.callbackHandler = callbacks -> {
            for (final javax.security.auth.callback.Callback cb : callbacks) {
                if (cb instanceof javax.security.auth.callback.NameCallback) {
                    ((javax.security.auth.callback.NameCallback) cb).setName("testuser");
                } else if (cb instanceof javax.security.auth.callback.PasswordCallback) {
                    ((javax.security.auth.callback.PasswordCallback) cb).setPassword("password".toCharArray());
                }
            }
        };
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);

        Assertions.assertTrue(this.loginModule.login());
    }

    /**
     * Test login with allowGuestLogin=false and guest user throws LoginException.
     *
     * @param mockAuth
     *            the mocked auth provider
     * @param mockIdentity
     *            the mocked identity
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void login_guestDisabledThrows(@Mocked final IWindowsAuthProvider mockAuth,
            @Mocked final IWindowsIdentity mockIdentity) throws Exception {
        Assertions.assertNotNull(new Expectations() {
            {
                mockAuth.logonUser("Guest", "password");
                this.result = mockIdentity;
                mockIdentity.isGuest();
                this.result = true;
                mockIdentity.getFqn();
                this.result = "DOMAIN\\Guest";
                this.minTimes = 0;
                mockIdentity.dispose();
                this.minTimes = 1;
            }
        });

        this.loginModule.setAuth(mockAuth);
        this.loginModule.setAllowGuestLogin(false);
        this.callbackHandler = callbacks -> {
            for (final javax.security.auth.callback.Callback cb : callbacks) {
                if (cb instanceof javax.security.auth.callback.NameCallback) {
                    ((javax.security.auth.callback.NameCallback) cb).setName("Guest");
                } else if (cb instanceof javax.security.auth.callback.PasswordCallback) {
                    ((javax.security.auth.callback.PasswordCallback) cb).setPassword("password".toCharArray());
                }
            }
        };
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);

        Assertions.assertThrows(LoginException.class, () -> this.loginModule.login());
    }

    /**
     * Test login with SID principal format uses SID as principal name.
     *
     * @param mockAuth
     *            the mocked auth provider
     * @param mockIdentity
     *            the mocked identity
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void login_sidPrincipalFormat(@Mocked final IWindowsAuthProvider mockAuth,
            @Mocked final IWindowsIdentity mockIdentity) throws Exception {
        Assertions.assertNotNull(new Expectations() {
            {
                mockAuth.logonUser("testuser", "pass");
                this.result = mockIdentity;
                mockIdentity.isGuest();
                this.result = false;
                this.minTimes = 0;
                mockIdentity.getGroups();
                this.result = new IWindowsAccount[0];
                mockIdentity.getFqn();
                this.result = "DOMAIN\\testuser";
                this.minTimes = 0;
                mockIdentity.getSidString();
                this.result = "S-1-5-21-sid";
                mockIdentity.dispose();
                this.minTimes = 1;
            }
        });

        this.options.put("principalFormat", "sid");
        this.loginModule.setAuth(mockAuth);
        this.callbackHandler = callbacks -> {
            for (final javax.security.auth.callback.Callback cb : callbacks) {
                if (cb instanceof javax.security.auth.callback.NameCallback) {
                    ((javax.security.auth.callback.NameCallback) cb).setName("testuser");
                } else if (cb instanceof javax.security.auth.callback.PasswordCallback) {
                    ((javax.security.auth.callback.PasswordCallback) cb).setPassword("pass".toCharArray());
                }
            }
        };
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);

        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());

        final boolean hasSidPrincipal = this.subject.getPrincipals().stream()
                .anyMatch(p -> "S-1-5-21-sid".equals(p.getName()));
        Assertions.assertTrue(hasSidPrincipal);
    }

    /**
     * Test login with BOTH principal format includes both FQN and SID principals.
     *
     * @param mockAuth
     *            the mocked auth provider
     * @param mockIdentity
     *            the mocked identity
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void login_bothPrincipalFormat(@Mocked final IWindowsAuthProvider mockAuth,
            @Mocked final IWindowsIdentity mockIdentity) throws Exception {
        Assertions.assertNotNull(new Expectations() {
            {
                mockAuth.logonUser("testuser", "pass");
                this.result = mockIdentity;
                mockIdentity.isGuest();
                this.result = false;
                this.minTimes = 0;
                mockIdentity.getGroups();
                this.result = new IWindowsAccount[0];
                mockIdentity.getFqn();
                this.result = "DOMAIN\\testuser";
                mockIdentity.getSidString();
                this.result = "S-1-5-21-both";
                mockIdentity.dispose();
                this.minTimes = 1;
            }
        });

        this.options.put("principalFormat", "both");
        this.loginModule.setAuth(mockAuth);
        this.callbackHandler = callbacks -> {
            for (final javax.security.auth.callback.Callback cb : callbacks) {
                if (cb instanceof javax.security.auth.callback.NameCallback) {
                    ((javax.security.auth.callback.NameCallback) cb).setName("testuser");
                } else if (cb instanceof javax.security.auth.callback.PasswordCallback) {
                    ((javax.security.auth.callback.PasswordCallback) cb).setPassword("pass".toCharArray());
                }
            }
        };
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);

        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());

        Assertions.assertEquals(2, this.subject.getPrincipals().size());
    }

    /**
     * Test login with NONE principal format adds no user principals but may add role principals.
     *
     * @param mockAuth
     *            the mocked auth provider
     * @param mockIdentity
     *            the mocked identity
     * @param mockGroup
     *            the mocked group
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void login_nonePrincipalFormat(@Mocked final IWindowsAuthProvider mockAuth,
            @Mocked final IWindowsIdentity mockIdentity, @Mocked final IWindowsAccount mockGroup) throws Exception {
        Assertions.assertNotNull(new Expectations() {
            {
                mockAuth.logonUser("testuser", "pass");
                this.result = mockIdentity;
                mockIdentity.isGuest();
                this.result = false;
                this.minTimes = 0;
                mockIdentity.getGroups();
                this.result = new IWindowsAccount[] { mockGroup };
                this.minTimes = 0;
                mockGroup.getFqn();
                this.result = "DOMAIN\\Users";
                this.minTimes = 0;
                mockGroup.getSidString();
                this.result = "S-1-5-32-545";
                this.minTimes = 0;
                mockIdentity.getFqn();
                this.result = "DOMAIN\\testuser";
                this.minTimes = 0;
                mockIdentity.getSidString();
                this.result = "S-1-5-21-none";
                this.minTimes = 0;
                mockIdentity.dispose();
                this.minTimes = 1;
            }
        });

        this.options.put("principalFormat", "none");
        this.options.put("roleFormat", "none");
        this.loginModule.setAuth(mockAuth);
        this.callbackHandler = callbacks -> {
            for (final javax.security.auth.callback.Callback cb : callbacks) {
                if (cb instanceof javax.security.auth.callback.NameCallback) {
                    ((javax.security.auth.callback.NameCallback) cb).setName("testuser");
                } else if (cb instanceof javax.security.auth.callback.PasswordCallback) {
                    ((javax.security.auth.callback.PasswordCallback) cb).setPassword("pass".toCharArray());
                }
            }
        };
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);

        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());
        Assertions.assertEquals(0, this.subject.getPrincipals().size());
    }

    /**
     * Test login with role format SID adds role principals with SID.
     *
     * @param mockAuth
     *            the mocked auth provider
     * @param mockIdentity
     *            the mocked identity
     * @param mockGroup
     *            the mocked group
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void login_sidRoleFormat(@Mocked final IWindowsAuthProvider mockAuth, @Mocked final IWindowsIdentity mockIdentity,
            @Mocked final IWindowsAccount mockGroup) throws Exception {
        Assertions.assertNotNull(new Expectations() {
            {
                mockAuth.logonUser("testuser", "pass");
                this.result = mockIdentity;
                mockIdentity.isGuest();
                this.result = false;
                this.minTimes = 0;
                mockIdentity.getGroups();
                this.result = new IWindowsAccount[] { mockGroup };
                mockGroup.getFqn();
                this.result = "DOMAIN\\Users";
                this.minTimes = 0;
                mockGroup.getSidString();
                this.result = "S-1-5-32-545";
                mockIdentity.getFqn();
                this.result = "DOMAIN\\testuser";
                this.minTimes = 0;
                mockIdentity.getSidString();
                this.result = "S-1-5-21-sid-role";
                this.minTimes = 0;
                mockIdentity.dispose();
                this.minTimes = 1;
            }
        });

        this.options.put("roleFormat", "sid");
        this.loginModule.setAuth(mockAuth);
        this.callbackHandler = callbacks -> {
            for (final javax.security.auth.callback.Callback cb : callbacks) {
                if (cb instanceof javax.security.auth.callback.NameCallback) {
                    ((javax.security.auth.callback.NameCallback) cb).setName("testuser");
                } else if (cb instanceof javax.security.auth.callback.PasswordCallback) {
                    ((javax.security.auth.callback.PasswordCallback) cb).setPassword("pass".toCharArray());
                }
            }
        };
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);

        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());

        final boolean hasSidRole = this.subject.getPrincipals().stream()
                .anyMatch(p -> "S-1-5-32-545".equals(p.getName()));
        Assertions.assertTrue(hasSidRole);
    }

    /**
     * Test login with role format BOTH adds two role principals per group.
     *
     * @param mockAuth
     *            the mocked auth provider
     * @param mockIdentity
     *            the mocked identity
     * @param mockGroup
     *            the mocked group
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void login_bothRoleFormat(@Mocked final IWindowsAuthProvider mockAuth, @Mocked final IWindowsIdentity mockIdentity,
            @Mocked final IWindowsAccount mockGroup) throws Exception {
        Assertions.assertNotNull(new Expectations() {
            {
                mockAuth.logonUser("testuser", "pass");
                this.result = mockIdentity;
                mockIdentity.isGuest();
                this.result = false;
                this.minTimes = 0;
                mockIdentity.getGroups();
                this.result = new IWindowsAccount[] { mockGroup };
                mockGroup.getFqn();
                this.result = "DOMAIN\\Users";
                mockGroup.getSidString();
                this.result = "S-1-5-32-545";
                mockIdentity.getFqn();
                this.result = "DOMAIN\\testuser";
                mockIdentity.getSidString();
                this.result = "S-1-5-21-both-role";
                this.minTimes = 0;
                mockIdentity.dispose();
                this.minTimes = 1;
            }
        });

        this.options.put("roleFormat", "both");
        this.loginModule.setAuth(mockAuth);
        this.callbackHandler = callbacks -> {
            for (final javax.security.auth.callback.Callback cb : callbacks) {
                if (cb instanceof javax.security.auth.callback.NameCallback) {
                    ((javax.security.auth.callback.NameCallback) cb).setName("testuser");
                } else if (cb instanceof javax.security.auth.callback.PasswordCallback) {
                    ((javax.security.auth.callback.PasswordCallback) cb).setPassword("pass".toCharArray());
                }
            }
        };
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);

        Assertions.assertTrue(this.loginModule.login());
        Assertions.assertTrue(this.loginModule.commit());

        // 1 user principal (FQN) + 2 role principals (FQN and SID) = 3 total
        Assertions.assertEquals(3, this.subject.getPrincipals().size());
    }

}
