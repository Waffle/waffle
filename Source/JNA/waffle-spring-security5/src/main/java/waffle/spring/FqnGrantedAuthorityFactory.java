/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.spring;

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
            grantedAuthorityString = grantedAuthorityString.toUpperCase();
        }

        return new SimpleGrantedAuthority(grantedAuthorityString);
    }

}
