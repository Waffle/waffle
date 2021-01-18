/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWaffleRealm.class);

    /** The Constant REALM_NAME. */
    private static final String REALM_NAME = "WAFFLE";

    /** The provider. */
    private IWindowsAuthProvider provider = new WindowsAuthProviderImpl();

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
