/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2017 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.spring;

import org.springframework.security.core.GrantedAuthority;

import waffle.windows.auth.WindowsAccount;

/**
 * Used by {@link WindowsAuthenticationToken} to convert {@link WindowsAccount}s representing groups into
 * {@link GrantedAuthority}s.
 */
@FunctionalInterface
public interface GrantedAuthorityFactory {

    /**
     * Creates a {@link GrantedAuthority} from the given {@link WindowsAccount}.
     *
     * @param windowsAccount
     *            the windows account
     * @return the granted authority
     */
    GrantedAuthority createGrantedAuthority(final WindowsAccount windowsAccount);

}
