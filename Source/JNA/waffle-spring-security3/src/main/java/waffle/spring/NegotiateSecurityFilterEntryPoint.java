/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
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
 * 
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterEntryPoint implements AuthenticationEntryPoint {

    private static final Logger              LOGGER = LoggerFactory.getLogger(NegotiateSecurityFilterEntryPoint.class);
    private SecurityFilterProviderCollection provider;

    public NegotiateSecurityFilterEntryPoint() {
        LOGGER.debug("[waffle.spring.NegotiateEntryPoint] loaded");
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException ex) throws IOException, ServletException {

        LOGGER.debug("[waffle.spring.NegotiateEntryPoint] commence");

        if (this.provider == null) {
            throw new ServletException("Missing NegotiateEntryPoint.Provider");
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("Connection", "keep-alive");
        this.provider.sendUnauthorized(response);
        response.flushBuffer();
    }

    public SecurityFilterProviderCollection getProvider() {
        return this.provider;
    }

    public void setProvider(final SecurityFilterProviderCollection value) {
        this.provider = value;
    }
}
