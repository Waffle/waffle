/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.spring;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.WindowsAccount;

/**
 * A Windows authentication token.
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationToken implements Authentication {

	private static final long serialVersionUID = 1L;
	private WindowsPrincipal _principal = null;
	private Collection<GrantedAuthority> _authorities = null;

	public WindowsAuthenticationToken(WindowsPrincipal identity) {
		_principal = identity;
		_authorities = new ArrayList<GrantedAuthority>();
		_authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
		for(WindowsAccount group : _principal.getGroups().values()) {
			_authorities.add(new GrantedAuthorityImpl("ROLE_" + group.getFqn().toUpperCase()));
		}
	}
	
	public Collection<GrantedAuthority> getAuthorities() {
		return _authorities;
	}

	public Object getCredentials() {
		return null;
	}

	public Object getDetails() {
		return null;
	}

	public Object getPrincipal() {
		return _principal;
	}

	public boolean isAuthenticated() {
		return (_principal != null);
	}

	public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
		throw new IllegalArgumentException();	
	}

	public String getName() {
		return _principal.getName();
	}
}
