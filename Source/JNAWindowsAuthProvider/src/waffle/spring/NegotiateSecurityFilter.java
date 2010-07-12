/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.springframework.security.core.Authentication;
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
		if (! authorizationHeader.isNull()) {
			
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
				
				Authentication authentication = new WindowsAuthenticationToken(principal);				
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
}
