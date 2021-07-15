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
    CallbackHandler callbackHandler;

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
        this.options.put("mapRolesFromDomainGroups", "\" DUMMYDOMAIN ,\t DUMMYDOMAIN2 \"  ");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assertions.assertTrue(this.loginModule.isDebug());
        Assertions.assertEquals(PrincipalFormat.SID, Whitebox.getInternalState(this.loginModule, "principalFormat"));
        Assertions.assertEquals(PrincipalFormat.NONE, Whitebox.getInternalState(this.loginModule, "roleFormat"));
        Assertions.assertEquals("DUMMYDOMAIN,DUMMYDOMAIN2", Whitebox.getInternalState(this.loginModule, "domains"));
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

}
