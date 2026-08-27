/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

/**
 * Negotiate Security Filter Test.
 */
class NegotiateSecurityFilterTest {

    /** The negotiate security filter. */
    @Tested
    private NegotiateSecurityFilter negotiateSecurityFilter;

    /**
     * Reset the static windows flag after each test to avoid state leak.
     *
     * @throws Exception
     *             if reflection fails
     */
    @AfterEach
    void resetWindowsFlag() throws Exception {
        final Field windowsField = NegotiateSecurityFilter.class.getDeclaredField("windows");
        windowsField.setAccessible(true);
        windowsField.set(null, null);
    }

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

    /**
     * Test do filter on Windows with disableSSO calls chain.
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
    void testDoFilterOnWindowsDisableSSOCallsChain(@Mocked final HttpServletRequest request,
            @Mocked final HttpServletResponse response, @Mocked final FilterChain chain,
            @Mocked final IWindowsAuthProvider mockAuth) throws Exception {
        // Simulate Windows environment
        final Field windowsField = NegotiateSecurityFilter.class.getDeclaredField("windows");
        windowsField.setAccessible(true);
        windowsField.set(null, Boolean.TRUE);

        this.negotiateSecurityFilter.setAuth(mockAuth);
        this.negotiateSecurityFilter.init(null);

        // Set disableSSO=true via reflection
        final Field disableSSOField = NegotiateSecurityFilter.class.getDeclaredField("disableSSO");
        disableSSOField.setAccessible(true);
        disableSSOField.set(this.negotiateSecurityFilter, Boolean.TRUE);

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
     * Test do filter on Windows with no auth header sends 401.
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
    void testDoFilterOnWindowsNoAuthHeaderSendsUnauthorized(@Mocked final HttpServletRequest request,
            @Mocked final HttpServletResponse response, @Mocked final FilterChain chain,
            @Mocked final IWindowsAuthProvider mockAuth) throws Exception {
        // Simulate Windows environment
        final Field windowsField = NegotiateSecurityFilter.class.getDeclaredField("windows");
        windowsField.setAccessible(true);
        windowsField.set(null, Boolean.TRUE);

        this.negotiateSecurityFilter.setAuth(mockAuth);
        this.negotiateSecurityFilter.init(null);

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
                request.getRequestURL();
                this.result = null;
                this.minTimes = 0;
                request.getUserPrincipal();
                this.result = null;
                this.minTimes = 0;
                request.getSession(false);
                this.result = null;
                this.minTimes = 0;
                request.getHeader("Authorization");
                this.result = null;
                this.minTimes = 0;
            }
        };

        this.negotiateSecurityFilter.doFilter(request, response, chain);

        new Verifications() {
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                this.times = 1;
                chain.doFilter(this.withInstanceOf(HttpServletRequest.class),
                        this.withInstanceOf(HttpServletResponse.class));
                this.times = 0;
            }
        };
    }

    /**
     * Test do filter on Windows with non-Windows principal in session calls chain.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @param chain
     *            the chain
     * @param mockAuth
     *            the mock auth
     * @param mockProviders
     *            the mock providers
     * @param session
     *            the session
     * @param principal
     *            a non-Windows principal
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testDoFilterOnWindowsNonWindowsPrincipalInSessionCallsChain(@Mocked final HttpServletRequest request,
            @Mocked final HttpServletResponse response, @Mocked final FilterChain chain,
            @Mocked final IWindowsAuthProvider mockAuth, @Mocked final SecurityFilterProviderCollection mockProviders,
            @Mocked final HttpSession session, @Mocked final Principal principal) throws Exception {
        // Simulate Windows environment
        final Field windowsField = NegotiateSecurityFilter.class.getDeclaredField("windows");
        windowsField.setAccessible(true);
        windowsField.set(null, Boolean.TRUE);

        this.negotiateSecurityFilter.setAuth(mockAuth);
        this.negotiateSecurityFilter.init(null);

        final String sessionKey = NegotiateSecurityFilter.class.getName() + ".PRINCIPAL";

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
                request.getRequestURL();
                this.result = null;
                this.minTimes = 0;
                request.getUserPrincipal();
                this.result = null;
                this.minTimes = 0;
                request.getSession(false);
                this.result = session;
                this.minTimes = 0;
                session.getAttribute(sessionKey);
                this.result = principal;
                this.minTimes = 0;
                mockProviders.isPrincipalException(request);
                this.result = false;
                this.minTimes = 0;
                principal.getName();
                this.result = "testuser";
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
     * Test do filter on Windows with WindowsPrincipal in session calls chain with wrapped request.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @param chain
     *            the chain
     * @param mockAuth
     *            the mock auth
     * @param mockProviders
     *            the mock providers
     * @param session
     *            the session
     * @param mockIdentity
     *            the mock identity
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testDoFilterOnWindowsWindowsPrincipalInSessionCallsChain(@Mocked final HttpServletRequest request,
            @Mocked final HttpServletResponse response, @Mocked final FilterChain chain,
            @Mocked final IWindowsAuthProvider mockAuth, @Mocked final SecurityFilterProviderCollection mockProviders,
            @Mocked final HttpSession session, @Mocked final IWindowsIdentity mockIdentity) throws Exception {
        // Simulate Windows environment
        final Field windowsField = NegotiateSecurityFilter.class.getDeclaredField("windows");
        windowsField.setAccessible(true);
        windowsField.set(null, Boolean.TRUE);

        this.negotiateSecurityFilter.setAuth(mockAuth);
        this.negotiateSecurityFilter.init(null);

        final String sessionKey = NegotiateSecurityFilter.class.getName() + ".PRINCIPAL";

        new Expectations() {
            {
                mockIdentity.getFqn();
                this.result = "DOMAIN\\testuser";
                mockIdentity.getSid();
                this.result = new byte[0];
                this.minTimes = 0;
                mockIdentity.getSidString();
                this.result = "S-1-5-21-test";
                this.minTimes = 0;
                mockIdentity.getGroups();
                this.result = new IWindowsAccount[0];
                this.minTimes = 0;
            }
        };

        final WindowsPrincipal windowsPrincipal = new WindowsPrincipal(mockIdentity, PrincipalFormat.FQN,
                PrincipalFormat.FQN);

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
                request.getRequestURL();
                this.result = null;
                this.minTimes = 0;
                request.getUserPrincipal();
                this.result = null;
                this.minTimes = 0;
                request.getSession(false);
                this.result = session;
                this.minTimes = 0;
                session.getAttribute(sessionKey);
                this.result = windowsPrincipal;
                this.minTimes = 0;
                mockProviders.isPrincipalException(request);
                this.result = false;
                this.minTimes = 0;
            }
        };

        this.negotiateSecurityFilter.doFilter(request, response, chain);

        new Verifications() {
            {
                chain.doFilter(this.withInstanceOf(NegotiateRequestWrapper.class), response);
                this.times = 1;
            }
        };
    }

    /**
     * Test do filter on Windows with auth header sends providers request.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @param chain
     *            the chain
     * @param mockAuth
     *            the mock auth
     * @param mockProviders
     *            the mock providers
     * @param session
     *            the http session
     * @param mockIdentity
     *            the windows identity
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testDoFilterOnWindowsWithAuthHeaderAuthenticates(@Mocked final HttpServletRequest request,
            @Mocked final HttpServletResponse response, @Mocked final FilterChain chain,
            @Mocked final IWindowsAuthProvider mockAuth, @Mocked final SecurityFilterProviderCollection mockProviders,
            @Mocked final HttpSession session, @Mocked final IWindowsIdentity mockIdentity) throws Exception {
        // Simulate Windows environment
        final Field windowsField = NegotiateSecurityFilter.class.getDeclaredField("windows");
        windowsField.setAccessible(true);
        windowsField.set(null, Boolean.TRUE);

        this.negotiateSecurityFilter.setAuth(mockAuth);
        this.negotiateSecurityFilter.init(null);

        final String sessionKey = NegotiateSecurityFilter.class.getName() + ".PRINCIPAL";

        new Expectations() {
            {
                mockIdentity.getFqn();
                this.result = "DOMAIN\\testuser";
                mockIdentity.getSid();
                this.result = new byte[] { 1, 2, 3 };
                this.minTimes = 0;
                mockIdentity.getSidString();
                this.result = "S-1-5-21-test";
                this.minTimes = 0;
                mockIdentity.getGroups();
                this.result = new IWindowsAccount[0];
                mockIdentity.isGuest();
                this.result = false;
                this.minTimes = 0;
                mockIdentity.dispose();
                this.minTimes = 0;

                request.getMethod();
                this.result = "GET";
                this.minTimes = 0;
                request.getRequestURI();
                this.result = "/test";
                this.minTimes = 0;
                request.getContentLength();
                this.result = 0;
                this.minTimes = 0;
                request.getRequestURL();
                this.result = null;
                this.minTimes = 0;
                request.getUserPrincipal();
                this.result = null;
                this.minTimes = 0;
                request.getSession(false);
                this.result = session;
                session.getAttribute(sessionKey);
                this.result = null;
                this.minTimes = 0;
                mockProviders.isPrincipalException(request);
                this.result = false;
                this.minTimes = 0;
                request.getHeader("Authorization");
                this.result = "Negotiate dGVzdA==";
                this.minTimes = 0;
                mockProviders.doFilter(request, response);
                this.result = mockIdentity;
                this.minTimes = 0;
                request.getSession(true);
                this.result = session;
                this.minTimes = 0;
                session.getAttribute("javax.security.auth.subject");
                this.result = null;
                this.minTimes = 0;
                session.setAttribute(this.withInstanceOf(String.class), this.withInstanceOf(Object.class));
                this.minTimes = 0;
            }
        };

        this.negotiateSecurityFilter.doFilter(request, response, chain);

        new Verifications() {
            {
                chain.doFilter(this.withInstanceOf(NegotiateRequestWrapper.class), response);
                this.times = 1;
            }
        };
    }

}
