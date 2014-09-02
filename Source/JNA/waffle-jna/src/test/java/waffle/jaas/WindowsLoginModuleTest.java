/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
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
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import waffle.windows.auth.PrincipalFormat;

public class WindowsLoginModuleTest {

    private WindowsLoginModule  loginModule;
    private Subject             subject;
    private CallbackHandler     callbackHandler;
    private Map<String, String> options;

    @Test
    public void checkAuth() {
        Assert.assertNotNull(this.loginModule.getAuth());
        this.loginModule.setAuth(null);
        Assert.assertNull(this.loginModule.getAuth());
    }

    @Test
    public void checkGuestLogin() {
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        this.loginModule.setAllowGuestLogin(false);
        Assert.assertFalse(this.loginModule.isAllowGuestLogin());
    }

    @Test
    public void commit_noPrincipal() throws LoginException {
        Assert.assertFalse(this.loginModule.commit());
    }

    @Test(expected = LoginException.class)
    public void commit_subjectReadOnly() throws LoginException {
        this.subject.setReadOnly();
        Whitebox.setInternalState(this.loginModule, Set.class, new LinkedHashSet<Principal>());
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        this.loginModule.commit();
    }

    @Test
    public void commit_success() throws LoginException {
        Whitebox.setInternalState(this.loginModule, Set.class, new LinkedHashSet<Principal>());
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        this.loginModule.commit();
    }

    @Test
    public void commit_withDebug() throws LoginException {
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        final Set<Principal> principals = new LinkedHashSet<Principal>();
        principals.add(new UserPrincipal("FQN"));
        Whitebox.setInternalState(this.loginModule, Set.class, principals);
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        this.loginModule.commit();
    }

    @Before
    public void init() {
        this.loginModule = new WindowsLoginModule();
        this.subject = new Subject();
        this.callbackHandler = Mockito.mock(CallbackHandler.class);
        this.options = new HashMap<String, String>();
    }

    @Test
    public void initialize_withOptions() {
        this.options.put("debug", "true");
        this.options.put("principalFormat", "sid");
        this.options.put("roleFormat", "none");
        this.options.put("junk", "junk");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.isDebug());
        Assert.assertEquals(PrincipalFormat.SID, Whitebox.getInternalState(this.loginModule, "principalFormat"));
        Assert.assertEquals(PrincipalFormat.NONE, Whitebox.getInternalState(this.loginModule, "roleFormat"));
    }

    @Test(expected = LoginException.class)
    public void login_invalidGuestLogin() throws LoginException {
        this.callbackHandler = new UsernamePasswordCallbackHandler("Guest", "password");
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        this.loginModule.login();
    }

    @Test(expected = LoginException.class)
    public void login_nullPassword() throws LoginException {
        this.callbackHandler = new UsernamePasswordCallbackHandler("Guest", null);
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        this.loginModule.login();
    }

    @Test(expected = LoginException.class)
    public void login_throwIOException() throws LoginException, IOException, UnsupportedCallbackException {
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        PowerMockito.doThrow(new IOException()).when(this.callbackHandler).handle(Matchers.any(Callback[].class));
        this.loginModule.login();
    }

    @Test(expected = LoginException.class)
    public void login_throwUnsupportedCallbackException() throws LoginException, IOException,
            UnsupportedCallbackException {
        this.options.put("debug", "true");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.isAllowGuestLogin());
        PowerMockito.doThrow(new UnsupportedCallbackException(new NameCallback("Callback Exception")))
                .when(this.callbackHandler).handle(Matchers.any(Callback[].class));
        this.loginModule.login();
    }

    @Test(expected = LoginException.class)
    public void logon_noCallbackHandler() throws LoginException {
        this.loginModule.login();
    }

    @Test
    public void logout_abortNoUser() throws LoginException {
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.abort());
    }

    @Test
    public void logout_noUser() throws LoginException {
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.logout());
    }

    @Test(expected = LoginException.class)
    public void logout_subjectReadOnly() throws LoginException {
        this.subject.setReadOnly();
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        this.loginModule.logout();
    }

    @Test
    public void logout_validUser() throws LoginException {
        Whitebox.setInternalState(this.loginModule, "username", "waffle-user");
        this.loginModule.initialize(this.subject, this.callbackHandler, null, this.options);
        Assert.assertTrue(this.loginModule.logout());
    }

}
