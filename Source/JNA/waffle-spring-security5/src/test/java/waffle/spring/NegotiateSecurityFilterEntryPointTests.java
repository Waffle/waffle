/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
