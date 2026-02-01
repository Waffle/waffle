/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import waffle.servlet.spi.SecurityFilterProviderCollection;

/**
 * Sends back a request for a Negotiate Authentication to the browser.
 */
public class NegotiateSecurityFilterEntryPoint implements AuthenticationEntryPoint {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiateSecurityFilterEntryPoint.class);

    /** The provider. */
    private SecurityFilterProviderCollection provider;

    /**
     * Instantiates a new negotiate security filter entry point.
     */
    public NegotiateSecurityFilterEntryPoint() {
        NegotiateSecurityFilterEntryPoint.LOGGER.debug("[waffle.spring.NegotiateEntryPoint] loaded");
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException ex) throws IOException, ServletException {

        NegotiateSecurityFilterEntryPoint.LOGGER.debug("[waffle.spring.NegotiateEntryPoint] commence");

        if (this.provider == null) {
            throw new ServletException("Missing NegotiateEntryPoint.Provider");
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("Connection", "keep-alive");
        this.provider.sendUnauthorized(response);
        response.flushBuffer();
    }

    /**
     * Gets the provider.
     *
     * @return the provider
     */
    public SecurityFilterProviderCollection getProvider() {
        return this.provider;
    }

    /**
     * Sets the provider.
     *
     * @param value
     *            the new provider
     */
    public void setProvider(final SecurityFilterProviderCollection value) {
        this.provider = value;
    }
}
