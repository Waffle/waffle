/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The Class NtlmServletRequest.
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
     *
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
     *
     * @return the remote host
     */
    private static String getRemoteHost(final HttpServletRequest request) {
        return request.getRemoteHost() == null ? request.getRemoteAddr() : request.getRemoteHost();
    }

}
