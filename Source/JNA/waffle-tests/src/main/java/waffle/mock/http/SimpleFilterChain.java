/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.mock.http;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Simple filter chain.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class SimpleFilterChain implements FilterChain {

    /** The request. */
    private ServletRequest  request;

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

    /*
     * (non-Javadoc)
     * @see javax.servlet.FilterChain#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void doFilter(final ServletRequest sreq, final ServletResponse srep) throws IOException, ServletException {

        this.request = sreq;
        this.response = srep;
    }
}
