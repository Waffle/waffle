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
