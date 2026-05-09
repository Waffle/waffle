/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.PrincipalFormat;

/**
 * Negotiate Security Filter Test.
 */
class NegotiateSecurityFilterTest {

    /** The negotiate security filter. */
    @Tested
    private NegotiateSecurityFilter negotiateSecurityFilter;

    /** The init parameter names. */
    private final Enumeration<String> initParameterNames = Collections.enumeration(new java.util.ArrayList<String>() {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        {
            this.add("principalFormat");
            this.add("principalFormat");
            this.add("roleFormat");
            this.add("allowGuestLogin");
            this.add("impersonate");
            this.add("securityFilterProviders");
            this.add("excludePatterns");
            this.add("excludeCorsPreflight");
            this.add("excludeBearerAuthorization");
        }
    });

    /**
     * Test cors and bearer authorization I init.
     *
     * @param filterConfig
     *            the filter config
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testCorsAndBearerAuthorizationI_init(@Mocked final FilterConfig filterConfig) throws Exception {
        this.getClass().getClassLoader().getResource("logback.xml");

        new Expectations() {
            {
                filterConfig.getInitParameterNames();
                this.result = NegotiateSecurityFilterTest.this.initParameterNames;
                filterConfig.getInitParameter("principalFormat");
                this.result = "fqn";
                filterConfig.getInitParameter("roleFormat");
                this.result = "fqn";
                filterConfig.getInitParameter("allowGuestLogin");
                this.result = "false";
                filterConfig.getInitParameter("impersonate");
                this.result = "true";
                filterConfig.getInitParameter("securityFilterProviders");
                this.result = "waffle.servlet.spi.BasicSecurityFilterProvider\nwaffle.servlet.spi.NegotiateSecurityFilterProvider";
                filterConfig.getInitParameter("excludePatterns");
                this.result = ".*/peter/.*";
                filterConfig.getInitParameter("excludeCorsPreflight");
                this.result = "true";
                filterConfig.getInitParameter("excludeBearerAuthorization");
                this.result = "true";
            }
        };

        this.negotiateSecurityFilter.init(filterConfig);

        final Field excludeCorsPreflight = this.negotiateSecurityFilter.getClass()
                .getDeclaredField("excludeCorsPreflight");
        final Field excludeBearerAuthorization = this.negotiateSecurityFilter.getClass()
                .getDeclaredField("excludeBearerAuthorization");
        excludeCorsPreflight.setAccessible(true);
        excludeBearerAuthorization.setAccessible(true);
        Assertions.assertTrue(excludeCorsPreflight.getBoolean(this.negotiateSecurityFilter));
        Assertions.assertTrue(excludeBearerAuthorization.getBoolean(this.negotiateSecurityFilter));
        Assertions.assertTrue(this.negotiateSecurityFilter.isImpersonate());
        Assertions.assertFalse(this.negotiateSecurityFilter.isAllowGuestLogin());

        new Verifications() {
            {
                filterConfig.getInitParameter(this.withInstanceOf(String.class));
                this.minTimes = 8;
            }
        };

    }

    /**
     * Test exclude cors and OAUTH bearer authorization do filter.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @param chain
     *            the chain
     * @param filterConfig
     *            the filter config
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testExcludeCorsAndOAUTHBearerAuthorization_doFilter(@Mocked final HttpServletRequest request,
            @Mocked final HttpServletResponse response, @Mocked final FilterChain chain,
            @Mocked final FilterConfig filterConfig) throws Exception {
        this.getClass().getClassLoader().getResource("logback.xml");

        new Expectations() {
            {
                filterConfig.getInitParameterNames();
                this.result = NegotiateSecurityFilterTest.this.initParameterNames;
                filterConfig.getInitParameter("principalFormat");
                this.result = "fqn";
                filterConfig.getInitParameter("roleFormat");
                this.result = "fqn";
                filterConfig.getInitParameter("allowGuestLogin");
                this.result = "false";
                filterConfig.getInitParameter("impersonate");
                this.result = "false";
                filterConfig.getInitParameter("securityFilterProviders");
                this.result = "waffle.servlet.spi.BasicSecurityFilterProvider\nwaffle.servlet.spi.NegotiateSecurityFilterProvider";
                filterConfig.getInitParameter("excludePatterns");
                this.result = ".*/peter/.*";
                filterConfig.getInitParameter("excludeCorsPreflight");
                this.result = "true";
                filterConfig.getInitParameter("excludeBearerAuthorization");
                this.result = "true";
                request.getAttribute("cors.request.type");
                this.result = "PRE_FLIGHT";
                this.minTimes = 0;
                request.getMethod();
                this.result = "OPTIONS";
                this.minTimes = 0;
                request.getHeader("Authorization");
                this.result = "Bearer aBase64hash";
                this.minTimes = 0;
            }
        };

        this.negotiateSecurityFilter.init(filterConfig);
        this.negotiateSecurityFilter.doFilter(request, response, chain);

        new Verifications() {
            {
                chain.doFilter(request, response);
                this.times = 1;
            }
        };

    }

    /**
     * Test destroy does not throw.
     */
    @Test
    void testDestroy() {
        Assertions.assertDoesNotThrow(() -> this.negotiateSecurityFilter.destroy());
    }

    /**
     * Test set and get principal format.
     */
    @Test
    void testSetGetPrincipalFormat() {
        this.negotiateSecurityFilter.setPrincipalFormat("sid");
        Assertions.assertEquals(PrincipalFormat.SID, this.negotiateSecurityFilter.getPrincipalFormat());

        this.negotiateSecurityFilter.setPrincipalFormat("both");
        Assertions.assertEquals(PrincipalFormat.BOTH, this.negotiateSecurityFilter.getPrincipalFormat());

        this.negotiateSecurityFilter.setPrincipalFormat("fqn");
        Assertions.assertEquals(PrincipalFormat.FQN, this.negotiateSecurityFilter.getPrincipalFormat());

        this.negotiateSecurityFilter.setPrincipalFormat("none");
        Assertions.assertEquals(PrincipalFormat.NONE, this.negotiateSecurityFilter.getPrincipalFormat());
    }

    /**
     * Test set and get role format.
     */
    @Test
    void testSetGetRoleFormat() {
        this.negotiateSecurityFilter.setRoleFormat("sid");
        Assertions.assertEquals(PrincipalFormat.SID, this.negotiateSecurityFilter.getRoleFormat());

        this.negotiateSecurityFilter.setRoleFormat("both");
        Assertions.assertEquals(PrincipalFormat.BOTH, this.negotiateSecurityFilter.getRoleFormat());

        this.negotiateSecurityFilter.setRoleFormat("fqn");
        Assertions.assertEquals(PrincipalFormat.FQN, this.negotiateSecurityFilter.getRoleFormat());

        this.negotiateSecurityFilter.setRoleFormat("none");
        Assertions.assertEquals(PrincipalFormat.NONE, this.negotiateSecurityFilter.getRoleFormat());
    }

    /**
     * Test set and get auth.
     *
     * @param mockAuth
     *            the mock auth
     */
    @Test
    void testSetGetAuth(@Mocked final IWindowsAuthProvider mockAuth) {
        this.negotiateSecurityFilter.setAuth(mockAuth);
        Assertions.assertEquals(mockAuth, this.negotiateSecurityFilter.getAuth());
    }

    /**
     * Test set and is impersonate.
     */
    @Test
    void testSetIsImpersonate() {
        this.negotiateSecurityFilter.setImpersonate(true);
        Assertions.assertTrue(this.negotiateSecurityFilter.isImpersonate());

        this.negotiateSecurityFilter.setImpersonate(false);
        Assertions.assertFalse(this.negotiateSecurityFilter.isImpersonate());
    }

    /**
     * Test is allow guest login default.
     */
    @Test
    void testIsAllowGuestLoginDefault() {
        Assertions.assertTrue(this.negotiateSecurityFilter.isAllowGuestLogin());
    }

    /**
     * Test get providers after init with null filter config.
     *
     * @param mockAuth
     *            the mock auth
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testGetProvidersAfterInitWithNullConfig(@Mocked final IWindowsAuthProvider mockAuth) throws Exception {
        this.negotiateSecurityFilter.setAuth(mockAuth);
        this.negotiateSecurityFilter.init(null);
        final SecurityFilterProviderCollection providers = this.negotiateSecurityFilter.getProviders();
        Assertions.assertNotNull(providers);
        Assertions.assertEquals(2, providers.size());
    }

    /**
     * Test do filter on non-Windows calls chain.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @param chain
     *            the chain
     * @param mockAuth
     *            the mock auth
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testDoFilterOnNonWindowsCallsChain(@Mocked final HttpServletRequest request,
            @Mocked final HttpServletResponse response, @Mocked final FilterChain chain,
            @Mocked final IWindowsAuthProvider mockAuth) throws Exception {
        this.negotiateSecurityFilter.setAuth(mockAuth);
        this.negotiateSecurityFilter.init(null);

        // Mock minimum needed for non-Windows path
        new Expectations() {
            {
                request.getMethod();
                this.result = "GET";
                this.minTimes = 0;
                request.getRequestURI();
                this.result = "/test";
                this.minTimes = 0;
                request.getContentLength();
                this.result = 0;
                this.minTimes = 0;
            }
        };

        this.negotiateSecurityFilter.doFilter(request, response, chain);

        new Verifications() {
            {
                chain.doFilter(request, response);
                this.times = 1;
            }
        };
    }

    /**
     * Test init with disableSSO parameter.
     *
     * @param filterConfig
     *            the filter config
     * @param mockAuth
     *            the mock auth
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testInitWithDisableSSO(@Mocked final FilterConfig filterConfig, @Mocked final IWindowsAuthProvider mockAuth)
            throws Exception {
        final Enumeration<String> paramNames = Collections
                .enumeration(java.util.Arrays.asList("disableSSO", "securityFilterProviders"));

        new Expectations() {
            {
                filterConfig.getInitParameterNames();
                this.result = paramNames;
                filterConfig.getInitParameter("disableSSO");
                this.result = "true";
                filterConfig.getInitParameter("securityFilterProviders");
                this.result = "waffle.servlet.spi.BasicSecurityFilterProvider\nwaffle.servlet.spi.NegotiateSecurityFilterProvider";
            }
        };

        this.negotiateSecurityFilter.setAuth(mockAuth);
        this.negotiateSecurityFilter.init(filterConfig);

        final Field disableSSO = this.negotiateSecurityFilter.getClass().getDeclaredField("disableSSO");
        disableSSO.setAccessible(true);
        Assertions.assertTrue(disableSSO.getBoolean(this.negotiateSecurityFilter));
    }

}
