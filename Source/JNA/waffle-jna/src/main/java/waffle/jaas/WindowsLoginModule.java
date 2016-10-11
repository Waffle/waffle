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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

    /** The Constant LOGGER. */
    private static final Logger  LOGGER          = LoggerFactory.getLogger(WindowsLoginModule.class);

    /** The username. */
    private String               username;

    /** The debug. */
    private boolean              debug;

    /** The subject. */
    private Subject              subject;

    /** The callback handler. */
    private CallbackHandler      callbackHandler;

    /** The auth. */
    private IWindowsAuthProvider auth            = new WindowsAuthProviderImpl();

    /** The principals. */
    private Set<Principal>       principals;

    /** The principal format. */
    private PrincipalFormat      principalFormat = PrincipalFormat.FQN;

    /** The role format. */
    private PrincipalFormat      roleFormat      = PrincipalFormat.FQN;

    /** The allow guest login. */
    private boolean              allowGuestLogin = true;

    /*
     * (non-Javadoc)
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject,
     * javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     */
    @Override
    public void initialize(final Subject initSubject, final CallbackHandler initCallbackHandler,
            final Map<String, ?> initSharedState, final Map<String, ?> initOptions) {

        this.subject = initSubject;
        this.callbackHandler = initCallbackHandler;

        for (final Entry<String, ?> option : initOptions.entrySet()) {
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
     *
     * @return true, if successful
     * @throws LoginException
     *             the login exception
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
        } catch (final IOException e) {
            WindowsLoginModule.LOGGER.trace("", e);
            throw new LoginException(e.toString());
        } catch (final UnsupportedCallbackException e) {
            WindowsLoginModule.LOGGER.trace("", e);
            throw new LoginException(
                    "Callback {} not available to gather authentication information from the user.".replace("{}", e
                            .getCallback().getClass().getName()));
        }

        IWindowsIdentity windowsIdentity;
        try {
            windowsIdentity = this.auth.logonUser(userName, password);
        } catch (final Exception e) {
            WindowsLoginModule.LOGGER.trace("", e);
            throw new LoginException(e.getMessage());
        }

        try {
            // disable guest login
            if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
                WindowsLoginModule.LOGGER.debug("guest login disabled: {}", windowsIdentity.getFqn());
                throw new LoginException("Guest login disabled");
            }

            this.principals = new LinkedHashSet<>();
            // add the main user principal to the subject principals
            this.principals.addAll(WindowsLoginModule.getUserPrincipals(windowsIdentity, this.principalFormat));
            if (this.roleFormat != PrincipalFormat.NONE) {
                // create the group principal and add roles as members of the group
                final GroupPrincipal groupList = new GroupPrincipal("Roles");
                for (final IWindowsAccount group : windowsIdentity.getGroups()) {
                    for (final Principal role : WindowsLoginModule.getRolePrincipals(group, this.roleFormat)) {
                        WindowsLoginModule.LOGGER.debug(" group: {}", role.getName());
                        groupList.addMember(new RolePrincipal(role.getName()));
                    }
                }
                // add the group and roles to the subject principals
                this.principals.add(groupList);
            }

            this.username = windowsIdentity.getFqn();
            WindowsLoginModule.LOGGER.debug("successfully logged in {} ({})", this.username,
                    windowsIdentity.getSidString());
        } finally {
            windowsIdentity.dispose();
        }

        return true;
    }

    /**
     * Abort a login process.
     *
     * @return true, if successful
     * @throws LoginException
     *             the login exception
     */
    @Override
    public boolean abort() throws LoginException {
        return this.logout();
    }

    /**
     * Commit principals to the subject.
     *
     * @return true, if successful
     * @throws LoginException
     *             the login exception
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

        WindowsLoginModule.LOGGER.debug("committing {} principals",
                Integer.valueOf(this.subject.getPrincipals().size()));
        if (this.debug) {
            for (final Principal principal : principalsSet) {
                WindowsLoginModule.LOGGER.debug(" principal: {}", principal.getName());
            }
        }

        return true;
    }

    /**
     * Logout a user.
     *
     * @return true, if successful
     * @throws LoginException
     *             the login exception
     */
    @Override
    public boolean logout() throws LoginException {
        if (this.subject.isReadOnly()) {
            throw new LoginException("Subject cannot be read-only.");
        }

        this.subject.getPrincipals().clear();

        if (this.username != null) {
            WindowsLoginModule.LOGGER.debug("logging out {}", this.username);
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

        final List<Principal> principalsList = new ArrayList<>();
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

        final List<Principal> principalsList = new ArrayList<>();
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
