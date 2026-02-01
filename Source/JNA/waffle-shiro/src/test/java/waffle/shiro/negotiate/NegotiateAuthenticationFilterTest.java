/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.shiro.negotiate;

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
    private MockServletResponse response;

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

        Assertions.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, this.response.sc);
        Assertions.assertEquals(0, this.response.errorCode);

        Assertions.assertFalse(this.response.isFlushed);
    }

    /**
     * Test send challenge initiate negotiate.
     */
    @Test
    void testSendChallengeInitiateNegotiate() {

        this.out = new byte[1];
        this.out[0] = -1;

        this.negAuthFilter.sendChallengeInitiateNegotiate(this.response);

        Assertions.assertEquals("Negotiate", this.response.headersAdded.get("WWW-Authenticate").get(0));
        Assertions.assertEquals("NTLM", this.response.headersAdded.get("WWW-Authenticate").get(1));

        Assertions.assertEquals("keep-alive", this.response.headers.get("Connection"));

        Assertions.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, this.response.sc);
        Assertions.assertEquals(0, this.response.errorCode);

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

        Assertions.assertEquals(0, this.response.sc);
        Assertions.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, this.response.errorCode);

        Assertions.assertTrue(this.response.isFlushed);
    }

}
