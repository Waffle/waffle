/*******************************************************************************
* Waffle (http://waffle.codeplex.com)
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
package waffle.servlet;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Negotiate Request wrapper.
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateRequestWrapper extends HttpServletRequestWrapper {

	private WindowsPrincipal _principal;
	
	public NegotiateRequestWrapper(HttpServletRequest request, WindowsPrincipal principal) {
		super(request);
		_principal = principal;
	}

	/**
	 * User principal.
	 */
	@Override
	public Principal getUserPrincipal() {
		return _principal;
	}
	
	/**
	 * Authentication type.
	 */
	@Override
	public String getAuthType() {
		return "NEGOTIATE";
	}
	
	/**
	 * Remote username.
	 */
	@Override
	public String getRemoteUser() {
		return _principal.getName();
	}
	
	/**
	 * Returns true if the user is in a given role.
	 */
	@Override
	public boolean isUserInRole(String role) {
		return _principal.hasRole(role);
	}
}
