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
package waffle.apache;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Response;
import org.slf4j.Logger;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import static java.util.Arrays.asList;

/**
 * @author dblock[at]dblock[dot]org
 */
abstract class WaffleAuthenticatorBase extends AuthenticatorBase {

	private static final Set<String> SUPPORTED_PROTOCOLS = new LinkedHashSet<String>(asList("Negotiate", "NTLM"));

	protected String _info;
	protected Logger _log;
	protected PrincipalFormat _principalFormat = PrincipalFormat.fqn;
	protected PrincipalFormat _roleFormat = PrincipalFormat.fqn;
	protected boolean _allowGuestLogin = true;
	protected Set<String> _protocols = SUPPORTED_PROTOCOLS;

	protected IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();

	/**
	 * Windows authentication provider.
	 * 
	 * @return IWindowsAuthProvider.
	 */
	public IWindowsAuthProvider getAuth() {
		return _auth;
	}

	/**
	 * Set Windows auth provider.
	 * 
	 * @param provider
	 *            Class implements IWindowsAuthProvider.
	 */
	public void setAuth(IWindowsAuthProvider provider) {
		_auth = provider;
	}

	@Override
	public String getInfo() {
		return _info;
	}

	/**
	 * Set the principal format.
	 * 
	 * @param format
	 *            Principal format.
	 */
	public void setPrincipalFormat(String format) {
		_principalFormat = PrincipalFormat.valueOf(format);
		_log.debug("principal format: {}", _principalFormat);
	}

	/**
	 * Principal format.
	 * 
	 * @return Principal format.
	 */
	public PrincipalFormat getPrincipalFormat() {
		return _principalFormat;
	}

	/**
	 * Set the principal format.
	 * 
	 * @param format
	 *            Role format.
	 */
	public void setRoleFormat(String format) {
		_roleFormat = PrincipalFormat.valueOf(format);
		_log.debug("role format: {}", _roleFormat);
	}

	/**
	 * Principal format.
	 * 
	 * @return Role format.
	 */
	public PrincipalFormat getRoleFormat() {
		return _roleFormat;
	}

	/**
	 * True if Guest login permitted.
	 * 
	 * @return True if Guest login permitted, false otherwise.
	 */
	public boolean isAllowGuestLogin() {
		return _allowGuestLogin;
	}

	/**
	 * Set whether Guest login is permitted. Default is true, if the Guest account is enabled, an invalid
	 * username/password results in a Guest login.
	 * 
	 * @param value
	 *            True or false.
	 */
	public void setAllowGuestLogin(boolean value) {
		_allowGuestLogin = value;
	}

	/**
	 * Set the authentication protocols. Default is "Negotiate, NTLM".
	 *
	 * @param protocols
	 *            Authentication protocols
	 */
	public void setProtocols(String protocols) {
		_protocols = new LinkedHashSet<String>();
		String[] protocolNames = protocols.split(",");
		for (String protocolName : protocolNames) {
			protocolName = protocolName.trim();
			if (!protocolName.isEmpty()) {
				_log.debug("init protocol: {}", protocolName);
				if (SUPPORTED_PROTOCOLS.contains(protocolName)) {
					_protocols.add(protocolName);
				} else {
					_log.error("unsupported protocol: {}", protocolName);
					throw new RuntimeException("Unsupported protocol: "
							+ protocolName);
				}
			}
		}
	}

	/**
	 * Send a 401 Unauthorized along with protocol authentication headers.
	 * 
	 * @param response
	 *            HTTP Response
	 */
	protected void sendUnauthorized(Response response) {
		try {
			for (String protocol : _protocols) {
				response.addHeader("WWW-Authenticate", protocol);
			}
			response.setHeader("Connection", "close");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			response.flushBuffer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Send an error code.
	 * 
	 * @param response
	 *            HTTP Response
	 * @param code
	 *            Error Code
	 */
	protected void sendError(Response response, int code) {
		try {
			response.sendError(code);
		} catch (IOException e) {
			_log.error(e.getMessage());
			_log.trace("{}", e);
			throw new RuntimeException(e);
		}
	}
}
