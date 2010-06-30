/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet.spi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import waffle.windows.auth.IWindowsIdentity;

/**
 * A security filter provider.
 * @author dblock[at]dblock[dot]org
 */
public interface SecurityFilterProvider {

	/**
	 * Add authentication method headers.
	 * @param response
	 *  Http Response
	 */
	public void sendUnauthorized(HttpServletResponse response);
	
	/**
	 * Returns true if despite having a principal authentication needs to happen.
	 * @param request
	 *  Http Request
	 * @return
	 *  True if authentication is required.
	 */
	public boolean isPrincipalException(HttpServletRequest request);
	
	/**
	 * Execute filter.
	 * @param request
	 *  Http Request
	 * @param response
	 *  Http Response
	 * @return
	 *  A Windows identity in case authentication completed or NULL if not.
	 *  Thrown exceptions should be caught and processed as 401 Access Denied.
	 */
	public IWindowsIdentity doFilter(HttpServletRequest request, 
			HttpServletResponse response) throws IOException;

	/**
	 * Tests whether a specific security package is supported.
	 * @param securityPackage
	 *  Security package.
	 * @return
	 *  True if the security package is supported, false otherwise.
	 */
	public boolean isSecurityPackageSupported(String securityPackage);

	/**
	 * Init a parameter.
	 * @param parameterName
	 *  Parameter name.
	 * @param parameterValue
	 *  Parameter value.
	 */
	public void initParameter(String parameterName, String parameterValue);
}
