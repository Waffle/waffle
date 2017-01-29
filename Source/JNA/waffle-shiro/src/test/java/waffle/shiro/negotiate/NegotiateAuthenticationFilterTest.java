/*
 * MIT License
 *
 * Copyright (c) 2010-2023 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import mockit.Tested;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

/**
 * The Class NegotiateAuthenticationFilterTest.
 */
@DisabledOnJre(JRE.JAVA_21)
class NegotiateAuthenticationFilterTest {

    /** The neg auth filter. */
    @Tested
    private NegotiateAuthenticationFilter negAuthFilter;

    /** The response. */
    MockServletResponse response;

    /** The out. */
    private byte[] out;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.response = Mockito.mock(MockServletResponse.class, Mockito.CALLS_REAL_METHODS);
        Whitebox.setInternalState(this.response, "headers", new HashMap<>());
        Whitebox.setInternalState(this.response, "headersAdded", new HashMap<>());
    }

    /**
     * Test is login attempt.
     */
    @Test
    void testIsLoginAttempt() {
        Assertions.assertFalse(this.negAuthFilter.isLoginAttempt(""));
        Assertions.assertTrue(this.negAuthFilter.isLoginAttempt("NEGOTIATe"));
        Assertions.assertTrue(this.negAuthFilter.isLoginAttempt("ntlm"));
    }

    /**
     * Test send challenge during negotiate.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testSendChallengeDuringNegotiate() {

        final String myProtocol = "myProtocol";

        this.out = new byte[1];
        this.out[0] = -1;

        this.negAuthFilter.sendChallengeDuringNegotiate(myProtocol, this.response, this.out);

        Assertions.assertEquals(String.join(" ", myProtocol, Base64.getEncoder().encodeToString(this.out)),
                this.response.headers.get("WWW-Authenticate"));

        Assertions.assertEquals("keep-alive", this.response.headers.get("Connection"));

        Assertions.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, this.response.errorCode);

        Assertions.assertFalse(this.response.isFlushed);
    }

    /**
     * Test send challenge initiate negotiate.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testSendChallengeInitiateNegotiate() {

        this.out = new byte[1];
        this.out[0] = -1;

        this.negAuthFilter.sendChallengeInitiateNegotiate(this.response);

        Assertions.assertEquals("Negotiate", this.response.headersAdded.get("WWW-Authenticate").get(0));
        Assertions.assertEquals("NTLM", this.response.headersAdded.get("WWW-Authenticate").get(1));

        Assertions.assertEquals("keep-alive", this.response.headers.get("Connection"));

        Assertions.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, this.response.errorCode);

        Assertions.assertFalse(this.response.isFlushed);
    }

    /**
     * Test send challenge on failure.
     */
    @Test
    void testSendChallengeOnFailure() {

        this.negAuthFilter.sendChallengeOnFailure(this.response);

        Assertions.assertEquals("Negotiate", this.response.headersAdded.get("WWW-Authenticate").get(0));
        Assertions.assertEquals("NTLM", this.response.headersAdded.get("WWW-Authenticate").get(1));

        Assertions.assertEquals("close", this.response.headers.get("Connection"));

        Assertions.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, this.response.errorCode);

        Assertions.assertTrue(this.response.isFlushed);
    }

}
