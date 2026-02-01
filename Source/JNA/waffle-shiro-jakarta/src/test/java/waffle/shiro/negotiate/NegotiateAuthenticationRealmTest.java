/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
 */
class NegotiateAuthenticationRealmTest {

    /** The neg auth realm. */
    @Tested
    private NegotiateAuthenticationRealm negAuthRealm;

    @Mocked
    private AuthenticationToken authenticationToken;

    /**
     * Test supports.
     */
    @Test
    void testSupports() {
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
    void testAuthenticationInfo(@Mocked final NegotiateToken negotiateToken) {
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
