/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */

package waffle.spring;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import waffle.servlet.WindowsPrincipal;
import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

/**
 * A Spring Negotiate security filter.
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilter extends GenericFilterBean {
    
    private Log _log = LogFactory.getLog(NegotiateSecurityFilter.class);
    private SecurityFilterProviderCollection _provider = null;
    private PrincipalFormat _principalFormat = PrincipalFormat.fqn;
    private PrincipalFormat _roleFormat = PrincipalFormat.fqn;
	private boolean _allowGuestLogin = true;

	private GrantedAuthorityFactory _grantedAuthorityFactory = WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY_FACTORY;
	private GrantedAuthority _defaultGrantedAuthority = WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY;
	
	public NegotiateSecurityFilter() {
		_log.debug("[waffle.spring.NegotiateSecurityFilter] loaded");
	}
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
    	
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
		_log.info(request.getMethod() + " " + request.getRequestURI() + ", contentlength: " + request.getContentLength());

		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
		
		// authenticate user
		if (! authorizationHeader.isNull() 
				&& _provider.isSecurityPackageSupported(authorizationHeader.getSecurityPackage())) {
			
			// log the user in using the token
			IWindowsIdentity windowsIdentity = null;
						
			try {
				
				windowsIdentity = _provider.doFilter(request, response);
				if (windowsIdentity == null) {
					return;
				}
				
			} catch (Exception e) {
				_log.warn("error logging in user: " + e.getMessage());
				sendUnauthorized(response, true);
				return;
			}
			
			if (! _allowGuestLogin && windowsIdentity.isGuest()) {
				_log.warn("guest login disabled: " + windowsIdentity.getFqn());
				sendUnauthorized(response, true);
				return;
			}
			
			try {
				_log.debug("logged in user: " + windowsIdentity.getFqn() + 
						" (" + windowsIdentity.getSidString() + ")");

				WindowsPrincipal principal = new WindowsPrincipal(
						windowsIdentity, _principalFormat, _roleFormat);
				
				_log.debug("roles: " + principal.getRolesString());			
				
				WindowsAuthenticationToken authentication = new WindowsAuthenticationToken(
					principal,
					_grantedAuthorityFactory,
					_defaultGrantedAuthority);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);

				_log.info("successfully logged in user: " + windowsIdentity.getFqn());
				
			} finally {
				windowsIdentity.dispose();
			}
		}
		
		chain.doFilter(request, response);
    }
    
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        
        if (_provider == null) {
        	throw new ServletException("Missing NegotiateSecurityFilter.Provider");
        }
    }

	/**
	 * Send a 401 Unauthorized along with protocol authentication headers.
	 * @param response
	 *  HTTP Response
	 * @param close
	 *  Close connection.
	 */
	private void sendUnauthorized(HttpServletResponse response, boolean close) {
		try {
			_provider.sendUnauthorized(response);
			if (close) {
				response.setHeader("Connection", "close");
			} else {				
				response.setHeader("Connection", "keep-alive");
			}
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			response.flushBuffer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public PrincipalFormat getPrincipalFormat() {
		return _principalFormat;
	}
	
	public void setPrincipalFormat(PrincipalFormat principalFormat) {
		_principalFormat = principalFormat;
	}
	
	public PrincipalFormat getRoleFormat() {
		return _roleFormat;
	}
	
	public void setRoleFormat(PrincipalFormat principalFormat) {
		_roleFormat = principalFormat;
	}
	
	public boolean getAllowGuestLogin() {
		return _allowGuestLogin;
	}
	
	public void setAllowGuestLogin(boolean allowGuestLogin) {
		_allowGuestLogin = allowGuestLogin;
	}
	
	public SecurityFilterProviderCollection getProvider() {
		return _provider;
	}
	
	public void setProvider(SecurityFilterProviderCollection provider) {
		_provider = provider;
	}
	
	public GrantedAuthorityFactory getGrantedAuthorityFactory() {
		return _grantedAuthorityFactory;
	}

	public void setGrantedAuthorityFactory(GrantedAuthorityFactory grantedAuthorityFactory) {
		_grantedAuthorityFactory = grantedAuthorityFactory;
	}

	public GrantedAuthority getDefaultGrantedAuthority() {
		return _defaultGrantedAuthority;
	}

	public void setDefaultGrantedAuthority(GrantedAuthority defaultGrantedAuthority) {
		_defaultGrantedAuthority = defaultGrantedAuthority;
	}
}
