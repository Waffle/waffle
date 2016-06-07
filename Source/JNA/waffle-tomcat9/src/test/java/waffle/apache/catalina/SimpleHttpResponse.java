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
package waffle.apache.catalina;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple HTTP Response.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpResponse extends Response {

    /** The Constant LOGGER. */
    private static final Logger             LOGGER  = LoggerFactory.getLogger(SimpleHttpResponse.class);

    /** The status. */
    private int                             status  = 500;

    /** The headers. */
    private final Map<String, List<String>> headers = new HashMap<>();

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Response#addHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void addHeader(final String headerName, final String headerValue) {
        List<String> current = this.headers.get(headerName);
        if (current == null) {
            current = new ArrayList<>();
        }
        current.add(headerValue);
        this.headers.put(headerName, current);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Response#flushBuffer()
     */
    @Override
    public void flushBuffer() {
        SimpleHttpResponse.LOGGER.info("{} {}", Integer.valueOf(this.status), this.getStatusString());
        for (final String header : this.headers.keySet()) {
            for (final String headerValue : this.headers.get(header)) {
                SimpleHttpResponse.LOGGER.info("{}: {}", header, headerValue);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Response#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(final String headerName) {
        final List<String> headerValues = this.headers.get(headerName);
        return headerValues == null ? null : String.join(", ", headerValues);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Response#getHeaderNames()
     */
    @Override
    public Collection<String> getHeaderNames() {
        return this.headers.keySet();
    }

    /**
     * Gets the header values.
     *
     * @param headerName
     *            the header name
     * @return the header values
     */
    public String[] getHeaderValues(final String headerName) {
        final List<String> headerValues = this.headers.get(headerName);
        return headerValues == null ? null : headerValues.toArray(new String[0]);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Response#getStatus()
     */
    @Override
    public int getStatus() {
        return this.status;
    }

    /**
     * Gets the status string.
     *
     * @return the status string
     */
    public String getStatusString() {
        return this.status == 401 ? "Unauthorized" : "Unknown";
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Response#sendError(int)
     */
    @Override
    public void sendError(final int rc) {
        this.status = rc;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Response#sendError(int, java.lang.String)
     */
    @Override
    public void sendError(final int rc, final String message) {
        this.status = rc;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Response#setHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void setHeader(final String headerName, final String headerValue) {
        List<String> current = this.headers.get(headerName);
        if (current == null) {
            current = new ArrayList<>();
        } else {
            current.clear();
        }
        current.add(headerValue);
        this.headers.put(headerName, current);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Response#setStatus(int)
     */
    @Override
    public void setStatus(final int value) {
        this.status = value;
    }
}
