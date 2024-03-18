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
package waffle.servlet.spi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
