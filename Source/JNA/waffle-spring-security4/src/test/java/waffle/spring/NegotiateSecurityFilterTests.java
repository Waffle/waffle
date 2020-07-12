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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

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
    private ApplicationContext ctx;

    /**
     * Sets the up.
     */
    @BeforeEach
    public void setUp() {
        final String[] configFiles = new String[] { "springTestFilterBeans.xml" };
        this.ctx = new ClassPathXmlApplicationContext(configFiles);
        SecurityContextHolder.getContext().setAuthentication(null);
        this.filter = (NegotiateSecurityFilter) this.ctx.getBean("waffleNegotiateSecurityFilter");
    }

    /**
     * Shut down.
     */
    @AfterEach
    public void shutDown() {
        ((AbstractApplicationContext) this.ctx).close();
    }

    /**
     * Test filter.
     */
    @Test
    public void testFilter() {
        Assertions.assertFalse(this.filter.isAllowGuestLogin());
        Assertions.assertEquals(PrincipalFormat.FQN, this.filter.getPrincipalFormat());
        Assertions.assertEquals(PrincipalFormat.BOTH, this.filter.getRoleFormat());
        Assertions.assertNull(this.filter.getFilterConfig());
        Assertions.assertNotNull(this.filter.getProvider());
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
        Assertions.assertEquals(2, provider.size());
        Assertions.assertTrue(provider.getByClassName(
                "waffle.servlet.spi.BasicSecurityFilterProvider") instanceof BasicSecurityFilterProvider);
        Assertions.assertTrue(provider.getByClassName(
                "waffle.servlet.spi.NegotiateSecurityFilterProvider") instanceof NegotiateSecurityFilterProvider);
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
        Assertions.assertEquals(500, response.getStatus());
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

        final String clientToken = Base64.getEncoder()
                .encodeToString(WindowsAccountImpl.getCurrentUsername().getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", securityPackage + " " + clientToken);

        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertNotNull(auth);
        final Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Assertions.assertNotNull(authorities);
        Assertions.assertEquals(3, authorities.size());

        final List<String> list = new ArrayList<>();
        for (final GrantedAuthority grantedAuthority : authorities) {
            list.add(grantedAuthority.getAuthority());
        }
        Collections.sort(list);
        Assertions.assertEquals("ROLE_EVERYONE", list.get(0));
        Assertions.assertEquals("ROLE_USER", list.get(1));
        Assertions.assertEquals("ROLE_USERS", list.get(2));
        Assertions.assertEquals(0, response.getHeaderNamesSize());
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
        Assertions.assertEquals(500, response.getStatus());
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

        final String clientToken = Base64.getEncoder().encodeToString("Guest".getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", securityPackage + " " + clientToken);

        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);

        Assertions.assertEquals(401, response.getStatus());
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Test after properties set.
     *
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    public void testAfterPropertiesSet() throws ServletException {
        this.filter.setProvider(null);
        Assertions.assertThrows(ServletException.class, () -> {
            this.filter.afterPropertiesSet();
        });
    }

}
