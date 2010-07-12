/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.apache;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Response;
import org.apache.commons.logging.Log;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
abstract class WaffleAuthenticatorBase extends AuthenticatorBase {
    protected String _info = null;
    protected Log _log = null;
    protected PrincipalFormat _principalFormat = PrincipalFormat.fqn;
    protected PrincipalFormat _roleFormat = PrincipalFormat.fqn;
	protected boolean _allowGuestLogin = true;
	
    protected IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();

	/**
	 * Windows authentication provider.
	 * @return
	 *  IWindowsAuthProvider.
	 */
	public IWindowsAuthProvider getAuth() {
		return _auth;
	}
	
	/**
	 * Set Windows auth provider.
	 * @param provider
	 *  Class implements IWindowsAuthProvider.
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
	 * @param format
	 *  Principal format.
	 */
	public void setPrincipalFormat(String format) {
		_principalFormat = PrincipalFormat.valueOf(format);
		_log.debug("principal format: " + _principalFormat);
	}

	/**
	 * Principal format.
	 * @return
	 *  Principal format.
	 */
	public PrincipalFormat getPrincipalFormat() {
		return _principalFormat;
	}

	/**
	 * Set the principal format.
	 * @param format
	 *  Role format.
	 */
	public void setRoleFormat(String format) {
		_roleFormat = PrincipalFormat.valueOf(format);
		_log.debug("role format: " + _roleFormat);
	}

	/**
	 * Principal format.
	 * @return
	 *  Role format.
	 */
	public PrincipalFormat getRoleFormat() {
		return _roleFormat;
	}

	/**
	 * True if Guest login permitted.
	 * @return
	 *  True if Guest login permitted, false otherwise.
	 */
	public boolean getAllowGuestLogin() {
		return _allowGuestLogin;
	}
	
	/**
	 * Set whether Guest login is permitted.
	 * Default is true, if the Guest account is enabled, an invalid username/password
	 * results in a Guest login.
	 * @param value
	 *  True or false.
	 */
	public void setAllowGuestLogin(boolean value) {
		_allowGuestLogin = value;
	}
	
	/**
	 * Send a 401 Unauthorized along with protocol authentication headers.
	 * @param response
	 *  HTTP Response
	 */
	protected void sendUnauthorized(Response response) {
		try {
			response.addHeader("WWW-Authenticate", "Negotiate");
			response.addHeader("WWW-Authenticate", "NTLM");
			response.setHeader("Connection", "close");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			response.flushBuffer();		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Send an error code.
	 * @param response
	 *  HTTP Response
	 * @param code
	 *  Error Code
	 */
	protected void sendError(Response response, int code) {
		try {
			response.sendError(code);
		} catch (IOException e) {
			_log.error(e.getMessage());
			throw new RuntimeException(e);
		}		
	}
}
