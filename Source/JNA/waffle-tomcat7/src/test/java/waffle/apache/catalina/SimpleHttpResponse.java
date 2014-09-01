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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * Simple HTTP Response.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpResponse extends Response {

    private static final Logger             LOGGER  = LoggerFactory.getLogger(SimpleHttpResponse.class);

    private int                             status  = 500;
    private final Map<String, List<String>> headers = new HashMap<String, List<String>>();

    @Override
    public void addHeader(final String headerName, final String headerValue) {
        List<String> current = this.headers.get(headerName);
        if (current == null) {
            current = new ArrayList<String>();
        }
        current.add(headerValue);
        this.headers.put(headerName, current);
    }

    @Override
    public void flushBuffer() {
        SimpleHttpResponse.LOGGER.info("{} {}", Integer.valueOf(this.status), this.getStatusString());
        for (final String header : this.headers.keySet()) {
            for (final String headerValue : this.headers.get(header)) {
                SimpleHttpResponse.LOGGER.info("{}: {}", header, headerValue);
            }
        }
    }

    @Override
    public String getHeader(final String headerName) {
        final List<String> headerValues = this.headers.get(headerName);
        return headerValues == null ? null : Joiner.on(", ").join(headerValues);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return this.headers.keySet();
    }

    public String[] getHeaderValues(final String headerName) {
        final List<String> headerValues = this.headers.get(headerName);
        return headerValues == null ? null : headerValues.toArray(new String[0]);
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    public String getStatusString() {
        return this.status == 401 ? "Unauthorized" : "Unknown";
    }

    @Override
    public void sendError(final int rc) {
        this.status = rc;
    }

    @Override
    public void sendError(final int rc, final String message) {
        this.status = rc;
    }

    @Override
    public void setHeader(final String headerName, final String headerValue) {
        List<String> current = this.headers.get(headerName);
        if (current == null) {
            current = new ArrayList<String>();
        } else {
            current.clear();
        }
        current.add(headerValue);
        this.headers.put(headerName, current);
    }

    @Override
    public void setStatus(final int value) {
        this.status = value;
    }
}
