/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import mockit.Deencapsulation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Joiner;
import com.google.common.io.BaseEncoding;

/**
 * @author Dan Rollo Date: 2/14/13 Time: 11:11 PM
 */
public final class NegotiateAuthenticationFilterTest {

    private static abstract class MockServletResponse implements HttpServletResponse {

        boolean                         isFlushed;

        int                             errorCode;

        final Map<String, String>       headers      = new HashMap<String, String>();

        final Map<String, List<String>> headersAdded = new HashMap<String, List<String>>();

        int                             sc;

        @Override
        public void addHeader(final String name, final String value) {
            if (this.headersAdded.containsKey(name)) {
                this.headersAdded.get(name).add(value);
                return;
            }

            final List<String> values = new ArrayList<String>();
            values.add(value);
            this.headersAdded.put(name, values);
        }

        @Override
        public void flushBuffer() throws IOException {
            this.isFlushed = true;
        }

        @Override
        public void sendError(final int sendError) throws IOException {
            this.errorCode = sendError;
        }

        @Override
        public void setHeader(final String name, final String value) {
            this.headers.put(name, value);
        }

        @Override
        public void setStatus(final int status) {
            this.sc = status;
        }

    }

    private NegotiateAuthenticationFilter negAuthFilter;
    private MockServletResponse           response;

    private byte[]                        out;

    @Before
    public void setUp() {
        this.negAuthFilter = new NegotiateAuthenticationFilter();

        this.response = Mockito.mock(MockServletResponse.class, Mockito.CALLS_REAL_METHODS);
        Deencapsulation.setField(this.response, "headers", new HashMap<String, String>());
        Deencapsulation.setField(this.response, "headersAdded", new HashMap<String, String>());
    }

    @Test
    public void testIsLoginAttempt() {
        Assert.assertFalse(this.negAuthFilter.isLoginAttempt(""));
        Assert.assertTrue(this.negAuthFilter.isLoginAttempt("NEGOTIATe"));
        Assert.assertTrue(this.negAuthFilter.isLoginAttempt("ntlm"));
    }

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
