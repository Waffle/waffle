/*******************************************************************************
* Waffle (http://waffle.codeplex.com)
* 
* Copyright (c) 2010 Application Security, Inc.
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Application Security, Inc.
*******************************************************************************/
package waffle.spring;

import org.springframework.security.GrantedAuthority;

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
