/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.shiro.negotiate;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Class MockServletResponse.
 */
public abstract class MockServletResponse implements HttpServletResponse {

    /** The is flushed. */
    protected boolean isFlushed;

    /** The error code. */
    protected int errorCode;

    /** The headers. */
    protected Map<String, String> headers;

    /** The headers added. */
    protected Map<String, List<String>> headersAdded;

    /** The sc. */
    protected int sc;

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

    @Override
    public void flushBuffer() throws IOException {
        this.isFlushed = true;
    }

    @Override
    public void sendError(final int sendError) throws IOException {
        this.errorCode = sendError;
    }

    @Override
    public void setHeader(final String name, final String value) {
        this.headers.put(name, value);
    }

    @Override
    public void setStatus(final int status) {
        this.sc = status;
    }

}
