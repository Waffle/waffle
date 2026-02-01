/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring;

import jakarta.servlet.ServletException;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;

/**
 * The Class NegotiateSecurityFilterEntryPointTest.
 */
class NegotiateSecurityFilterEntryPointTest {

    /** The entry point. */
    private NegotiateSecurityFilterEntryPoint entryPoint;

    /** The ctx. */
    private ApplicationContext ctx;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        final String[] configFiles = new String[] { "springTestFilterBeans.xml" };
        this.ctx = new ClassPathXmlApplicationContext(configFiles);
        this.entryPoint = (NegotiateSecurityFilterEntryPoint) this.ctx.getBean("negotiateSecurityFilterEntryPoint");
    }

    /**
     * Shut down.
     */
    @AfterEach
    void shutDown() {
        ((AbstractApplicationContext) this.ctx).close();
    }

    /**
     * Test challenge get.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testChallengeGET() throws IOException, ServletException {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.entryPoint.commence(request, response, null);
        final String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
        Assertions.assertEquals(3, wwwAuthenticates.length);
        Assertions.assertEquals("NTLM", wwwAuthenticates[0]);
        Assertions.assertEquals("Negotiate", wwwAuthenticates[1]);
        Assertions.assertEquals("Basic realm=\"TestRealm\"", wwwAuthenticates[2]);
        Assertions.assertEquals(2, response.getHeaderNamesSize());
        Assertions.assertEquals("keep-alive", response.getHeader("Connection"));
        Assertions.assertEquals(401, response.getStatus());
    }

    /**
     * Test get set provider.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testGetSetProvider() throws IOException, ServletException {
        Assertions.assertNotNull(this.entryPoint.getProvider());
        this.entryPoint.setProvider(null);
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        final Throwable exception = Assertions.assertThrows(ServletException.class, () -> {
            this.entryPoint.commence(request, response, null);
        });
        Assertions.assertEquals("Missing NegotiateEntryPoint.Provider", exception.getMessage());
    }
}
