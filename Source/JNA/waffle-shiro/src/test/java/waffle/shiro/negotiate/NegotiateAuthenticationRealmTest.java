/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.shiro.negotiate;

import junit.framework.TestCase;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * The Class NegotiateAuthenticationRealmTest.
 *
 * @author Dan Rollo Date: 2/14/13 Time: 11:11 PM
 */
public final class NegotiateAuthenticationRealmTest extends TestCase {

    /** The neg auth realm. */
    private NegotiateAuthenticationRealm negAuthRealm;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        this.negAuthRealm = new NegotiateAuthenticationRealm();
    }

    /**
     * Test supports.
     */
    public void testSupports() {
        TestCase.assertFalse("Non-NegotiateToken should not be supported.",
                this.negAuthRealm.supports(new AuthenticationToken() {
                    private static final long serialVersionUID = 334672725950031145L;

                    @Override
                    public Object getPrincipal() {
                        return null;
                    }

                    @Override
                    public Object getCredentials() {
                        return null;
                    }
                }));

        TestCase.assertTrue(this.negAuthRealm.supports(new NegotiateToken(null, null, null, null, false, false, null)));
    }
}
