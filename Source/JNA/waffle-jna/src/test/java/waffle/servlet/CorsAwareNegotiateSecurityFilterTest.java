/*
 * MIT License
 *
 * Copyright (c) 2010-2024 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
