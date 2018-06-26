/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.servlet.spi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import waffle.windows.auth.IWindowsIdentity;

/**
 * A security filter provider.
 *
 * @author dblock[at]dblock[dot]org
 */
public interface SecurityFilterProvider {

    /**
     * Add authentication method headers.
     *
     * @param response
     *            Http Response.
     */
    void sendUnauthorized(final HttpServletResponse response);

    /**
     * Returns true if despite having a principal authentication needs to happen.
     *
     * @param request
     *            Http Request.
     * @return True if authentication is required.
     */
    boolean isPrincipalException(final HttpServletRequest request);

    /**
     * Execute filter.
     *
     * @param request
     *            Http Servlet Request.
     * @param response
     *            Http Servlet Response.
     * @return A Windows identity in case authentication completed or NULL if not. Thrown exceptions should be caught
     *         and processed as 401 Access Denied.
     * @throws IOException
     *             on doFilter.
     */
    IWindowsIdentity doFilter(final HttpServletRequest request, final HttpServletResponse response) throws IOException;

    /**
     * Tests whether a specific security package is supported.
     *
     * @param securityPackage
     *            Security package.
     * @return True if the security package is supported, false otherwise.
     */
    boolean isSecurityPackageSupported(final String securityPackage);

    /**
     * Init a parameter.
     *
     * @param parameterName
     *            Parameter name.
     * @param parameterValue
     *            Parameter value.
     */
    void initParameter(final String parameterName, final String parameterValue);
}
