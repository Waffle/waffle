/*
 * MIT License
 *
 * Copyright (c) 2010-2024 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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

}
