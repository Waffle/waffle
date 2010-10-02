/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
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
