/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

/**
 * A Waffle authentication provider for Spring-security.
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationProvider implements AuthenticationProvider {

    private Log _log = LogFactory.getLog(WindowsAuthenticationProvider.class);
    private PrincipalFormat _principalFormat = PrincipalFormat.fqn;
    private PrincipalFormat _roleFormat = PrincipalFormat.fqn;
	private boolean _allowGuestLogin = true;
	private IWindowsAuthProvider _authProvider = null;
	
	public WindowsAuthenticationProvider() {
		_log.debug("[waffle.spring.WindowsAuthenticationProvider] loaded");		
	}
	
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
	        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
	        IWindowsIdentity windowsIdentity = _authProvider.logonUser(auth.getName(), auth.getCredentials().toString());
			_log.debug("logged in user: " + windowsIdentity.getFqn() + " (" + windowsIdentity.getSidString() + ")");
			
			if (! _allowGuestLogin && windowsIdentity.isGuest()) {
				_log.warn("guest login disabled: " + windowsIdentity.getFqn());
				throw new GuestLoginDisabledAuthenticationException(windowsIdentity.getFqn());
			}
			
	        WindowsPrincipal windowsPrincipal = new WindowsPrincipal(windowsIdentity, _principalFormat, _roleFormat);
			_log.debug("roles: " + windowsPrincipal.getRolesString());			
	        WindowsAuthenticationToken token = new WindowsAuthenticationToken(windowsPrincipal);
			_log.info("successfully logged in user: " + windowsIdentity.getFqn());
			return token;
        } catch (Exception e) {
        	throw new AuthenticationServiceException(e.getMessage(), e);
        }
	}

	public boolean supports(Class<? extends Object> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
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
	
	public IWindowsAuthProvider getAuthProvider() {
		return _authProvider;
	}
	
	public void setAuthProvider(IWindowsAuthProvider authProvider) {
		_authProvider = authProvider;
	}
}
