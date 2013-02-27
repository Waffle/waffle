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
 * @author Dan Rollo
 * Date: 2/26/13
 * Time: 12:41 AM
 */
public class DynamicAuthenticationStrategy extends FirstSuccessfulStrategy {

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
