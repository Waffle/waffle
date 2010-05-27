/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.tomcat;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A Tomcat Negotiate (NTLM, Kerberos) authenticator.
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateAuthenticator extends AuthenticatorBase {

    private static Log _log = LogFactory.getLog(NegotiateAuthenticator.class);
	protected static IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();
    protected static final String _info = "waffle.tomcat.NegotiateAuthenticator/1.0";
    protected PrincipalFormat _principalFormat = PrincipalFormat.fqn;
    protected PrincipalFormat _roleFormat = PrincipalFormat.fqn;

	/**
	 * Windows auth provider.
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

	public NegotiateAuthenticator() {
		_log.debug("[waffle.tomcat.NegotiateAuthenticator] loaded");
	}
	
	@Override
	public void start() {
		_log.info("[waffle.tomcat.NegotiateAuthenticator] started");		
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

	@Override
	public void stop() {
		_log.info("[waffle.tomcat.NegotiateAuthenticator] stopped");		
	}

	@Override
	protected boolean authenticate(Request request, Response response, LoginConfig loginConfig) {
		
		Principal principal = request.getUserPrincipal();
		
		_log.debug("principal: " + 
				((principal == null) ? "<none>" : principal.getName()));
		
		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
		_log.debug("authorization: " + authorizationHeader.toString());
		
		// When using NTLM authentication and the browser is making a POST request, it 
		// preemptively sends a Type 2 authentication message (without the POSTed 
		// data). The server responds with a 401, and the browser sends a Type 3 
		// request with the POSTed data. This is to avoid the situation where user's 
		// credentials might be potentially invalid, and all this data is being POSTed 
		// across the wire.

		boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
		
		_log.debug("request method: " + request.getMethod());
		_log.debug("contentLength: " + request.getContentLength());
		_log.debug("NTLM post: " + ntlmPost);
		
		if (principal != null && ! ntlmPost) {
			// user already authenticated
			_log.debug("previously authenticated user: " + principal.getName());
			return true;
		}
			
		// authenticate user
		if (! authorizationHeader.isNull()) {
			
			String securityPackage = authorizationHeader.getSecurityPackage();
			_log.debug("security package: " + securityPackage);
			
			// maintain a connection-based session for NTLM tokens
			String connectionId = Integer.toString(request.getRemotePort());
			_log.debug("connection id: " + connectionId);
			
			if (ntlmPost) {
				// type 1 NTLM authentication message received
				_auth.resetSecurityToken(connectionId);
			}
			
			// log the user in using the token
			IWindowsSecurityContext securityContext = null;
			
			try {
				byte[] tokenBuffer = authorizationHeader.getTokenBytes();
				_log.debug("token buffer: " + tokenBuffer.length + " bytes");
				securityContext = _auth.acceptSecurityToken(connectionId, tokenBuffer, securityPackage);
				_log.debug("continue required: " + securityContext.getContinue());

				byte[] continueTokenBytes = securityContext.getToken();
				if (continueTokenBytes != null) {
					String continueToken = new String(Base64.encode(continueTokenBytes));
					_log.debug("continue token: " + continueToken);
					response.addHeader("WWW-Authenticate", securityPackage + " " + continueToken);
				}
				
    			if (securityContext.getContinue() || ntlmPost) {
    				response.setHeader("Connection", "keep-alive");
    				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    				response.flushBuffer();
    				return false;
    			}
    			
			} catch (Exception e) {
				_log.warn("error logging in user: " + e.getMessage());
				sendUnauthorized(response);
				return false;
			}
			
			// realm: fail if no realm is configured
			if(context == null || context.getRealm() == null) {
				_log.warn("missing realm");
				sendError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				return false;
			}

			// create and register the user principal with the session
			IWindowsIdentity windowsIdentity = securityContext.getIdentity();
			
			_log.debug("logged in user: " + windowsIdentity.getFqn() + 
					" (" + windowsIdentity.getSidString() + ")");
			
			WindowsPrincipal windowsPrincipal = new WindowsPrincipal(
					windowsIdentity, context.getRealm(), _principalFormat, _roleFormat);
			if (_log.isDebugEnabled()) {
				for(String role : windowsPrincipal.getRoles()) {
					_log.debug(" role: " + role);
				}
			}
			
			principal = windowsPrincipal;
			register(request, response, principal, securityPackage, principal.getName(), null);
			_log.info("successfully logged in user: " + principal.getName());
			
			return true;
		}
		
		_log.debug("authorization required");
		sendUnauthorized(response);
		return false;
	}
	
	/**
	 * Send a 401 Unauthorized along with protocol authentication headers.
	 * @param response
	 *  HTTP Response
	 */
	private void sendUnauthorized(Response response) {
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
	private void sendError(Response response, int code) {
		try {
			response.sendError(code);
		} catch (IOException e) {
			_log.error(e.getMessage());
			throw new RuntimeException(e);
		}		
	}
}
