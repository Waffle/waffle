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
package waffle.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The Class NtlmServletRequest.
 *
 * @author dblock[at]dblock[dot]org
 */
public final class NtlmServletRequest {

    /**
     * Instantiates a new ntlm servlet request.
     */
    private NtlmServletRequest() {
        // Prevent Instantiation of object
    }

    /**
     * Returns a unique connection id for a given servlet request.
     *
     * @param request
     *            Servlet request.
     * @return String.
     */
    public static String getConnectionId(final HttpServletRequest request) {
        final String remoteHost = NtlmServletRequest.getRemoteHost(request);
        return String.join(":", remoteHost == null ? "" : remoteHost, String.valueOf(request.getRemotePort()));
    }

    /**
     * Gets the remote host.
     *
     * @param request
     *            the request
     * @return the remote host
     */
    private static String getRemoteHost(final HttpServletRequest request) {
        return request.getRemoteHost() == null ? request.getRemoteAddr() : request.getRemoteHost();
    }

}
