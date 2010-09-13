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
    
	/**
	 * The {@link GrantedAuthorityFactory} that is used by default
	 * if a custom one is not specified.
	 * This default {@link GrantedAuthorityFactory} is a
	 * {@link FqnGrantedAuthorityFactory} with prefix {@code "ROLE_"}
	 * and will convert the fqn to uppercase
	 */
	public static final GrantedAuthorityFactory DEFAULT_GRANTED_AUTHORITY_FACTORY = new FqnGrantedAuthorityFactory("ROLE_", true);
	/**
	 * The {@link GrantedAuthority} that will be added to every 
	 * WindowsAuthenticationToken, unless another (or null) is specified.
	 */
	public static final GrantedAuthority DEFAULT_GRANTED_AUTHORITY = new GrantedAuthorityImpl("ROLE_USER");

	private static final long serialVersionUID = 1L;
	private WindowsPrincipal _principal = null;
	private Collection<GrantedAuthority> _authorities = null;

	/**
	 * Convenience constructor that calls 
	 * {@link #WindowsAuthenticationToken(WindowsPrincipal, GrantedAuthorityFactory, GrantedAuthority)}
	 * with:
	 * <ul>
	 * <li>the given identity,</li>
	 * <li>the {@link #DEFAULT_GRANTED_AUTHORITY_FACTORY}</li>
	 * <li>the {@link #DEFAULT_GRANTED_AUTHORITY}</li>
	 * </ul>
	 */
	public WindowsAuthenticationToken(WindowsPrincipal identity) {
		this(identity, DEFAULT_GRANTED_AUTHORITY_FACTORY, DEFAULT_GRANTED_AUTHORITY);
	}
	
	/**
	 * @param identity The {@link WindowsPrincipal} for which this token exists.
	 * @param grantedAuthorityFactory used to construct {@link GrantedAuthority}s for each of the groups to which the {@link WindowsPrincipal} belongs 
	 * @param defaultGrantedAuthority if not null, this {@link GrantedAuthority} will always be added to the granted authorities list
	 */
	public WindowsAuthenticationToken(WindowsPrincipal identity,
			GrantedAuthorityFactory grantedAuthorityFactory,
			GrantedAuthority defaultGrantedAuthority) {
		
		_principal = identity;
		_authorities = new ArrayList<GrantedAuthority>();
		if (defaultGrantedAuthority != null) {
			_authorities.add(defaultGrantedAuthority);
		}
		for(WindowsAccount group : _principal.getGroups().values()) {
			_authorities.add(grantedAuthorityFactory.createGrantedAuthority(group));
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
