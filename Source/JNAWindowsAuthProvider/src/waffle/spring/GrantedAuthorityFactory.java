/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.spring;

import org.springframework.security.core.GrantedAuthority;

import waffle.windows.auth.WindowsAccount;

/**
 * Used by {@link WindowsAuthenticationToken} to convert {@link WindowsAccount}s
 * representing groups into {@link GrantedAuthority}s.
 */
public interface GrantedAuthorityFactory {
	
	/**
	 * Creates a {@link GrantedAuthority} from the given {@link WindowsAccount}.
	 */
	GrantedAuthority createGrantedAuthority(WindowsAccount windowsAccount);

}
