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
package waffle.apache.catalina;

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

    private ServletRequest  request;
    private ServletResponse response;

    public ServletRequest getRequest() {
        return this.request;
    }

    public ServletResponse getResponse() {
        return this.response;
    }

    @Override
    public void doFilter(ServletRequest sreq, ServletResponse srep) throws IOException, ServletException {
        this.request = sreq;
        this.response = srep;
    }
}
