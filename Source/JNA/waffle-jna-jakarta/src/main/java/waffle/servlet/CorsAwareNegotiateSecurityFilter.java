/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.util.AuthorizationHeader;
import waffle.util.CorsPreFlightCheck;

/**
 * The Class CorsAwareNegotiateSecurityFilter.
 */
public class CorsAwareNegotiateSecurityFilter extends NegotiateSecurityFilter implements Filter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CorsAwareNegotiateSecurityFilter.class);

    /**
     * Instantiates a new negotiate security filter.
     */
    public CorsAwareNegotiateSecurityFilter() {
        CorsAwareNegotiateSecurityFilter.LOGGER.info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] loaded");
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        CorsAwareNegotiateSecurityFilter.LOGGER.info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Starting");
        super.init(filterConfig);
        CorsAwareNegotiateSecurityFilter.LOGGER.info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Started");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        CorsAwareNegotiateSecurityFilter.LOGGER.info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Filtering");

        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(httpServletRequest);

        if (CorsPreFlightCheck.isPreflight(httpServletRequest)) {
            CorsAwareNegotiateSecurityFilter.LOGGER.info(
                    "[waffle.servlet.CorsAwareNegotiateSecurityFilter] Request is CORS preflight; continue filter chain");
            chain.doFilter(request, response);
        } else if (authorizationHeader.isBearerAuthorizationHeader()) {
            CorsAwareNegotiateSecurityFilter.LOGGER
                    .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Request is Bearer, continue filter chain");
            chain.doFilter(request, response);
        } else {
            CorsAwareNegotiateSecurityFilter.LOGGER
                    .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Request is Not CORS preflight");

            super.doFilter(request, response, chain);

            CorsAwareNegotiateSecurityFilter.LOGGER
                    .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Authentication Completed");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        CorsAwareNegotiateSecurityFilter.LOGGER.info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] unloaded");
    }

}
