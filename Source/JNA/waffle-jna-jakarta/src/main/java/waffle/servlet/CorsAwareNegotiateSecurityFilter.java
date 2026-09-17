/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CorsAwareNegotiateSecurityFilter.class);

    /**
     * Instantiates a new negotiate security filter.
     */
    public CorsAwareNegotiateSecurityFilter() {
        CorsAwareNegotiateSecurityFilter.logger.info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] loaded");
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        CorsAwareNegotiateSecurityFilter.logger.info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Starting");
        super.init(filterConfig);
        CorsAwareNegotiateSecurityFilter.logger.info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Started");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        CorsAwareNegotiateSecurityFilter.logger.info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Filtering");

        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(httpServletRequest);

        if (CorsPreFlightCheck.isPreflight(httpServletRequest)) {
            CorsAwareNegotiateSecurityFilter.logger.info(
                    "[waffle.servlet.CorsAwareNegotiateSecurityFilter] Request is CORS preflight; continue filter chain");
            chain.doFilter(request, response);
        } else if (authorizationHeader.isBearerAuthorizationHeader()) {
            CorsAwareNegotiateSecurityFilter.logger
                    .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Request is Bearer, continue filter chain");
            chain.doFilter(request, response);
        } else {
            CorsAwareNegotiateSecurityFilter.logger
                    .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Request is Not CORS preflight");

            super.doFilter(request, response, chain);

            CorsAwareNegotiateSecurityFilter.logger
                    .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Authentication Completed");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        CorsAwareNegotiateSecurityFilter.logger.info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] unloaded");
    }

}
