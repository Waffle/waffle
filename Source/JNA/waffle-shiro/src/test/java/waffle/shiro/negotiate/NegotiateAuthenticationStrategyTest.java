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

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class NegotiateAuthenticationStrategyTest.
 *
 * @author Dan Rollo Date: 3/18/13 Time: 3:34 PM
 */
public class NegotiateAuthenticationStrategyTest {

    /** The auth strategy. */
    private NegotiateAuthenticationStrategy authStrategy;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.authStrategy = new NegotiateAuthenticationStrategy();
    }

    /**
     * Test after attempt.
     */
    @Test
    void testAfterAttempt() {

        final Realm otherRealm = new IniRealm();

        this.authStrategy.afterAttempt(otherRealm, null, null, null, new RuntimeException());

        final AuthenticationInProgressException authInProgressException = new AuthenticationInProgressException();

        this.authStrategy.afterAttempt(otherRealm, null, null, null, authInProgressException);

        final Throwable exception = Assertions.assertThrows(AuthenticationInProgressException.class, () -> {
            this.authStrategy.afterAttempt(new NegotiateAuthenticationRealm(), null, null, null,
                    authInProgressException);
        });
        Assertions.assertNull(exception.getMessage());
    }
}
