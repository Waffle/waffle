/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.spring;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterEntryPointTests {

    private NegotiateSecurityFilterEntryPoint entryPoint;
    private ApplicationContext                ctx;

    @Before
    public void setUp() {
        String[] configFiles = new String[] { "springTestFilterBeans.xml" };
        this.ctx = new ClassPathXmlApplicationContext(configFiles);
        this.entryPoint = (NegotiateSecurityFilterEntryPoint) this.ctx.getBean("negotiateSecurityFilterEntryPoint");
    }

    @After
    public void shutDown() {
        ((AbstractApplicationContext) this.ctx).close();
    }

    @Test
    public void testChallengeGET() throws IOException, ServletException {
        SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        SimpleHttpResponse response = new SimpleHttpResponse();
        this.entryPoint.commence(request, response, null);
        String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
        Assert.assertEquals(3, wwwAuthenticates.length);
        Assert.assertEquals("NTLM", wwwAuthenticates[0]);
        Assert.assertEquals("Negotiate", wwwAuthenticates[1]);
        Assert.assertTrue(wwwAuthenticates[2].equals("Basic realm=\"TestRealm\""));
        Assert.assertEquals(2, response.getHeaderNamesSize());
        Assert.assertEquals("keep-alive", response.getHeader("Connection"));
        Assert.assertEquals(401, response.getStatus());
    }

    @Test(expected = ServletException.class)
    public void testGetSetProvider() throws IOException, ServletException {
        Assert.assertNotNull(this.entryPoint.getProvider());
        this.entryPoint.setProvider(null);
        SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        SimpleHttpResponse response = new SimpleHttpResponse();
        this.entryPoint.commence(request, response, null);
        Assert.fail("expected ServletException");
    }
}
