/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import waffle.util.AuthorizationHeader;
import waffle.util.Base64;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A Negotiate (NTLM/Kerberos) Security Filter
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilter implements Filter {

    private static Log _log = LogFactory.getLog(NegotiateSecurityFilter.class);
	private static IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();
    private PrincipalFormat _principalFormat = PrincipalFormat.fqn;
    private PrincipalFormat _roleFormat = PrincipalFormat.fqn;

	public NegotiateSecurityFilter() {
		_log.debug("[waffle.servlet.NegotiateSecurityFilter] loaded");
	}
    
	@Override
	public void destroy() {
		_log.info("[waffle.servlet.NegotiateSecurityFilter] stopped");
	}

	@Override
	public void doFilter(ServletRequest sreq, ServletResponse sres,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) sreq;
		HttpServletResponse response = (HttpServletResponse) sres;

		_log.debug(request.getMethod() + " " + request.getRequestURI() + ", contentlength: " + request.getContentLength());
		
		Principal principal = request.getUserPrincipal();		
		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
		boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
		
		_log.debug("authorization: " + authorizationHeader.toString() + ", ntlm post: " + ntlmPost);
		
		if (principal != null && ! ntlmPost) {
			// user already authenticated
			_log.debug("previously authenticated user: " + principal.getName());
			chain.doFilter(request, response);
		}
			
		// authenticate user
		if (! authorizationHeader.isNull()) {
			
			// extract security package from the authorization header
			String securityPackage = authorizationHeader.getSecurityPackage();
			
			// maintain a connection-based session for NTLM tokens
			String connectionId = Integer.toString(request.getRemotePort());

			_log.debug("security package: " + securityPackage + ", connection id: " + connectionId);
			
			if (ntlmPost) {
				// type 2 NTLM authentication message received
				_auth.resetSecurityToken(connectionId);
			}
			
			// log the user in using the token
			IWindowsSecurityContext securityContext = null;
			
			try {
				byte[] tokenBuffer = authorizationHeader.getTokenBytes();
				_log.debug("token buffer: " + tokenBuffer.length + " byte(s)");
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

			try {
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
						_principalFormat, _roleFormat);
				
				_log.debug("roles: " + windowsPrincipal.getRolesString());			
				subject.getPrincipals().add(windowsPrincipal);
				session.setAttribute("javax.security.auth.subject", subject);
				
				_log.info("successfully logged in user: " + windowsIdentity.getFqn());
				
				NegotiateRequestWrapper requestWrapper = new NegotiateRequestWrapper(
						request, windowsPrincipal);
				
				chain.doFilter(requestWrapper, response);
			} finally {
				windowsIdentity.dispose();
			}

			return;
		}
		
		_log.debug("authorization required");
		sendUnauthorized(response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		_log.info("[waffle.servlet.NegotiateSecurityFilter] started");		
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
}
