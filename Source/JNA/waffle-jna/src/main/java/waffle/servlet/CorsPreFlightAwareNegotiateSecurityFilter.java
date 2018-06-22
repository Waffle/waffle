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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.util.CorsPreFlightHelper;

public class CorsPreFlightAwareNegotiateSecurityFilter extends NegotiateSecurityFilter implements Filter {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiateSecurityFilter.class);

    /**
     * Instantiates a new negotiate security filter.
     */
    public CorsPreFlightAwareNegotiateSecurityFilter() {
        CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                .info("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] loaded");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                .info("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] Loaded");

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                .info("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] Filtering");

        final HttpServletRequest sreq = (HttpServletRequest) request;

        if (CorsPreFlightHelper.isPreFlight(sreq)) {
            CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                    .debug("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] Request is CorsPreFlight");
            chain.doFilter(request, response);
        } else {
            CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                    .debug("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] Request is Not orsPreFlight");

            super.doFilter(request, response, chain);

            CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                    .info("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] Authentication Completed");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                .info("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] unloaded");

    }

}
