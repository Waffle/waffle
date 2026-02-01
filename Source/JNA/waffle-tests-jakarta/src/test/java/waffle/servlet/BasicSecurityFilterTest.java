/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.security.auth.Subject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.mock.MockWindowsAuthProvider;
import waffle.mock.http.SimpleFilterChain;
import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * Waffle Tomcat Security Filter Test.
 */
class BasicSecurityFilterTest {

    /** The filter. */
    private NegotiateSecurityFilter filter;

    /**
     * Sets the up.
     *
     * @throws ServletException
     *             the servlet exception
     */
    @BeforeEach
    void setUp() throws ServletException {
        this.filter = new NegotiateSecurityFilter();
        this.filter.setAuth(new MockWindowsAuthProvider());
        this.filter.init(null);
    }

    /**
     * Tear down.
     */
    @AfterEach
    void tearDown() {
        this.filter.destroy();
    }

    /**
     * Test basic auth.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testBasicAuth() throws IOException, ServletException {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");

        final String userHeaderValue = WindowsAccountImpl.getCurrentUsername() + ":password";
        final String basicAuthHeader = "Basic "
                + Base64.getEncoder().encodeToString(userHeaderValue.getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", basicAuthHeader);

        final SimpleHttpResponse response = new SimpleHttpResponse();
        final FilterChain filterChain = new SimpleFilterChain();
        this.filter.doFilter(request, response, filterChain);
        final Subject subject = (Subject) request.getSession(false).getAttribute("javax.security.auth.subject");
        Assertions.assertNotNull(subject);
        assertThat(subject.getPrincipals().size()).isPositive();
    }
}
