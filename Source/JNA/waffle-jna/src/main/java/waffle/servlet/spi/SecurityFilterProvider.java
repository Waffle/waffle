/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
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
     *            Http Response
     */
    void sendUnauthorized(HttpServletResponse response);

    /**
     * Returns true if despite having a principal authentication needs to happen.
     * 
     * @param request
     *            Http Request
     * @return True if authentication is required.
     */
    boolean isPrincipalException(HttpServletRequest request);

    /**
     * Execute filter.
     * 
     * @param request
     *            Http Request
     * @param response
     *            Http Response
     * @return A Windows identity in case authentication completed or NULL if not. Thrown exceptions should be caught
     *         and processed as 401 Access Denied.
     */
    IWindowsIdentity doFilter(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * Tests whether a specific security package is supported.
     * 
     * @param securityPackage
     *            Security package.
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
