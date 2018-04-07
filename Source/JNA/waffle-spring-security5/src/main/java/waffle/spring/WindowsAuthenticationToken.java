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

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.WindowsAccount;

/**
 * A Windows authentication token.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationToken implements Authentication {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * The {@link GrantedAuthorityFactory} that is used by default if a custom one is not specified. This default
     * {@link GrantedAuthorityFactory} is a {@link FqnGrantedAuthorityFactory} with prefix {@code "ROLE_"} and will
     * convert the fqn to uppercase
     */
    public static final GrantedAuthorityFactory DEFAULT_GRANTED_AUTHORITY_FACTORY = new FqnGrantedAuthorityFactory(
            "ROLE_", true);

    /**
     * The {@link GrantedAuthority} that will be added to every WindowsAuthenticationToken, unless another (or null) is
     * specified.
     */
    public static final GrantedAuthority DEFAULT_GRANTED_AUTHORITY = new SimpleGrantedAuthority("ROLE_USER");

    /** The principal. */
    private final WindowsPrincipal principal;

    /** The authorities. */
    private final Collection<GrantedAuthority> authorities;

    /**
     * Convenience constructor that calls
     * {@link #WindowsAuthenticationToken(WindowsPrincipal, GrantedAuthorityFactory, GrantedAuthority)} with:
     * <ul>
     * <li>the given identity,</li>
     * <li>the {@link #DEFAULT_GRANTED_AUTHORITY_FACTORY}</li>
     * <li>the {@link #DEFAULT_GRANTED_AUTHORITY}</li>
     * </ul>
     * .
     *
     * @param identity
     *            the identity
     */
    public WindowsAuthenticationToken(final WindowsPrincipal identity) {
        this(identity, WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY_FACTORY,
                WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY);
    }

    /**
     * Instantiates a new windows authentication token.
     *
     * @param identity
     *            The {@link WindowsPrincipal} for which this token exists.
     * @param grantedAuthorityFactory
     *            used to construct {@link GrantedAuthority}s for each of the groups to which the
     *            {@link WindowsPrincipal} belongs
     * @param defaultGrantedAuthority
     *            if not null, this {@link GrantedAuthority} will always be added to the granted authorities list
     */
    public WindowsAuthenticationToken(final WindowsPrincipal identity,
            final GrantedAuthorityFactory grantedAuthorityFactory, final GrantedAuthority defaultGrantedAuthority) {

        this.principal = identity;
        this.authorities = new ArrayList<>();
        if (defaultGrantedAuthority != null) {
            this.authorities.add(defaultGrantedAuthority);
        }
        for (final WindowsAccount group : this.principal.getGroups().values()) {
            this.authorities.add(grantedAuthorityFactory.createGrantedAuthority(group));
        }
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.principal != null;
    }

    @Override
    public void setAuthenticated(final boolean authenticated) {
        throw new IllegalArgumentException();
    }

    @Override
    public String getName() {
        return this.principal.getName();
    }

}
