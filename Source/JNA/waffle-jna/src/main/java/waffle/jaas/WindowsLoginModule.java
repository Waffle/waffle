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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A Java Security login module for Windows authentication.
 * 
 * @author dblock[at]dblock[dot]org
 * @see javax.security.auth.spi.LoginModule
 */
public class WindowsLoginModule implements LoginModule {

    private static final Logger  LOGGER          = LoggerFactory.getLogger(WindowsLoginModule.class);

    private String               username;
    private boolean              debug;
    private Subject              subject;
    private CallbackHandler      callbackHandler;
    private IWindowsAuthProvider auth            = new WindowsAuthProviderImpl();
    private Set<Principal>       principals;
    private PrincipalFormat      principalFormat = PrincipalFormat.FQN;
    private PrincipalFormat      roleFormat      = PrincipalFormat.FQN;
    private boolean              allowGuestLogin = true;

    @Override
    public void initialize(final Subject initSubject, final CallbackHandler initCallbackHandler,
            final Map<String, ?> initSharedState, final Map<String, ?> initOptions) {

        this.subject = initSubject;
        this.callbackHandler = initCallbackHandler;

        for (Entry<String, ?> option : initOptions.entrySet()) {
            if (option.getKey().equalsIgnoreCase("debug")) {
                this.debug = Boolean.parseBoolean((String) option.getValue());
            } else if (option.getKey().equalsIgnoreCase("principalFormat")) {
                this.principalFormat = PrincipalFormat
                        .valueOf(((String) option.getValue()).toUpperCase(Locale.ENGLISH));
            } else if (option.getKey().equalsIgnoreCase("roleFormat")) {
                this.roleFormat = PrincipalFormat.valueOf(((String) option.getValue()).toUpperCase(Locale.ENGLISH));
            }
        }
    }

    /**
     * Use Windows SSPI to authenticate a username with a password.
     */
    @Override
    public boolean login() throws LoginException {
        if (this.callbackHandler == null) {
            throw new LoginException("Missing callback to gather information from the user.");
        }

        final NameCallback usernameCallback = new NameCallback("user name: ");
        final PasswordCallback passwordCallback = new PasswordCallback("password: ", false);

        final Callback[] callbacks = new Callback[2];
        callbacks[0] = usernameCallback;
        callbacks[1] = passwordCallback;

        final String userName;
        final String password;

        try {
            this.callbackHandler.handle(callbacks);
            userName = usernameCallback.getName();
            password = passwordCallback.getPassword() == null ? "" : new String(passwordCallback.getPassword());
            passwordCallback.clearPassword();
        } catch (IOException e) {
            LOGGER.trace("{}", e);
            throw new LoginException(e.toString());
        } catch (UnsupportedCallbackException e) {
            LOGGER.trace("{}", e);
            throw new LoginException(
                    "Callback {} not available to gather authentication information from the user.".replace("{}", e
                            .getCallback().getClass().getName()));
        }

        IWindowsIdentity windowsIdentity;
        try {
            windowsIdentity = this.auth.logonUser(userName, password);
        } catch (Exception e) {
            LOGGER.trace("{}", e);
            throw new LoginException(e.getMessage());
        }

        try {
            // disable guest login
            if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
                LOGGER.debug("guest login disabled: {}", windowsIdentity.getFqn());
                throw new LoginException("Guest login disabled");
            }

            this.principals = new LinkedHashSet<Principal>();
            this.principals.addAll(getUserPrincipals(windowsIdentity, this.principalFormat));
            if (this.roleFormat != PrincipalFormat.NONE) {
                for (IWindowsAccount group : windowsIdentity.getGroups()) {
                    this.principals.addAll(getRolePrincipals(group, this.roleFormat));
                }
            }

            this.username = windowsIdentity.getFqn();
            LOGGER.debug("successfully logged in {} ({})", this.username, windowsIdentity.getSidString());
        } finally {
            windowsIdentity.dispose();
        }

