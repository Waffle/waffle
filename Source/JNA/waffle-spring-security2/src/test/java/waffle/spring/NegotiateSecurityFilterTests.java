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
package waffle.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;

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
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterTests {

    private NegotiateSecurityFilter filter;
    private ApplicationContext      ctx;

    @Before
    public void setUp() {
        String[] configFiles = new String[] { "springTestFilterBeans.xml" };
        this.ctx = new ClassPathXmlApplicationContext(configFiles);
        SecurityContextHolder.getContext().setAuthentication(null);
        this.filter = (NegotiateSecurityFilter) this.ctx.getBean("waffleNegotiateSecurityFilter");
    }

    @After
    public void shutDown() {
        ((AbstractApplicationContext) this.ctx).close();
    }

    @Test
    public void testFilter() {
        assertFalse(this.filter.isAllowGuestLogin());
        assertEquals(PrincipalFormat.FQN, this.filter.getPrincipalFormatEnum());
        assertEquals(PrincipalFormat.BOTH, this.filter.getRoleFormatEnum());
        assertNull(this.filter.getFilterConfig());
        assertNotNull(this.filter.getProvider());
    }

    @Test
    public void testProvider() throws ClassNotFoundException {
        SecurityFilterProviderCollection provider = this.filter.getProvider();
        assertEquals(2, provider.size());
        assertTrue(provider.getByClassName("waffle.servlet.spi.BasicSecurityFilterProvider") instanceof BasicSecurityFilterProvider);
        assertTrue(provider.getByClassName("waffle.servlet.spi.NegotiateSecurityFilterProvider") instanceof NegotiateSecurityFilterProvider);
    }

    @Test
    public void testNoChallengeGET() throws IOException, ServletException {
        SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        SimpleHttpResponse response = new SimpleHttpResponse();
        SimpleFilterChain chain = new SimpleFilterChain();
        this.filter.doFilter(request, response, chain);
        // unlike servlet filters, it's a passthrough
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testNegotiate() throws IOException, ServletException {
        String securityPackage = "Negotiate";
        SimpleFilterChain filterChain = new SimpleFilterChain();
        SimpleHttpRequest request = new SimpleHttpRequest();

        String clientToken = BaseEncoding.base64().encode(WindowsAccountImpl.getCurrentUsername().getBytes());
        request.addHeader("Authorization", securityPackage + " " + clientToken);

        SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        GrantedAuthority[] authorities = auth.getAuthorities();
        assertNotNull(authorities);
        assertEquals(3, authorities.length);
        assertEquals("ROLE_USER", authorities[0].getAuthority());
        assertEquals("ROLE_USERS", authorities[1].getAuthority());
        assertEquals("ROLE_EVERYONE", authorities[2].getAuthority());
        assertEquals(0, response.getHeaderNamesSize());
    }

    @Test
    public void testUnsupportedSecurityPackagePassthrough() throws IOException, ServletException {
        SimpleFilterChain filterChain = new SimpleFilterChain();
        SimpleHttpRequest request = new SimpleHttpRequest();
        request.addHeader("Authorization", "Unsupported challenge");
        SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);
        // the filter should ignore authorization for an unsupported security package, ie. not return a 401
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGuestIsDisabled() throws IOException, ServletException {
        String securityPackage = "Negotiate";
        SimpleFilterChain filterChain = new SimpleFilterChain();
        SimpleHttpRequest request = new SimpleHttpRequest();

        String clientToken = BaseEncoding.base64().encode("Guest".getBytes());
        request.addHeader("Authorization", securityPackage + " " + clientToken);

        SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test(expected = ServletException.class)
    public void testAfterPropertiesSet() throws ServletException {
        this.filter.setProvider(null);
        this.filter.afterPropertiesSet();
    }
}
