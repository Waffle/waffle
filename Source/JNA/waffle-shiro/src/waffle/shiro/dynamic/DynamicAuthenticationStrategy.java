/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dan Rollo
 *******************************************************************************/

package waffle.shiro.dynamic;

import waffle.shiro.negotiate.AuthenticationInProgressException;
import waffle.shiro.negotiate.NegotiateAuthenticationRealm;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.realm.Realm;

/**
 * Custom authentication strategy for the Negotiate logic required for SSO/Negotiate auth realm
 * {@link NegotiateAuthenticationRealm}. When the negotiate logic is executing, one of more round trips with the client
 * occur. When the {@link NegotiateAuthenticationRealm negotiate realm} determines another handshake is needed, it
 * throws the exception: {@link AuthenticationInProgressException}. This custom strategy detects this exception,
 * and immediately re-throws it so classes higher up in the call stack will allow the handshake to proceed.
 * Without this added logic, the handshake would be halted after the first connection by the existing error handling.
 * This strategy is needed when using {@link DynamicAuthenticationStrategy} and more than one realm is configured in
 * shiro.ini.
 *
 * @author Dan Rollo
 * Date: 2/26/13
 * Time: 12:41 AM
 */
public class DynamicAuthenticationStrategy extends FirstSuccessfulStrategy {

    /**
     * When the negotiate logic is executing, one of more round trips with the client
     * occur. When the {@link NegotiateAuthenticationRealm negotiate realm} determines another handshake is needed, it
     * throws the exception: {@link AuthenticationInProgressException}. This custom strategy detects this exception,
     * and immediately re-throws it so classes higher up in the call stack will allow the handshake to proceed.
     * Without this added logic, the handshake would be halted after the first connection by the existing error handling.
     *
     * {@inheritDoc}
     */
    @Override
    public AuthenticationInfo afterAttempt(final Realm realm, final AuthenticationToken token, final AuthenticationInfo singleRealmInfo,
                                           final AuthenticationInfo aggregateInfo, final Throwable t)
            throws AuthenticationException {

        if (realm instanceof NegotiateAuthenticationRealm && t instanceof AuthenticationInProgressException) {
            // propagate exception upward as is, to signal continue is needed
            throw (AuthenticationInProgressException)t;
        }

        return super.afterAttempt(realm, token, singleRealmInfo, aggregateInfo, t);
    }
}
