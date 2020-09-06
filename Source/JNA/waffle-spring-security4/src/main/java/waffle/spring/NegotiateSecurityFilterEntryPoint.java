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
