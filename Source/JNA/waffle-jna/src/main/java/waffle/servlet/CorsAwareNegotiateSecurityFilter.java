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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.util.AuthorizationHeader;
import waffle.util.CorsPreflightCheck;

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

        if (CorsPreflightCheck.isPreflight(httpServletRequest)) {
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
