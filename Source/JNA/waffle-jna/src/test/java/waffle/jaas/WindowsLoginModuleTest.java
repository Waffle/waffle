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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import waffle.windows.auth.PrincipalFormat;

/**
 * The Class WindowsLoginModuleTest.
 */
public class WindowsLoginModuleTest {

    /** The login module. */
    private WindowsLoginModule  loginModule;

    /** The subject. */
    private Subject             subject;

    /** The callback handler. */
    @Mocked
    CallbackHandler             callbackHandler;

    /** The options. */
    private Map<String, String> options;

    /**
     * Check auth.
     */
    @Test
    public void checkAuth() {
        Assert.assertNotNull(this.loginModule.getAuth());
        this.loginModule.setAuth(null);
        Assert.assertNull(this.loginModule.getAuth());
    }

    /**
     * Check guest login.
     */
    @Test
    public void checkGuestLogin() {
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        this.loginModule.setAllowGuestLogin(false);
        Assert.assertFalse(this.loginModule.isAllowGuestLogin());
    }

    /**
     * Commit_no principal.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void commit_noPrincipal() throws LoginException {
        Assert.assertFalse(this.loginModule.commit());
    }

    /**
     * Commit_subject read only.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test(expected = LoginException.class)
    public void commit_subjectReadOnly() throws LoginException {
        this.subject.setReadOnly();
        Deencapsulation.setField(this.loginModule, new LinkedHashSet<Principal>());
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        this.loginModule.commit();
    }

    /**
     * Commit_success.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void commit_success() throws LoginException {
        Deencapsulation.setField(this.loginModule, new LinkedHashSet<Principal>());
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        this.loginModule.commit();
    }

    /**
     * Commit_with debug.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void commit_withDebug() throws LoginException {
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        final Set<Principal> principals = new LinkedHashSet<>();
        principals.add(new UserPrincipal("FQN"));
        Deencapsulation.setField(this.loginModule, principals);
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        this.loginModule.commit();
    }

    /**
     * Commit_with Roles.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void commit_withRoles() throws LoginException {
        final Set<Principal> principals = new LinkedHashSet<>();
        principals.add(new UserPrincipal("FQN"));
        final GroupPrincipal group = new GroupPrincipal("Roles");
        group.addMember(new RolePrincipal("WindowsGroup"));
        principals.add(group);
        Deencapsulation.setField(this.loginModule, principals);
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        this.loginModule.commit();
    }

    /**
     * Inits the.
     */
    @Before
    public void init() {
        this.loginModule = new WindowsLoginModule();
        this.subject = new Subject();
        this.options = new HashMap<>();
    }

    /**
     * Initialize_with options.
     */
    @Test
    public void initialize_withOptions() {
        this.options.put("debug", "true");
        this.options.put("principalFormat", "sid");
        this.options.put("roleFormat", "none");
        this.options.put("junk", "junk");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.isDebug());
        Assert.assertEquals(PrincipalFormat.SID, Deencapsulation.getField(this.loginModule, "principalFormat"));
        Assert.assertEquals(PrincipalFormat.NONE, Deencapsulation.getField(this.loginModule, "roleFormat"));
    }

    /**
     * Login_invalid guest login.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test(expected = LoginException.class)
    public void login_invalidGuestLogin() throws LoginException {
        this.callbackHandler = new UsernamePasswordCallbackHandler("Guest", "password");
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        this.loginModule.login();
    }

    /**
     * Login_null password.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test(expected = LoginException.class)
    public void login_nullPassword() throws LoginException {
        this.callbackHandler = new UsernamePasswordCallbackHandler("Guest", null);
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        this.loginModule.login();
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
    @Test(expected = LoginException.class)
    public void login_throwIOException() throws LoginException, IOException, UnsupportedCallbackException {
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        Assert.assertNotNull(new Expectations() {
            {
                WindowsLoginModuleTest.this.callbackHandler.handle(this.withInstanceOf(Callback[].class));
                this.result = new IOException();
            }
        });
        this.loginModule.login();
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
    @Test(expected = LoginException.class)
    public void login_throwUnsupportedCallbackException() throws LoginException, IOException,
            UnsupportedCallbackException {
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        Assert.assertNotNull(new Expectations() {
            {
                WindowsLoginModuleTest.this.callbackHandler.handle(this.withInstanceOf(Callback[].class));
                this.result = new UnsupportedCallbackException(new NameCallback("Callback Exception"));
            }
        });
        this.loginModule.login();
    }

    /**
     * Logon_no callback handler.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test(expected = LoginException.class)
    public void logon_noCallbackHandler() throws LoginException {
        this.loginModule.login();
    }

    /**
     * Logout_abort no user.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void logout_abortNoUser() throws LoginException {
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.abort());
    }

    /**
     * Logout_no user.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void logout_noUser() throws LoginException {
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.logout());
    }

    /**
     * Logout_subject read only.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test(expected = LoginException.class)
    public void logout_subjectReadOnly() throws LoginException {
        this.subject.setReadOnly();
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        this.loginModule.logout();
    }

    /**
     * Logout_valid user.
     *
     * @throws LoginException
     *             the login exception
     */
    @Test
    public void logout_validUser() throws LoginException {
        Deencapsulation.setField(this.loginModule, "username", "waffle-user");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.logout());
    }

}
