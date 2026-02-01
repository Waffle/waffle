/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
     *
     * @return the granted authority
     */
    GrantedAuthority createGrantedAuthority(WindowsAccount windowsAccount);

}
