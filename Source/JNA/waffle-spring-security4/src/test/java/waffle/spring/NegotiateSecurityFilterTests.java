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
package waffle.spring;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.io.BaseEncoding;

import waffle.mock.http.SimpleFilterChain;
import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;
import waffle.servlet.spi.BasicSecurityFilterProvider;
import waffle.servlet.spi.NegotiateSecurityFilterProvider;
import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * The Class NegotiateSecurityFilterTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterTests {

    /** The filter. */
    private NegotiateSecurityFilter filter;

    /** The ctx. */
    private ApplicationContext      ctx;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        final String[] configFiles = new String[] { "springTestFilterBeans.xml" };
        this.ctx = new ClassPathXmlApplicationContext(configFiles);
        SecurityContextHolder.getContext().setAuthentication(null);
        this.filter = (NegotiateSecurityFilter) this.ctx.getBean("waffleNegotiateSecurityFilter");
    }

    /**
     * Shut down.
     */
    @After
    public void shutDown() {
        ((AbstractApplicationContext) this.ctx).close();
    }

    /**
     * Test filter.
     */
    @Test
    public void testFilter() {
        Assert.assertFalse(this.filter.isAllowGuestLogin());
        Assert.assertEquals(PrincipalFormat.FQN, this.filter.getPrincipalFormat());
        Assert.assertEquals(PrincipalFormat.BOTH, this.filter.getRoleFormat());
        Assert.assertNull(this.filter.getFilterConfig());
        Assert.assertNotNull(this.filter.getProvider());
    }

    /**
     * Test provider.
     *
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    @Test
    public void testProvider() throws ClassNotFoundException {
        final SecurityFilterProviderCollection provider = this.filter.getProvider();
        Assert.assertEquals(2, provider.size());
        Assert.assertTrue(provider.getByClassName("waffle.servlet.spi.BasicSecurityFilterProvider") instanceof BasicSecurityFilterProvider);
        Assert.assertTrue(provider.getByClassName("waffle.servlet.spi.NegotiateSecurityFilterProvider") instanceof NegotiateSecurityFilterProvider);
    }

    /**
     * Test no challenge get.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    public void testNoChallengeGET() throws IOException, ServletException {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        final SimpleFilterChain chain = new SimpleFilterChain();
        this.filter.doFilter(request, response, chain);
        // unlike servlet filters, it's a passthrough
        Assert.assertEquals(500, response.getStatus());
    }

    /**
     * Test negotiate.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    public void testNegotiate() throws IOException, ServletException {
        final String securityPackage = "Negotiate";
        final SimpleFilterChain filterChain = new SimpleFilterChain();
        final SimpleHttpRequest request = new SimpleHttpRequest();

        final String clientToken = BaseEncoding.base64().encode(
                WindowsAccountImpl.getCurrentUsername().getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", securityPackage + " " + clientToken);

        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull(auth);
        final Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Assert.assertNotNull(authorities);
        Assert.assertEquals(3, authorities.size());
        final Iterator<? extends GrantedAuthority> authoritiesIterator = authorities.iterator();

        final List<String> list = new ArrayList<>();
        while (authoritiesIterator.hasNext()) {
            list.add(authoritiesIterator.next().getAuthority());
        }
        Collections.sort(list);
        Assert.assertEquals("ROLE_EVERYONE", list.get(0));
        Assert.assertEquals("ROLE_USER", list.get(1));
        Assert.assertEquals("ROLE_USERS", list.get(2));
        Assert.assertEquals(0, response.getHeaderNamesSize());
    }

    /**
     * Test unsupported security package passthrough.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    public void testUnsupportedSecurityPackagePassthrough() throws IOException, ServletException {
        final SimpleFilterChain filterChain = new SimpleFilterChain();
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.addHeader("Authorization", "Unsupported challenge");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);
        // the filter should ignore authorization for an unsupported security package, ie. not return a 401
        Assert.assertEquals(500, response.getStatus());
    }

    /**
     * Test guest is disabled.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    public void testGuestIsDisabled() throws IOException, ServletException {
        final String securityPackage = "Negotiate";
        final SimpleFilterChain filterChain = new SimpleFilterChain();
        final SimpleHttpRequest request = new SimpleHttpRequest();

        final String clientToken = BaseEncoding.base64().encode("Guest".getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", securityPackage + " " + clientToken);

        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);

        Assert.assertEquals(401, response.getStatus());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Test after properties set.
     *
     * @throws ServletException
     *             the servlet exception
     */
    @Test(expected = ServletException.class)
    public void testAfterPropertiesSet() throws ServletException {
        this.filter.setProvider(null);
        this.filter.afterPropertiesSet();
    }
}
