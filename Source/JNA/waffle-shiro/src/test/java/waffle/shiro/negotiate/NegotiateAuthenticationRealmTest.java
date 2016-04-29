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

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.junit.Assert;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;

/**
 * The Class NegotiateAuthenticationRealmTest.
 *
 * @author Dan Rollo Date: 2/14/13 Time: 11:11 PM
 */
public final class NegotiateAuthenticationRealmTest {

    /** The neg auth realm. */
    @Tested
    private NegotiateAuthenticationRealm negAuthRealm;

    @Mocked
    AuthenticationToken                  authenticationToken;

    /**
     * Test supports.
     */
    @Test
    public void testSupports() {
        Assert.assertFalse("Non-NegotiateToken should not be supported.",
                this.negAuthRealm.supports(this.authenticationToken));

        Assert.assertTrue(this.negAuthRealm.supports(new NegotiateToken(null, null, null, null, false, false, null)));
    }

    /**
     * Test authentication info exception.
     *
     * @param negotiateToken
     *            the negotiate token
     */
    @Test(expected = AuthenticationException.class)
    public void testAuthenticationInfo(@Mocked final NegotiateToken negotiateToken) {
        Assert.assertNotNull(new Expectations() {
            {
                negotiateToken.getIn();
                this.result = new Byte((byte) 0);
            }
        });
        this.negAuthRealm.doGetAuthenticationInfo(negotiateToken);
    }
}
