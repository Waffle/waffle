package waffle.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

abstract class WaffleAuthenticatorBase extends AuthenticatorBase {
    protected String _info = null;
    protected Log _log = null;
    protected PrincipalFormat _principalFormat = PrincipalFormat.fqn;
    protected PrincipalFormat _roleFormat = PrincipalFormat.fqn;
	
    protected static IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();

	/**
	 * Windows authentication provider.
	 * @return
	 *  IWindowsAuthProvider.
	 */
	public static IWindowsAuthProvider getAuth() {
		return _auth;
	}
	
	/**
	 * Set Windows auth provider.
	 * @param provider
	 *  Class implements IWindowsAuthProvider.
	 */
	public static void setAuth(IWindowsAuthProvider provider) {
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
		_principalFormat = PrincipalFormat.parse(format);
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
		_roleFormat = PrincipalFormat.parse(format);
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
	 * Send a 401 Unauthorized along with protocol authentication headers.
	 * @param response
	 *  HTTP Response
	 */
	protected void sendUnauthorized(Response response) {
		try {
			response.addHeader("WWW-Authenticate", "Negotiate");
			response.addHeader("WWW-Authenticate", "NTLM");
			response.setHeader("Connection", "close");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
