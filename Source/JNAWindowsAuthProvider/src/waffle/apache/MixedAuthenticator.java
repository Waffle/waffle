/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.apache;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.commons.logging.LogFactory;

import waffle.util.AuthorizationHeader;
import waffle.util.Base64;
import waffle.util.NtlmServletRequest;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * Mixed Negotiate + Form Authenticator.
 * @author dblock[at]dblock[dot]org
 */
public class MixedAuthenticator extends WaffleAuthenticatorBase {

	public MixedAuthenticator() {
		super();
		_log = LogFactory.getLog(MixedAuthenticator.class);
    	_info = "waffle.apache.MixedAuthenticator/1.0";
    	_log.debug("[waffle.apache.MixedAuthenticator] loaded");
	}

	@Override
	public void start() {
		_log.info("[waffle.apache.MixedAuthenticator] started");		
	}
	
	@Override
	public void stop() {
		_log.info("[waffle.apache.MixedAuthenticator] stopped");		
	}

	@Override
	protected boolean authenticate(Request request, Response response, LoginConfig loginConfig) {

		// realm: fail if no realm is configured
		if(context == null || context.getRealm() == null) {
			_log.warn("missing context/realm");
			sendError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return false;
		}

		_log.debug(request.getMethod() + " " + request.getRequestURI() + ", contentlength: " + request.getContentLength());
		
		String queryString = request.getQueryString();
		boolean negotiateCheck = (queryString != null && queryString.equals("j_negotiate_check"));
		_log.debug("negotiateCheck: " + negotiateCheck + " (" +  ((queryString == null) ? "<none>" : queryString) + ")");
		boolean securityCheck = (queryString != null && queryString.equals("j_security_check"));
		_log.debug("securityCheck: " + securityCheck + " (" +  ((queryString == null) ? "<none>" : queryString) + ")");

		Principal principal = request.getUserPrincipal();
		
		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);		
		boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
		_log.debug("authorization: " + authorizationHeader.toString() + ", ntlm post: " + ntlmPost);
	
		if (principal != null && ! ntlmPost) {
			_log.debug("previously authenticated user: " + principal.getName());
			return true;
		} else if (negotiateCheck) {
			if (! authorizationHeader.isNull()) {
				return negotiate(request, response, authorizationHeader);
			} else {
				_log.debug("authorization required");
				sendUnauthorized(response);
				return false;
			}
		} else if (securityCheck) {
			boolean postResult = post(request, response, loginConfig);
			if (postResult) {
				redirectTo(request, response, request.getServletPath());
			} else {
				redirectTo(request, response, loginConfig.getErrorPage());
			}
			return postResult;
		} else {
			redirectTo(request, response, loginConfig.getLoginPage());
			return false;
		}
	}
	
	private boolean negotiate(Request request, Response response, AuthorizationHeader authorizationHeader) {

		String securityPackage = authorizationHeader.getSecurityPackage();			
		// maintain a connection-based session for NTLM tokens
		String connectionId = NtlmServletRequest.getConnectionId(request);
		
		_log.debug("security package: " + securityPackage + ", connection id: " + connectionId);
		
		boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
		
		if (ntlmPost) {
			// type 1 NTLM authentication message received
			_auth.resetSecurityToken(connectionId);
		}
		
		// log the user in using the token
		IWindowsSecurityContext securityContext = null;
		
		try {
			byte[] tokenBuffer = authorizationHeader.getTokenBytes();
			_log.debug("token buffer: " + tokenBuffer.length + " byte(s)");
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
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				response.flushBuffer();
				return false;
			}
			
		} catch (Exception e) {
			_log.warn("error logging in user: " + e.getMessage());
			sendUnauthorized(response);
			return false;
		}
		
		// create and register the user principal with the session
		IWindowsIdentity windowsIdentity = securityContext.getIdentity();
		
		// disable guest login
		if (! _allowGuestLogin && windowsIdentity.isGuest()) {
			_log.warn("guest login disabled: " + windowsIdentity.getFqn());
			sendUnauthorized(response);
			return false;			
		}
		
		try {
			
			_log.debug("logged in user: " + windowsIdentity.getFqn() + 
					" (" + windowsIdentity.getSidString() + ")");
			
			GenericWindowsPrincipal windowsPrincipal = new GenericWindowsPrincipal(
					windowsIdentity, context.getRealm(), _principalFormat, _roleFormat);
			
			_log.debug("roles: " + windowsPrincipal.getRolesString());
	
			// create a session associated with this request if there's none
			HttpSession session = request.getSession(true);
			_log.debug("session id:" + session.getId());

			register(request, response, windowsPrincipal, securityPackage, windowsPrincipal.getName(), null);
			_log.info("successfully logged in user: " + windowsPrincipal.getName());
			
		} finally {
			windowsIdentity.dispose();
		}
		
		return true;
	}
	
	private boolean post(Request request, Response response, LoginConfig loginConfig) {
		
		String username = request.getParameter("j_username");
		String password = request.getParameter("j_password");
		
		_log.debug("logging in: " + username);
		
		IWindowsIdentity windowsIdentity = null;
        try {
        	windowsIdentity = _auth.logonUser(username, password);
        } catch (Exception e) {
        	_log.error(e.getMessage());
        	return false;
        }

		// disable guest login
		if (! _allowGuestLogin && windowsIdentity.isGuest()) {
			_log.warn("guest login disabled: " + windowsIdentity.getFqn());
			return false;
		}
        
        try {
	        _log.debug("successfully logged in " + username + " (" + windowsIdentity.getSidString() + ")");       
	        
			GenericWindowsPrincipal windowsPrincipal = new GenericWindowsPrincipal(
					windowsIdentity, context.getRealm(), _principalFormat, _roleFormat);
			
			_log.debug("roles: " + windowsPrincipal.getRolesString());
			
			// create a session associated with this request if there's none
			HttpSession session = request.getSession(true);
			_log.debug("session id:" + session.getId());

			register(request, response, windowsPrincipal, "FORM", windowsPrincipal.getName(), null);
			_log.info("successfully logged in user: " + windowsPrincipal.getName());
        } finally {
        	windowsIdentity.dispose();
        }
		
		return true;
	}
	
	private void redirectTo(Request request, Response response, String url) {
		try {
			_log.debug("redirecting to: " + url);
			ServletContext servletContext = context.getServletContext();
			RequestDispatcher disp = servletContext.getRequestDispatcher(url);
			disp.forward(request.getRequest(), response);
		} catch (IOException e) {
			_log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (ServletException e) {
			_log.error(e.getMessage());
			throw new RuntimeException(e);
		}		
	}
}

