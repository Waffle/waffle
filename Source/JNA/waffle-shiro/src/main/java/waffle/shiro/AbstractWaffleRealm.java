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
package waffle.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashingPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A {@link org.apache.shiro.realm.Realm} that authenticates with Active Directory using WAFFLE. Authorization is left
 * for subclasses to define by implementing the {@link #buildAuthorizationInfo} method.
 */
public abstract class AbstractWaffleRealm extends AuthorizingRealm {

    /** The Constant LOGGER. */
    private static final Logger  LOGGER     = LoggerFactory.getLogger(AbstractWaffleRealm.class);

    /** The Constant REALM_NAME. */
    private static final String  REALM_NAME = "WAFFLE";

    /** The provider. */
    private IWindowsAuthProvider provider   = new WindowsAuthProviderImpl();

    /*
     * (non-Javadoc)
     * @see
     * org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
     */
    @Override
    protected final AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authToken) {
        AuthenticationInfo authenticationInfo = null;
        if (authToken instanceof UsernamePasswordToken) {
            final UsernamePasswordToken token = (UsernamePasswordToken) authToken;
            final String username = token.getUsername();
            IWindowsIdentity identity = null;
            try {
                AbstractWaffleRealm.LOGGER.debug("Attempting login for user {}", username);
                identity = this.provider.logonUser(username, new String(token.getPassword()));
                if (identity.isGuest()) {
                    AbstractWaffleRealm.LOGGER.debug("Guest identity for user {}; denying access", username);
                    throw new AuthenticationException("Guest identities are not allowed access");
                }
                final Object principal = new WaffleFqnPrincipal(identity);
                authenticationInfo = this.buildAuthenticationInfo(token, principal);
                AbstractWaffleRealm.LOGGER.debug("Successful login for user {}", username);
            } catch (final RuntimeException e) {
                AbstractWaffleRealm.LOGGER.debug("Failed login for user {}: {}", username, e.getMessage());
                AbstractWaffleRealm.LOGGER.trace("", e);
                throw new AuthenticationException("Login failed", e);
            } finally {
                if (identity != null) {
                    identity.dispose();
                }
            }
        }
        return authenticationInfo;
    }

    /**
     * Builds the authentication info.
     *
     * @param token
     *            the token
     * @param principal
     *            the principal
     * @return the authentication info
     */
    private AuthenticationInfo buildAuthenticationInfo(final UsernamePasswordToken token, final Object principal) {
        AuthenticationInfo authenticationInfo;
        final HashingPasswordService hashService = this.getHashService();
        if (hashService != null) {
            final Hash hash = hashService.hashPassword(token.getPassword());
            final ByteSource salt = hash.getSalt();
            authenticationInfo = new SimpleAuthenticationInfo(principal, hash, salt, AbstractWaffleRealm.REALM_NAME);
        } else {
            final Object creds = token.getCredentials();
            authenticationInfo = new SimpleAuthenticationInfo(principal, creds, AbstractWaffleRealm.REALM_NAME);
        }
        return authenticationInfo;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
     */
    @Override
    protected final AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
        final WaffleFqnPrincipal principal = principals.oneByType(WaffleFqnPrincipal.class);
        return principal == null ? null : this.buildAuthorizationInfo(principal);
    }

    /**
     * Assembles the appropriate authorization information for the specified principal.
     * 
     * @param principal
     *            the principal for which to assemble authorization information
     * @return the authorization information for the specified principal
     */
    protected abstract AuthorizationInfo buildAuthorizationInfo(final WaffleFqnPrincipal principal);

    /**
     * Allow overriding the default implementation of {@link IWindowsAuthProvider} This is only needed for testing,
     * since for normal usage the default is what you want.
     * 
     * @param value
     *            the windows authorization provider
     */
    void setProvider(final IWindowsAuthProvider value) {
        this.provider = value;
    }

    /**
     * Gets the hash service.
     *
     * @return the hash service
     */
    private HashingPasswordService getHashService() {
        final CredentialsMatcher matcher = this.getCredentialsMatcher();
        if (matcher instanceof PasswordMatcher) {
            final PasswordMatcher passwordMatcher = (PasswordMatcher) matcher;
            final PasswordService passwordService = passwordMatcher.getPasswordService();
            if (passwordService instanceof HashingPasswordService) {
                return (HashingPasswordService) passwordService;
            }
        }
        return null;
    }
}
