/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet.spi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import waffle.windows.auth.IWindowsIdentity;

/**
 * A security filter provider.
 */
public interface SecurityFilterProvider {

    /**
     * Add authentication method headers.
     *
     * @param response
     *            Http Response.
     */
    void sendUnauthorized(HttpServletResponse response);

    /**
     * Returns true if despite having a principal authentication needs to happen.
     *
     * @param request
     *            Http Request.
     *
     * @return True if authentication is required.
     */
    boolean isPrincipalException(HttpServletRequest request);

    /**
     * Execute filter.
     *
     * @param request
     *            Http Servlet Request.
     * @param response
     *            Http Servlet Response.
     *
     * @return A Windows identity in case authentication completed or NULL if not. Thrown exceptions should be caught
     *         and processed as 401 Access Denied.
     *
     * @throws IOException
     *             on doFilter.
     */
    IWindowsIdentity doFilter(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * Tests whether a specific security package is supported.
     *
     * @param securityPackage
     *            Security package.
     *
     * @return True if the security package is supported, false otherwise.
     */
    boolean isSecurityPackageSupported(String securityPackage);

    /**
     * Init a parameter.
     *
     * @param parameterName
     *            Parameter name.
     * @param parameterValue
     *            Parameter value.
     */
    void initParameter(String parameterName, String parameterValue);
}
