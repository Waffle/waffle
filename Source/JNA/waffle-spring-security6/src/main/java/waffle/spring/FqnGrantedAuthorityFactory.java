/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring;

import java.util.Locale;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import waffle.windows.auth.WindowsAccount;

/**
 * A {@link GrantedAuthorityFactory} that uses the {@link WindowsAccount}'s fqn as the basis of the
 * {@link GrantedAuthority} string, and (optionally) applies two transformations:
 * <ul>
 * <li>prepending a prefix, and</li>
 * <li>converting to uppercase</li>
 * </ul>
 * .
 */
public class FqnGrantedAuthorityFactory implements GrantedAuthorityFactory {

    /** The prefix. */
    private final String prefix;

    /** The convert to upper case. */
    private final boolean convertToUpperCase;

    /**
     * Instantiates a new fqn granted authority factory.
     *
     * @param newPrefix
     *            the new prefix
     * @param newConvertToUpperCase
     *            the new convert to upper case
     */
    public FqnGrantedAuthorityFactory(final String newPrefix, final boolean newConvertToUpperCase) {
        this.prefix = newPrefix;
        this.convertToUpperCase = newConvertToUpperCase;
    }

    @Override
    public GrantedAuthority createGrantedAuthority(final WindowsAccount windowsAccount) {

        String grantedAuthorityString = windowsAccount.getFqn();

        if (this.prefix != null) {
            grantedAuthorityString = this.prefix + grantedAuthorityString;
        }

        if (this.convertToUpperCase) {
            grantedAuthorityString = grantedAuthorityString.toUpperCase(Locale.ENGLISH);
        }

        return new SimpleGrantedAuthority(grantedAuthorityString);
    }

}
