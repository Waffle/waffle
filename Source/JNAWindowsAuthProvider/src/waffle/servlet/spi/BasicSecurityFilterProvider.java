/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet.spi;

import java.io.IOException;
import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;

/**
 * A Basic authentication security filter provider.
 * http://tools.ietf.org/html/rfc2617
 * @author dblock[at]dblock[dot]org
 */
public class BasicSecurityFilterProvider implements SecurityFilterProvider {

    private Log _log = LogFactory.getLog(BasicSecurityFilterProvider.class);
	private String _realm = "BasicSecurityFilterProvider";
	private IWindowsAuthProvider _auth = null;

	public BasicSecurityFilterProvider(IWindowsAuthProvider auth) {
		_auth = auth;		
	}
	
	public IWindowsIdentity doFilter(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
		String usernamePassword = new String(authorizationHeader.getTokenBytes());
		String[] usernamePasswordArray = usernamePassword.split(":", 2);
		if (usernamePasswordArray.length != 2) {
			throw new RuntimeException("Invalid username:password in Authorization header.");
		}
		_log.debug("logging in user: " + usernamePasswordArray[0]);
		return _auth.logonUser(usernamePasswordArray[0], usernamePasswordArray[1]);        
	}

	public boolean isPrincipalException(HttpServletRequest request) {
		return false;
	}

	public boolean isSecurityPackageSupported(String securityPackage) {
		return securityPackage.equalsIgnoreCase("Basic");
	}

	public void sendUnauthorized(HttpServletResponse response) {
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + _realm + "\"");
	}
	
	/**
	 * Protection space.
	 * @return
	 *  Name of the protection space.
	 */
	public String getRealm() {
		return _realm;
	}
	
	/**
	 * Set the protection space.
	 * @param realm
	 *  Protection space name.
	 */
	public void setRealm(String realm) {
		_realm = realm;
	}

	/**
	 * Init configuration parameters.
	 */
	public void initParameter(String parameterName, String parameterValue) {
		if (parameterName.equals("realm")) {
			setRealm(parameterValue);
		} else {
			throw new InvalidParameterException(parameterName);
		}
	}
}
