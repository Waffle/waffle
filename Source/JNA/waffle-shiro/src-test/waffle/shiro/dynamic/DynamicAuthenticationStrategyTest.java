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

import junit.framework.TestCase;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import waffle.shiro.negotiate.AuthenticationInProgressException;
import waffle.shiro.negotiate.NegotiateAuthenticationRealm;

/**
 * @author Dan Rollo
 * Date: 2/26/13
 * Time: 11:33 PM
 */
public class DynamicAuthenticationStrategyTest extends TestCase {

    private DynamicAuthenticationStrategy dynamicAuthenticationStrategy;


    protected void setUp() {
        dynamicAuthenticationStrategy = new DynamicAuthenticationStrategy();
    }

    public void testAfterAttempt() throws Exception {

        final Realm otherRealm = new IniRealm();

        dynamicAuthenticationStrategy.afterAttempt(otherRealm, null, null, null, new RuntimeException());


        final AuthenticationInProgressException authInProgressException = new AuthenticationInProgressException();

        dynamicAuthenticationStrategy.afterAttempt(otherRealm, null, null, null, authInProgressException);

        try {
            dynamicAuthenticationStrategy.afterAttempt(new NegotiateAuthenticationRealm(), null, null, null, authInProgressException);
            fail();
        } catch (AuthenticationInProgressException e) {
            // this is expected
        }
    }
}
