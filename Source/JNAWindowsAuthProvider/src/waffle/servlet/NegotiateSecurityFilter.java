/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;

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

import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A Negotiate (NTLM/Kerberos) Security Filter
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilter implements Filter {

    private static Log _log = LogFactory.getLog(NegotiateSecurityFilter.class);
    private PrincipalFormat _principalFormat = PrincipalFormat.fqn;
    private PrincipalFormat _roleFormat = PrincipalFormat.fqn;
    private SecurityFilterProviderCollection _providers = null;
	private static IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();
	private boolean _allowGuestLogin = true;

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

		_log.info(request.getMethod() + " " + request.getRequestURI() + ", contentlength: " + request.getContentLength());
		
		Principal principal = request.getUserPrincipal();
		if (principal != null && ! _providers.isPrincipalException(request)) {
			// user already authenticated
			_log.info("previously authenticated user: " + principal.getName());
			chain.doFilter(request, response);
			return;
		}
		
		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
		
		// authenticate user
		if (! authorizationHeader.isNull()) {
			
			// log the user in using the token
			IWindowsIdentity windowsIdentity = null;
						
			try {
				
				windowsIdentity = _providers.doFilter(request, response);
				if (windowsIdentity == null) {
					return;
				}
				
			} catch (Exception e) {
				_log.warn("error logging in user: " + e.getMessage());
				sendUnauthorized(response);
				return;
			}
			
			if (! _allowGuestLogin && windowsIdentity.isGuest()) {
				_log.warn("guest login disabled: " + windowsIdentity.getFqn());
				sendUnauthorized(response);
				return;					
			}
			
			try {
				_log.info("logged in user: " + windowsIdentity.getFqn() + 
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
				
				_log.info("roles: " + windowsPrincipal.getRolesString());			
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
		
		_log.info("authorization required");
		sendUnauthorized(response);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (filterConfig != null) {
			Enumeration parameterNames = filterConfig.getInitParameterNames();
			while(parameterNames.hasMoreElements()) {
				String parameterName = (String) parameterNames.nextElement();
				String parameterValue = filterConfig.getInitParameter(parameterName);
				_log.debug(parameterName + "=" + parameterValue);
				if (parameterName.equals("principalFormat")) {
					_principalFormat = PrincipalFormat.parse(parameterValue);
				} else if (parameterName.equals("roleFormat")) {
					_roleFormat = PrincipalFormat.parse(parameterValue);
				} else if (parameterName.equals("allowGuestLogin")) {
					_allowGuestLogin = Boolean.parseBoolean(parameterValue);
				} else if (parameterName.equals("securityFilterProviders")) {
					_providers = new SecurityFilterProviderCollection(
							parameterValue.split("\n"), _auth);
				} else {
					_log.error("invalid parameter: " + parameterName);
					throw new ServletException("Invalid parameter: " + parameterName);
				}
			}
		}
		if (_providers == null) {
			_log.debug("initializing default secuirty filter providers");
			_providers = new SecurityFilterProviderCollection(_auth);
		}
		_log.info("[waffle.servlet.NegotiateSecurityFilter] started");		
	}
	
	/**
	 * Set the principal format.
	 * @param format
	 *  Principal format.
	 */
	public void setPrincipalFormat(String format) {
		_principalFormat = PrincipalFormat.parse(format);
		_log.info("principal format: " + _principalFormat);
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
		_log.info("role format: " + _roleFormat);
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
			_providers.sendUnauthorized(response);
			response.setHeader("Connection", "close");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			response.flushBuffer();		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
	
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
}
