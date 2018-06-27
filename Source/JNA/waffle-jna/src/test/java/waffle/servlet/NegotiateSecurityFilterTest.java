package waffle.servlet;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.Field;
import java.util.*;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

import org.junit.jupiter.api.Test;

import waffle.util.CorsPreflightCheck;

/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
class NegotiateSecurityFilterTest {

    @Tested
    private NegotiateSecurityFilter negotiateSecurityFilter;

    private Enumeration<String> initParameterNames = Collections.enumeration(new java.util.ArrayList<String>() {
        {
            add("principalFormat");
            add("principalFormat");
            add("roleFormat");
            add("allowGuestLogin");
            add("impersonate");
            add("securityFilterProviders");
            add("excludePatterns");
            add("excludeCorsPreflight");
            add("excludeBearerAuthorization");
        }
    });

    @Test
    void testCorsAndBearerAuthorizationI_init(@Mocked FilterConfig filterConfig) throws Exception {
        getClass().getClassLoader().getResource("logback.xml");
        new Expectations() {
            {

                filterConfig.getInitParameterNames();
                result = initParameterNames;
                filterConfig.getInitParameter("principalFormat");
                result = "fqn";
                filterConfig.getInitParameter("roleFormat");
                result = "fqn";
                filterConfig.getInitParameter("allowGuestLogin");
                result = "false";
                filterConfig.getInitParameter("impersonate");
                result = "true";
                filterConfig.getInitParameter("securityFilterProviders");
                result = "waffle.servlet.spi.BasicSecurityFilterProvider\nwaffle.servlet.spi.NegotiateSecurityFilterProvider";
                filterConfig.getInitParameter("excludePatterns");
                result = ".*/peter/.*";
                filterConfig.getInitParameter("excludeCorsPreflight");
                result = "true";
                filterConfig.getInitParameter("excludeBearerAuthorization");
                result = "true";
            }
        };

        negotiateSecurityFilter.init(filterConfig);

        Field excludeCorsPreflight = negotiateSecurityFilter.getClass().getDeclaredField("excludeCorsPreflight");
        Field excludeBearerAuthorization = negotiateSecurityFilter.getClass()
                .getDeclaredField("excludeBearerAuthorization");
        excludeCorsPreflight.setAccessible(true);
        excludeBearerAuthorization.setAccessible(true);
        assertTrue(excludeCorsPreflight.getBoolean(negotiateSecurityFilter));
        assertTrue(excludeBearerAuthorization.getBoolean(negotiateSecurityFilter));
        assertTrue(negotiateSecurityFilter.isImpersonate());
        assertFalse(negotiateSecurityFilter.isAllowGuestLogin());

        new Verifications() {
            {
                filterConfig.getInitParameter(withInstanceOf(String.class));
                minTimes = 8;
            }
        };

    }

    @Test
    void testExcludeCorsAndOAUTHBearerAuthorization_doFilter(@Mocked HttpServletRequest request,
            @Mocked HttpServletResponse response, @Mocked FilterChain chain, @Mocked FilterConfig filterConfig)
            throws Exception {
        getClass().getClassLoader().getResource("logback.xml");
        new Expectations() {
            {
                filterConfig.getInitParameterNames();
                result = initParameterNames;
                filterConfig.getInitParameter("principalFormat");
                result = "fqn";
                filterConfig.getInitParameter("roleFormat");
                result = "fqn";
                filterConfig.getInitParameter("allowGuestLogin");
                result = "false";
                filterConfig.getInitParameter("impersonate");
                result = "false";
                filterConfig.getInitParameter("securityFilterProviders");
                result = "waffle.servlet.spi.BasicSecurityFilterProvider\nwaffle.servlet.spi.NegotiateSecurityFilterProvider";
                filterConfig.getInitParameter("excludePatterns");
                result = ".*/peter/.*";
                filterConfig.getInitParameter("excludeCorsPreflight");
                result = "true";
                filterConfig.getInitParameter("excludeBearerAuthorization");
                result = "true";
                CorsPreflightCheck.isPreflight(request);
                result = true;
                request.getHeader("Authorization");
                result = "Bearer aBase64hash";
            }
        };

        negotiateSecurityFilter.init(filterConfig);
        negotiateSecurityFilter.doFilter(request, response, chain);

        assertNotNull(negotiateSecurityFilter.getAuth());

        new Verifications() {
            {
                chain.doFilter(request, response);
                times = 1;
            }
        };

    }

}
