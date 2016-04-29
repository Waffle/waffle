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

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Joiner;
import com.google.common.io.BaseEncoding;

import mockit.Deencapsulation;
import mockit.Tested;

/**
 * The Class NegotiateAuthenticationFilterTest.
 */
public final class NegotiateAuthenticationFilterTest {

    /** The neg auth filter. */
    @Tested
    private NegotiateAuthenticationFilter negAuthFilter;

    /** The response. */
    MockServletResponse                   response;

    /** The out. */
    private byte[]                        out;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        this.response = Mockito.mock(MockServletResponse.class, Mockito.CALLS_REAL_METHODS);
        Deencapsulation.setField(this.response, "headers", new HashMap<>());
        Deencapsulation.setField(this.response, "headersAdded", new HashMap<>());
    }

    /**
     * Test is login attempt.
     */
    @Test
    public void testIsLoginAttempt() {
        Assert.assertFalse(this.negAuthFilter.isLoginAttempt(""));
        Assert.assertTrue(this.negAuthFilter.isLoginAttempt("NEGOTIATe"));
        Assert.assertTrue(this.negAuthFilter.isLoginAttempt("ntlm"));
    }

    /**
     * Test send challenge during negotiate.
     */
    @Test
    public void testSendChallengeDuringNegotiate() {

        final String myProtocol = "myProtocol";

        this.out = new byte[1];
        this.out[0] = -1;

        this.negAuthFilter.sendChallengeDuringNegotiate(myProtocol, this.response, this.out);

        Assert.assertEquals(Joiner.on(" ").join(myProtocol, BaseEncoding.base64().encode(this.out)),
                this.response.headers.get("WWW-Authenticate"));

        Assert.assertEquals("keep-alive", this.response.headers.get("Connection"));

        Assert.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, this.response.sc);
        Assert.assertEquals(0, this.response.errorCode);

        Assert.assertFalse(this.response.isFlushed);
    }

    /**
     * Test send challenge initiate negotiate.
     */
    @Test
    public void testSendChallengeInitiateNegotiate() {

        this.out = new byte[1];
        this.out[0] = -1;

        this.negAuthFilter.sendChallengeInitiateNegotiate(this.response);

        Assert.assertEquals("Negotiate", this.response.headersAdded.get("WWW-Authenticate").get(0));
        Assert.assertEquals("NTLM", this.response.headersAdded.get("WWW-Authenticate").get(1));

        Assert.assertEquals("keep-alive", this.response.headers.get("Connection"));

        Assert.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, this.response.sc);
        Assert.assertEquals(0, this.response.errorCode);

        Assert.assertFalse(this.response.isFlushed);
    }

    /**
     * Test send challenge on failure.
     */
    @Test
    public void testSendChallengeOnFailure() {

        this.negAuthFilter.sendChallengeOnFailure(this.response);

        Assert.assertEquals("Negotiate", this.response.headersAdded.get("WWW-Authenticate").get(0));
        Assert.assertEquals("NTLM", this.response.headersAdded.get("WWW-Authenticate").get(1));

        Assert.assertEquals("close", this.response.headers.get("Connection"));

        Assert.assertEquals(0, this.response.sc);
        Assert.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, this.response.errorCode);

        Assert.assertTrue(this.response.isFlushed);
    }

}