        return true;
    }

    /**
     * Abort a login process.
     */
    @Override
    public boolean abort() throws LoginException {
        return logout();
    }

    /**
     * Commit principals to the subject.
     */
    @Override
    public boolean commit() throws LoginException {
        if (this.principals == null) {
            return false;
        }

        if (this.subject.isReadOnly()) {
            throw new LoginException("Subject cannot be read-only.");
        }

        final Set<Principal> principalsSet = this.subject.getPrincipals();
        principalsSet.addAll(this.principals);

        LOGGER.debug("committing {} principals", Integer.valueOf(this.subject.getPrincipals().size()));
        if (this.debug) {
            for (Principal principal : principalsSet) {
                LOGGER.debug(" principal: {}", principal.getName());
            }
        }

        return true;
    }

    /**
     * Logout a user.
     */
    @Override
    public boolean logout() throws LoginException {
        if (this.subject.isReadOnly()) {
            throw new LoginException("Subject cannot be read-only.");
        }

        this.subject.getPrincipals().clear();

        if (this.username != null) {
            LOGGER.debug("logging out {}", this.username);
        }

        return true;
    }

    /**
     * True if Debug is enabled.
     * 
     * @return True or false.
     */
    public boolean isDebug() {
        return this.debug;
    }

    /**
     * Windows auth provider.
     * 
     * @return IWindowsAuthProvider.
     */
    public IWindowsAuthProvider getAuth() {
        return this.auth;
    }

    /**
     * Set Windows auth provider.
     * 
     * @param provider
     *            Class implements IWindowsAuthProvider.
     */
    public void setAuth(final IWindowsAuthProvider provider) {
        this.auth = provider;
    }

    /**
     * Returns a list of user principal objects.
     * 
     * @param windowsIdentity
     *            Windows identity.
     * @param principalFormat
     *            Principal format.
     * @return A list of user principal objects.
     */
    private static List<Principal> getUserPrincipals(final IWindowsIdentity windowsIdentity,
            final PrincipalFormat principalFormat) {

        final List<Principal> principalsList = new ArrayList<Principal>();
        switch (principalFormat) {
            case FQN:
                principalsList.add(new UserPrincipal(windowsIdentity.getFqn()));
                break;
            case SID:
                principalsList.add(new UserPrincipal(windowsIdentity.getSidString()));
                break;
            case BOTH:
                principalsList.add(new UserPrincipal(windowsIdentity.getFqn()));
                principalsList.add(new UserPrincipal(windowsIdentity.getSidString()));
                break;
            case NONE:
                break;
            default:
                break;
        }
        return principalsList;
    }

    /**
     * Returns a list of role principal objects.
     * 
     * @param group
     *            Windows group.
     * @param principalFormat
     *            Principal format.
     * @return List of role principal objects.
     */
    private static List<Principal> getRolePrincipals(final IWindowsAccount group, final PrincipalFormat principalFormat) {

        final List<Principal> principalsList = new ArrayList<Principal>();
        switch (principalFormat) {
            case FQN:
                principalsList.add(new RolePrincipal(group.getFqn()));
                break;
            case SID:
                principalsList.add(new RolePrincipal(group.getSidString()));
                break;
            case BOTH:
                principalsList.add(new RolePrincipal(group.getFqn()));
                principalsList.add(new RolePrincipal(group.getSidString()));
                break;
            case NONE:
                break;
            default:
                break;
        }
        return principalsList;
    }

    /**
     * True if Guest login permitted.
     * 
     * @return True if Guest login permitted, false otherwise.
     */
    public boolean isAllowGuestLogin() {
        return this.allowGuestLogin;
    }

    /**
     * Set whether Guest login is permitted. Default is true, if the Guest account is enabled, an invalid
     * username/password results in a Guest login.
     * 
     * @param value
     *            True or false.
     */
    public void setAllowGuestLogin(final boolean value) {
        this.allowGuestLogin = value;
    }
}
