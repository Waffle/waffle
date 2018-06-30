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
package waffle.servlet;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mockit.*;

import org.junit.jupiter.api.Test;

import waffle.util.CorsPreflightCheck;

class CorsAwareNegotiateSecurityFilterTest {

    @Tested
    CorsAwareNegotiateSecurityFilter corsAwareNegotiateSecurityFilter;

    @Mocked
    HttpServletRequest preflightRequest;
    @Mocked
    HttpServletResponse preflightResponse;
    @Mocked
    FilterChain chain;
    @Mocked
    FilterConfig filterConfig;

    @Test
    void doFilterTestCorsPreflightRequest() throws Exception {

        new Expectations() {
            {
                preflightRequest.getMethod();
                result = "OPTIONS";
                preflightRequest.getHeader("Access-Control-Request-Method");
                result = "LOGIN";
                preflightRequest.getHeader("Access-Control-Request-Headers");
                result = "X-Request-For";
                preflightRequest.getHeader("Origin");
                result = "https://theorigin.preflight";
            }
        };

        corsAwareNegotiateSecurityFilter.doFilter(preflightRequest, preflightResponse, chain);

        new Verifications() {
            {
                CorsPreflightCheck.isPreflight(preflightRequest);
                times = 1;
                chain.doFilter(preflightRequest, preflightResponse);
            }
        };

    }

}
