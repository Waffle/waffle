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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpResponse extends Response {

    private static final Logger       logger  = LoggerFactory.getLogger(SimpleHttpResponse.class);

    private int                       status  = 500;
    private Map<String, List<String>> headers = new HashMap<String, List<String>>();

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public void addHeader(String headerName, String headerValue) {
        List<String> current = this.headers.get(headerName);
        if (current == null) {
            current = new ArrayList<String>();
        }
        current.add(headerValue);
        this.headers.put(headerName, current);
    }

    @Override
    public void setHeader(String headerName, String headerValue) {
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
    public void setStatus(int value) {
        this.status = value;
    }

    public String getStatusString() {
        if (this.status == 401) {
            return "Unauthorized";
        }
        return "Unknown";
    }

    @Override
    public void flushBuffer() {
        SimpleHttpResponse.logger.info("{} {}", Integer.valueOf(this.status), getStatusString());
        for (String header : this.headers.keySet()) {
            for (String headerValue : this.headers.get(header)) {
                SimpleHttpResponse.logger.info("{}: {}", header, headerValue);
            }
        }
    }

    @Override
    public String[] getHeaderValues(String headerName) {
        List<String> headerValues = this.headers.get(headerName);
        return headerValues == null ? null : headerValues.toArray(new String[0]);
    }

    @Override
    public String getHeader(String headerName) {
        List<String> headerValues = this.headers.get(headerName);
        return headerValues == null ? null : Joiner.on(", ").join(headerValues);
    }

    @Override
    public String[] getHeaderNames() {
        return this.headers.keySet().toArray(new String[0]);
    }

    @Override
    public void sendError(int rc, String message) {
        this.status = rc;
    }

    @Override
    public void sendError(int rc) {
        this.status = rc;
    }
}
