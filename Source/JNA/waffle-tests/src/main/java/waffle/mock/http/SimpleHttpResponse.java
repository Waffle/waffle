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
package waffle.mock.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpResponse extends HttpServletResponseWrapper {

    private static final Logger       LOGGER  = LoggerFactory.getLogger(SimpleHttpResponse.class);

    private int                       status  = 500;
    private Map<String, List<String>> headers = new HashMap<String, List<String>>();

    final ByteArrayOutputStream       bytes   = new ByteArrayOutputStream();

    private final ServletOutputStream out     = new ServletOutputStream() {
                                                  @Override
                                                  public void write(int b) throws IOException {
                                                      SimpleHttpResponse.this.bytes.write(b);
                                                  }
                                              };

    private final PrintWriter         writer  = new PrintWriter(this.bytes);

    public SimpleHttpResponse() {
        super(Mockito.mock(HttpServletResponse.class));
    }

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
        LOGGER.info("{}: {}", Integer.valueOf(this.status), getStatusString());
        for (String header : this.headers.keySet()) {
            for (String headerValue : this.headers.get(header)) {
                LOGGER.info("{}: {}", header, headerValue);
            }
        }
    }

    /**
     * Use this for testing the number of headers
     */
    public int getHeaderNamesSize() {
        return this.headers.size();
    }

    public String[] getHeaderValues(String headerName) {
        List<String> headerValues = this.headers.get(headerName);
        return headerValues == null ? null : headerValues.toArray(new String[0]);
    }

    public String getHeader(String headerName) {
        List<String> headerValues = this.headers.get(headerName);
        if (headerValues == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String headerValue : headerValues) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(headerValue);
        }
        return sb.toString();
    }

    @Override
    public void sendError(int rc, String message) {
        this.status = rc;
    }

    @Override
    public void sendError(int rc) {
        this.status = rc;
    }

    @Override
    public PrintWriter getWriter() {
        return this.writer;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return this.out;
    }

    public String getOutputText() {
        this.writer.flush();
        return this.bytes.toString();
    }
}