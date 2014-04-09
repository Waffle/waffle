/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dan Rollo
 *******************************************************************************/

package waffle.shiro.negotiate;

import junit.framework.TestCase;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author Dan Rollo
 *         Date: 2/14/13
 *         Time: 11:11 PM
 */
public final class NegotiateAuthenticationRealmTest extends TestCase {

    private NegotiateAuthenticationRealm negAuthRealm;

    @Override
    protected void setUp() throws Exception {
        negAuthRealm = new NegotiateAuthenticationRealm();
    }

    public void testSupports() {
        assertFalse("Non-NegotiateToken should not be supported.",
                negAuthRealm.supports(new AuthenticationToken() {
                    private static final long serialVersionUID = 334672725950031145L;

                    @Override
                    public Object getPrincipal() { return null; }

                    @Override
                    public Object getCredentials() { return null; }
                })
        );

        assertTrue(negAuthRealm.supports(new NegotiateToken(null, null, null, null, false, false, null)));
    }
}
