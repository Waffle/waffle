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

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

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
                CorsAwareNegotiateSecurityFilterTest.this.preflightRequest.getMethod();
                this.result = "OPTIONS";
                CorsAwareNegotiateSecurityFilterTest.this.preflightRequest.getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
                CorsAwareNegotiateSecurityFilterTest.this.preflightRequest.getHeader("Access-Control-Request-Headers");
                this.result = "X-Request-For";
                CorsAwareNegotiateSecurityFilterTest.this.preflightRequest.getHeader("Origin");
                this.result = "https://theorigin.preflight";
            }
        };

        this.corsAwareNegotiateSecurityFilter.doFilter(this.preflightRequest, this.preflightResponse, this.chain);

        new Verifications() {
            {
                CorsPreflightCheck.isPreflight(CorsAwareNegotiateSecurityFilterTest.this.preflightRequest);
                this.times = 1;
                CorsAwareNegotiateSecurityFilterTest.this.chain.doFilter(
                        CorsAwareNegotiateSecurityFilterTest.this.preflightRequest,
                        CorsAwareNegotiateSecurityFilterTest.this.preflightResponse);
            }
        };

    }

}
