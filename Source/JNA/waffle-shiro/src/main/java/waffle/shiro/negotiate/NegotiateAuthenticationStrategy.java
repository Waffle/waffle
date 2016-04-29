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
package waffle.shiro.negotiate;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.AbstractAuthenticationStrategy;
import org.apache.shiro.realm.Realm;

/**
 * Custom authentication strategy for the negotiate logic required for SSO/Negotiate auth realm
 * {@link NegotiateAuthenticationRealm}. When the negotiate logic is executing, one of more round trips with the client
 * occur. When the {@link NegotiateAuthenticationRealm} determines another handshake is needed, it throws the exception:
 * {@link AuthenticationInProgressException}. This custom strategy detects this exception, and immediately re-throws it
 * so classes higher up in the call stack will allow the handshake to proceed. Without this added logic, the handshake
 * could be halted after the first connection by the existing error handling. <br>
 * <br>
 * This strategy is needed when using {@link NegotiateAuthenticationFilter} and more than one realm is configured in
 * shiro.ini. If only one realm is defined, the current error handling in
 * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator#doSingleRealmAuthentication(org.apache.shiro.realm.Realm, org.apache.shiro.authc.AuthenticationToken)}
 * works fine without requiring this strategy. However, the current error handling in
 * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator#doMultiRealmAuthentication(java.util.Collection, org.apache.shiro.authc.AuthenticationToken)}
 * does require the {@link NegotiateAuthenticationStrategy} to ensure negotiate 'continue' calls will proceed. So for
 * now, the most reliable approach is to use this strategy.
 *
 * @author Dan Rollo Date: 3/18/13 Time: 3:31 PM
 * @see NegotiateAuthenticationStrategy#afterAttempt(org.apache.shiro.realm.Realm,
 *      org.apache.shiro.authc.AuthenticationToken, org.apache.shiro.authc.AuthenticationInfo,
 *      org.apache.shiro.authc.AuthenticationInfo, Throwable)
 */
public class NegotiateAuthenticationStrategy extends AbstractAuthenticationStrategy {

    /**
     * When the negotiate logic is executing, one of more round trips with the client occur. When the
     * {@link NegotiateAuthenticationRealm negotiate realm} determines another handshake is needed, it throws the
     * exception: {@link AuthenticationInProgressException}. This custom strategy detects this exception, and
     * immediately re-throws it so classes higher up in the call stack will allow the handshake to proceed. Without this
     * added logic, the handshake would be halted after the first connection by the existing error handling.
     * 
     * {@inheritDoc}
     */
    @Override
    public AuthenticationInfo afterAttempt(final Realm realm, final AuthenticationToken token,
            final AuthenticationInfo singleRealmInfo, final AuthenticationInfo aggregateInfo, final Throwable t) {

        if (realm instanceof NegotiateAuthenticationRealm && t instanceof AuthenticationInProgressException) {
            // propagate exception upward as is, to signal continue is needed
            throw (AuthenticationInProgressException) t;
        }

        return super.afterAttempt(realm, token, singleRealmInfo, aggregateInfo, t);
    }
}
