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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.BaseEncoding;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Dan Rollo Date: 2/14/13 Time: 11:11 PM
 */
public final class NegotiateAuthenticationFilterTest {

    private NegotiateAuthenticationFilter negAuthFilter;

    private MockServletResponse           response;
    private byte[]                        out;

    @Before
    public void setUp() {
        this.negAuthFilter = new NegotiateAuthenticationFilter();

        this.response = new MockServletResponse();
    }

    @Test
    public void testIsLoginAttempt() {
        Assert.assertFalse(this.negAuthFilter.isLoginAttempt(""));
        Assert.assertTrue(this.negAuthFilter.isLoginAttempt("NEGOTIATe"));
        Assert.assertTrue(this.negAuthFilter.isLoginAttempt("ntlm"));
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
    public void testSendChallengeDuringNegotiate() {

        final String myProtocol = "myProtocol";

        this.out = new byte[1];
        this.out[0] = -1;

        this.negAuthFilter.sendChallengeDuringNegotiate(myProtocol, this.response, this.out);

        Assert.assertEquals(myProtocol + " " + BaseEncoding.base64().encode(this.out),
                this.response.headers.get("WWW-Authenticate"));

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

    private static class MockServletResponse implements HttpServletResponse {
        private void notImplemented() {
            throw new RuntimeException("not implemented");
        }

        @Override
        public String getCharacterEncoding() {
            notImplemented();
            return null;
        }

        @Override
        public String getContentType() {
            notImplemented();
            return null;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            notImplemented();
            return null;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            notImplemented();
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {
            notImplemented();
        }

        @Override
        public void setContentLength(int len) {
            notImplemented();
        }

        @Override
        public void setContentType(String type) {
            notImplemented();
        }

        @Override
        public void setBufferSize(int size) {
            notImplemented();
        }

        @Override
        public int getBufferSize() {
            notImplemented();
            return 0;
        }

        boolean isFlushed;

        @Override
        public void flushBuffer() throws IOException {
            this.isFlushed = true;
        }

        @Override
        public void resetBuffer() {
            notImplemented();
        }

        @Override
        public boolean isCommitted() {
            notImplemented();
            return false;
        }

        @Override
        public void reset() {
            notImplemented();
        }

        @Override
        public void setLocale(Locale loc) {
            notImplemented();
        }

        @Override
        public Locale getLocale() {
            notImplemented();
            return null;
        }

        @Override
        public void addCookie(Cookie cookie) {
            notImplemented();
        }

        @Override
        public boolean containsHeader(String name) {
            notImplemented();
            return false;
        }

        @Override
        public String encodeURL(String url) {
            notImplemented();
            return null;
        }

        @Override
        public String encodeRedirectURL(String url) {
            notImplemented();
            return null;
        }

        @Deprecated
        @Override
        public String encodeUrl(String url) {
            notImplemented();
            return null;
        }

        @Deprecated
        @Override
        public String encodeRedirectUrl(String url) {
            notImplemented();
            return null;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            notImplemented();
        }

        int errorCode;

        @Override
        public void sendError(int sc) throws IOException {
            this.errorCode = sc;
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            notImplemented();
        }

        @Override
        public void setDateHeader(String name, long date) {
            notImplemented();
        }

        @Override
        public void addDateHeader(String name, long date) {
            notImplemented();
        }

        final Map<String, String> headers = new HashMap<String, String>();

        @Override
        public void setHeader(String name, String value) {
            this.headers.put(name, value);
        }

        final Map<String, List<String>> headersAdded = new HashMap<String, List<String>>();

        @Override
        public void addHeader(String name, String value) {
            if (this.headersAdded.containsKey(name)) {
                this.headersAdded.get(name).add(value);
                return;
            }

            List<String> values = new ArrayList<String>();
            values.add(value);
            this.headersAdded.put(name, values);
        }

        @Override
        public void setIntHeader(String name, int value) {
            notImplemented();
        }

        @Override
        public void addIntHeader(String name, int value) {
            notImplemented();
        }

        int sc;

        @Override
        public void setStatus(int sc) {
            this.sc = sc;
        }

        @Deprecated
        @Override
        public void setStatus(int sc, String sm) {
            notImplemented();
        }
    }

}
