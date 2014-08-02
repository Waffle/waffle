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
    private static final Logger  LOGGER     = LoggerFactory.getLogger(AbstractWaffleRealm.class);
    private static final String  REALM_NAME = "WAFFLE";

    private IWindowsAuthProvider provider   = new WindowsAuthProviderImpl();

    @Override
    protected final AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken)
            throws AuthenticationException {
        AuthenticationInfo authenticationInfo = null;
        if (authToken instanceof UsernamePasswordToken) {
            UsernamePasswordToken token = (UsernamePasswordToken) authToken;
            String username = token.getUsername();
            IWindowsIdentity identity = null;
            try {
                LOGGER.debug("Attempting login for user {}", username);
                identity = this.provider.logonUser(username, new String(token.getPassword()));
                if (identity.isGuest()) {
                    LOGGER.debug("Guest identity for user {}; denying access", username);
                    throw new AuthenticationException("Guest identities are not allowed access");
                }
                Object principal = new WaffleFqnPrincipal(identity);
                authenticationInfo = buildAuthenticationInfo(token, principal);
                LOGGER.debug("Successful login for user {}", username);
            } catch (RuntimeException e) {
                LOGGER.debug("Failed login for user {}: {}", username, e.getMessage());
                LOGGER.trace("{}", e);
                throw new AuthenticationException("Login failed", e);
            } finally {
                if (identity != null) {
                    identity.dispose();
                }
            }
        }
        return authenticationInfo;
    }

    private AuthenticationInfo buildAuthenticationInfo(UsernamePasswordToken token, Object principal) {
        AuthenticationInfo authenticationInfo;
        HashingPasswordService hashService = getHashService();
        if (hashService != null) {
            Hash hash = hashService.hashPassword(token.getPassword());
            ByteSource salt = hash.getSalt();
            authenticationInfo = new SimpleAuthenticationInfo(principal, hash, salt, REALM_NAME);
        } else {
            Object creds = token.getCredentials();
            authenticationInfo = new SimpleAuthenticationInfo(principal, creds, REALM_NAME);
        }
        return authenticationInfo;
    }

    @Override
    protected final AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        WaffleFqnPrincipal principal = principals.oneByType(WaffleFqnPrincipal.class);
        return principal == null ? null : buildAuthorizationInfo(principal);
    }

    /**
     * Assembles the appropriate authorization information for the specified principal.
     * 
     * @param principal
     *            the principal for which to assemble authorization information
     * @return the authorization information for the specified principal
     */
    protected abstract AuthorizationInfo buildAuthorizationInfo(WaffleFqnPrincipal principal);

    /**
     * Allow overriding the default implementation of {@link IWindowsAuthProvider} This is only needed for testing,
     * since for normal usage the default is what you want.
     */
    void setProvider(IWindowsAuthProvider value) {
        this.provider = value;
    }

    private HashingPasswordService getHashService() {
        CredentialsMatcher matcher = getCredentialsMatcher();
        if (matcher instanceof PasswordMatcher) {
            PasswordMatcher passwordMatcher = (PasswordMatcher) matcher;
            PasswordService passwordService = passwordMatcher.getPasswordService();
            if (passwordService instanceof HashingPasswordService) {
                return (HashingPasswordService) passwordService;
            }
        }
        return null;
    }
}
