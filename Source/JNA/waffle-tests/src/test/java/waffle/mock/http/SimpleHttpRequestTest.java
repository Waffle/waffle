/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import java.security.Principal;
import java.util.Enumeration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SimpleHttpRequest}.
 */
class SimpleHttpRequestTest {

    /** The request. */
    private SimpleHttpRequest request;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        SimpleHttpRequest.resetRemotePort();
        this.request = new SimpleHttpRequest();
    }

    /**
     * Test default method is GET.
     */
    @Test
    void testDefaultMethodIsGet() {
        Assertions.assertEquals("GET", this.request.getMethod());
    }

    /**
     * Test set method.
     */
    @Test
    void testSetMethod() {
        this.request.setMethod("POST");
        Assertions.assertEquals("POST", this.request.getMethod());
    }

    /**
     * Test add and get header.
     */
    @Test
    void testAddAndGetHeader() {
        this.request.addHeader("Authorization", "Bearer token");
        Assertions.assertEquals("Bearer token", this.request.getHeader("Authorization"));
    }

    /**
     * Test header names.
     */
    @Test
    void testHeaderNames() {
        this.request.addHeader("X-Header1", "v1");
        this.request.addHeader("X-Header2", "v2");
        final Enumeration<String> names = this.request.getHeaderNames();
        Assertions.assertNotNull(names);
        int count = 0;
        while (names.hasMoreElements()) {
            names.nextElement();
            count++;
        }
        Assertions.assertEquals(2, count);
    }

    /**
     * Test content length with null content.
     */
    @Test
    void testContentLengthNullContent() {
        Assertions.assertEquals(-1, this.request.getContentLength());
    }

    /**
     * Test set content length.
     */
    @Test
    void testSetContentLength() {
        this.request.setContentLength(100);
        Assertions.assertEquals(100, this.request.getContentLength());
    }

    /**
     * Test remote port increments.
     */
    @Test
    void testRemotePortIncrements() {
        final int port1 = this.request.getRemotePort();
        final SimpleHttpRequest request2 = new SimpleHttpRequest();
        final int port2 = request2.getRemotePort();
        Assertions.assertTrue(port2 > port1);
    }

    /**
     * Test set and get remote user.
     */
    @Test
    void testSetAndGetRemoteUser() {
        Assertions.assertNull(this.request.getRemoteUser());
        this.request.setRemoteUser("user1");
        Assertions.assertEquals("user1", this.request.getRemoteUser());
    }

    /**
     * Test get session.
     */
    @Test
    void testGetSession() {
        Assertions.assertNotNull(this.request.getSession());
        Assertions.assertNotNull(this.request.getSession(true));
        Assertions.assertNotNull(this.request.getSession(false));
    }

    /**
     * Test set and get query string.
     */
    @Test
    void testSetAndGetQueryString() {
        Assertions.assertNull(this.request.getQueryString());
        this.request.setQueryString("key=value&other=data");
        Assertions.assertEquals("key=value&other=data", this.request.getQueryString());
        Assertions.assertEquals("value", this.request.getParameter("key"));
        Assertions.assertEquals("data", this.request.getParameter("other"));
    }

    /**
     * Test query string with empty value.
     */
    @Test
    void testQueryStringWithEmptyValue() {
        this.request.setQueryString("key=");
        Assertions.assertEquals("", this.request.getParameter("key"));
    }

    /**
     * Test set and get request uri.
     */
    @Test
    void testSetAndGetRequestUri() {
        Assertions.assertNull(this.request.getRequestURI());
        this.request.setRequestURI("/my/resource");
        Assertions.assertEquals("/my/resource", this.request.getRequestURI());
    }

    /**
     * Test add and get parameter.
     */
    @Test
    void testAddAndGetParameter() {
        this.request.addParameter("param1", "val1");
        Assertions.assertEquals("val1", this.request.getParameter("param1"));
        Assertions.assertNull(this.request.getParameter("notset"));
    }

    /**
     * Test set and get remote host.
     */
    @Test
    void testSetAndGetRemoteHost() {
        Assertions.assertNull(this.request.getRemoteHost());
        this.request.setRemoteHost("example.com");
        Assertions.assertEquals("example.com", this.request.getRemoteHost());
    }

    /**
     * Test set and get remote addr.
     */
    @Test
    void testSetAndGetRemoteAddr() {
        Assertions.assertNull(this.request.getRemoteAddr());
        this.request.setRemoteAddr("192.168.0.1");
        Assertions.assertEquals("192.168.0.1", this.request.getRemoteAddr());
    }

    /**
     * Test set and get user principal.
     */
    @Test
    void testSetAndGetUserPrincipal() {
        Assertions.assertNull(this.request.getUserPrincipal());
        final Principal principal = () -> "testuser";
        this.request.setUserPrincipal(principal);
        Assertions.assertEquals(principal, this.request.getUserPrincipal());
    }

    /**
     * Test reset remote port resets counter.
     */
    @Test
    void testResetRemotePort() {
        SimpleHttpRequest.resetRemotePort();
        final SimpleHttpRequest fresh = new SimpleHttpRequest();
        Assertions.assertEquals(1, fresh.getRemotePort());
    }

    /**
     * Test set null query string.
     */
    @Test
    void testSetNullQueryString() {
        Assertions.assertDoesNotThrow(() -> this.request.setQueryString(null));
        Assertions.assertNull(this.request.getQueryString());
    }
}
