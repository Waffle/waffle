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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.windows.auth.PrincipalFormat;

/**
 * A Waffle authentication provider for Spring-security.
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateAuthenticationProvider implements AuthenticationProvider {

    private Log _log = LogFactory.getLog(NegotiateAuthenticationProvider.class);
    private PrincipalFormat _principalFormat = PrincipalFormat.fqn;
    private PrincipalFormat _roleFormat = PrincipalFormat.fqn;
	private boolean _allowGuestLogin = true;
    private SecurityFilterProviderCollection _providers = null;
	
	public NegotiateAuthenticationProvider() {
		_log.debug("[waffle.spring.NegotiateAuthenticationProvider] loaded");		
	}
	
	public Authentication authenticate(Authentication auth)
			throws AuthenticationException {
		return null;
	}

	public boolean supports(Class<? extends Object> auth) {
		return false;
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
	
	public SecurityFilterProviderCollection getProviders() {
		return _providers;
	}
	
	public void setProviders(SecurityFilterProviderCollection providers) {
		_providers = providers;
	}
}
