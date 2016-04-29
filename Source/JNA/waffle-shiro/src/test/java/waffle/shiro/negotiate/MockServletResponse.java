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
package waffle.shiro.negotiate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * The Class MockServletResponse.
 */
public abstract class MockServletResponse implements HttpServletResponse {

    /** The is flushed. */
    boolean                   isFlushed;

    /** The error code. */
    int                       errorCode;

    /** The headers. */
    Map<String, String>       headers;

    /** The headers added. */
    Map<String, List<String>> headersAdded;

    /** The sc. */
    int                       sc;

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void addHeader(final String name, final String value) {
        if (this.headersAdded.containsKey(name)) {
            this.headersAdded.get(name).add(value);
            return;
        }

        final List<String> values = new ArrayList<>();
        values.add(value);
        this.headersAdded.put(name, values);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#flushBuffer()
     */
    @Override
    public void flushBuffer() throws IOException {
        this.isFlushed = true;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     */
    @Override
    public void sendError(final int sendError) throws IOException {
        this.errorCode = sendError;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void setHeader(final String name, final String value) {
        this.headers.put(name, value);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    @Override
    public void setStatus(final int status) {
        this.sc = status;
    }

}
