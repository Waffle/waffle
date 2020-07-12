/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.spring;

import java.io.IOException;

import javax.servlet.ServletException;

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
 * The Class NegotiateSecurityFilterEntryPointTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterEntryPointTests {

    /** The entry point. */
    private NegotiateSecurityFilterEntryPoint entryPoint;

    /** The ctx. */
    private ApplicationContext ctx;

    /**
     * Sets the up.
     */
    @BeforeEach
    public void setUp() {
        final String[] configFiles = new String[] { "springTestFilterBeans.xml" };
        this.ctx = new ClassPathXmlApplicationContext(configFiles);
        this.entryPoint = (NegotiateSecurityFilterEntryPoint) this.ctx.getBean("negotiateSecurityFilterEntryPoint");
    }

    /**
     * Shut down.
     */
    @AfterEach
    public void shutDown() {
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
    public void testChallengeGET() throws IOException, ServletException {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.entryPoint.commence(request, response, null);
        final String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
        Assertions.assertEquals(3, wwwAuthenticates.length);
        Assertions.assertEquals("NTLM", wwwAuthenticates[0]);
        Assertions.assertEquals("Negotiate", wwwAuthenticates[1]);
        Assertions.assertTrue(wwwAuthenticates[2].equals("Basic realm=\"TestRealm\""));
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
    public void testGetSetProvider() throws IOException, ServletException {
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
