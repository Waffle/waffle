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
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpResponse.class);

    /** The status. */
    private int status = 500;

    /** The headers. */
    private final Map<String, List<String>> headers = new HashMap<>();

    @Override
    public void addHeader(final String headerName, final String headerValue) {
        List<String> current = this.headers.get(headerName);
        if (current == null) {
            current = new ArrayList<>();
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
        return headerValues == null ? null : String.join(", ", headerValues);
    }

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
            current = new ArrayList<>();
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
