/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

/**
 * Simple filter chain.
 */
public class SimpleFilterChain implements FilterChain {

    /** The request. */
    private ServletRequest request;

    /** The response. */
    private ServletResponse response;

    /**
     * Gets the request.
     *
     * @return the request
     */
    public ServletRequest getRequest() {
        return this.request;
    }

    /**
     * Gets the response.
     *
     * @return the response
     */
    public ServletResponse getResponse() {
        return this.response;
    }

    @Override
    public void doFilter(final ServletRequest sreq, final ServletResponse srep) throws IOException, ServletException {
        this.request = sreq;
        this.response = srep;
    }
}
