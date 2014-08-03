/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.spring;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;

import waffle.windows.auth.WindowsAccount;

/**
 * A {@link GrantedAuthorityFactory} that uses the {@link WindowsAccount}'s fqn as the basis of the
 * {@link GrantedAuthority} string, and (optionally) applies two transformations:
 * <ul>
 * <li>prepending a prefix, and</li>
 * <li>converting to uppercase</li>
 * </ul>
 */
public class FqnGrantedAuthorityFactory implements GrantedAuthorityFactory {

    private final String  prefix;
    private final boolean convertToUpperCase;

    public FqnGrantedAuthorityFactory(final String prefix, final boolean convertToUpperCase) {
        this.prefix = prefix;
        this.convertToUpperCase = convertToUpperCase;
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

        return new GrantedAuthorityImpl(grantedAuthorityString);
    }
}
