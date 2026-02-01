/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

import org.junit.jupiter.api.Test;

import waffle.util.CorsPreFlightCheck;

/**
 * The Class CorsAwareNegotiateSecurityFilterTest.
 */
class CorsAwareNegotiateSecurityFilterTest {

    /** The cors aware negotiate security filter. */
    @Tested
    private CorsAwareNegotiateSecurityFilter corsAwareNegotiateSecurityFilter;

    /** The preflight request. */
    @Mocked
    private HttpServletRequest preflightRequest;

    /** The preflight response. */
    @Mocked
    private HttpServletResponse preflightResponse;

    /** The chain. */
    @Mocked
    private FilterChain chain;

    /** The filter config. */
    @Mocked
    private FilterConfig filterConfig;

    /**
     * Do filter test cors preflight request.
     *
     * @throws Exception
     *             the exception
     */
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
                CorsPreFlightCheck.isPreflight(CorsAwareNegotiateSecurityFilterTest.this.preflightRequest);
                this.times = 1;
                CorsAwareNegotiateSecurityFilterTest.this.chain.doFilter(
                        CorsAwareNegotiateSecurityFilterTest.this.preflightRequest,
                        CorsAwareNegotiateSecurityFilterTest.this.preflightResponse);
            }
        };

    }

}
