/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.shiro.negotiate;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    AuthenticationToken authenticationToken;

    /**
     * Test supports.
     */
    @Test
    public void testSupports() {
        Assertions.assertFalse(this.negAuthRealm.supports(this.authenticationToken),
                "Non-NegotiateToken should not be supported.");

        Assertions
                .assertTrue(this.negAuthRealm.supports(new NegotiateToken(null, null, null, null, false, false, null)));
    }

    /**
     * Test authentication info exception.
     *
     * @param negotiateToken
     *            the negotiate token
     */
    @Test
    public void testAuthenticationInfo(@Mocked final NegotiateToken negotiateToken) {
        Assertions.assertNotNull(new Expectations() {
            {
                negotiateToken.getIn();
                this.result = Byte.valueOf((byte) 0);
            }
        });
        Assertions.assertThrows(AuthenticationException.class, () -> {
            this.negAuthRealm.doGetAuthenticationInfo(negotiateToken);
        });
    }
}
