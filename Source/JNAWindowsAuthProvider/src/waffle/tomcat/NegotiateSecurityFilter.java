/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.tomcat;

import java.io.IOException;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A Negotiate authentication security filter.
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilter implements Filter {

    private static Log _log = LogFactory.getLog(NegotiateSecurityFilter.class);
	private static IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();
    private PrincipalFormat _principalFormat = PrincipalFormat.fqn;
    private PrincipalFormat _roleFormat = PrincipalFormat.fqn;

	public NegotiateSecurityFilter() {
		_log.debug("[waffle.tomcat.NegotiateSecurityFilter] loaded");
	}
    
	@Override
	public void destroy() {
		_log.info("[waffle.tomcat.NegotiateSecurityFilter] stopped");
	}

	@Override
	public void doFilter(ServletRequest sreq, ServletResponse sres,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) sreq;
		HttpServletResponse response = (HttpServletResponse) sres;

		Principal principal = request.getUserPrincipal();
		
		_log.debug("principal: " + 
				((principal == null) ? "<none>" : principal.getName()));
		
		String authorization = request.getHeader("Authorization");

		_log.debug("authorization: " + 
				((authorization == null) ? "<none>" : authorization));
		
		// When using NTLM authentication and the browser is making a POST request, it 
		// preemptively sends a Type 2 authentication message (without the POSTed 
		// data). The server responds with a 401, and the browser sends a Type 3 
		// request with the POSTed data. This is to avoid the situation where user's 
		// credentials might be potentially invalid, and all this data is being POSTed 
		// across the wire.

		boolean ntlmPost = (request.getMethod() == "POST" 
			&& request.getContentLength() == 0
			&& authorization != null);
		
		_log.debug("request method: " + request.getMethod());
		_log.debug("contentLength: " + request.getContentLength());
		_log.debug("NTLM post: " + ntlmPost);
		
		if (principal != null && ! ntlmPost) {
			// user already authenticated
			_log.debug("previously authenticated user: " + principal.getName());
			chain.doFilter(request, response);
		}
			
		// authenticate user
		if (authorization != null) {
			
			// extract security package from the authorization header
			String securityPackage = getSecurityPackage(authorization);
			_log.debug("security package: " + securityPackage);
			
			// maintain a connection-based session for NTLM tokens
			String connectionId = Integer.toString(request.getRemotePort());
			_log.debug("connection id: " + connectionId);
			
			if (ntlmPost) {
				// type 2 NTLM authentication message received
				_auth.resetSecurityToken(connectionId);
			}
			
			// log the user in using the token
			IWindowsSecurityContext securityContext = null;
			String token = authorization.substring(securityPackage.length() + 1);
			
			try {
				byte[] tokenBuffer = Base64.decode(token);
				_log.debug("token buffer: " + tokenBuffer.length + " bytes");
				securityContext = _auth.acceptSecurityToken(connectionId, tokenBuffer, securityPackage);
				
				byte[] continueTokenBytes = securityContext.getToken();
				if (continueTokenBytes != null) {
					String continueToken = new String(Base64.encode(continueTokenBytes));
					_log.debug("continue token: " + continueToken);
					response.addHeader("WWW-Authenticate", securityPackage + " " + continueToken);
				}
				
				_log.debug("continue required: " + securityContext.getContinue());
    			if (securityContext.getContinue() || ntlmPost) {
    				response.setHeader("Connection", "keep-alive");
    				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    				response.flushBuffer();
    				return;
    			}
			} catch (Exception e) {
				_log.warn("error logging in user: " + e.getMessage());
				sendUnauthorized(response);
				return;
			}

			IWindowsIdentity windowsIdentity = securityContext.getIdentity();
			
			// store the logged in user

			_log.debug("logged in user: " + windowsIdentity.getFqn() + 
					" (" + windowsIdentity.getSidString() + ")");
			
			HttpSession session = request.getSession(true);
			if (session == null) {
				throw new ServletException("Expected HttpSession");
			}
			
			Subject subject = (Subject) session.getAttribute("javax.security.auth.subject");			
			if (subject == null) {
				subject = new Subject();
			}
						
			WindowsPrincipal windowsPrincipal = new WindowsPrincipal(windowsIdentity, 
					null, _principalFormat, _roleFormat);
			
			if (_log.isDebugEnabled()) {
				for(String role : windowsPrincipal.getRoles()) {
					_log.debug(" role: " + role);
				}
			}
			
			subject.getPrincipals().add(windowsPrincipal);
			session.setAttribute("javax.security.auth.subject", subject);
			
			_log.info("successfully logged in user: " + windowsIdentity.getFqn());
			
			NegotiateRequestWrapper requestWrapper = new NegotiateRequestWrapper(
					request, windowsPrincipal);
			
			chain.doFilter(requestWrapper, response);
			return;
		}
		
		_log.debug("authorization required");
		sendUnauthorized(response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		_log.info("[waffle.tomcat.NegotiateSecurityFilter] started");		
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
	private void sendUnauthorized(HttpServletResponse response) {
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
	 * Returns a supported security package string.
	 * @param authorization
	 *  Authorization header.
	 * @return
	 *  Negotiate or NTLM.
	 */
	private static String getSecurityPackage(String authorization) {
		if (authorization.startsWith("Negotiate ")) {
			return "Negotiate";
		} else if (authorization.startsWith("NTLM ")) {
			return "NTLM";
		} else {
			throw new RuntimeException("Unsupported security package: " + authorization);
		}		
	}
}
