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

import com.sun.jna.platform.win32.Win32Exception;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

/**
 * A Waffle authentication provider for Spring-security.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationProvider implements AuthenticationProvider {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsAuthenticationProvider.class);

    /** The principal format. */
    private PrincipalFormat principalFormat = PrincipalFormat.FQN;

    /** The role format. */
    private PrincipalFormat roleFormat = PrincipalFormat.FQN;

    /** The allow guest login. */
    private boolean allowGuestLogin = true;

    /** The auth provider. */
    private IWindowsAuthProvider authProvider;

    /** The granted authority factory. */
    private GrantedAuthorityFactory grantedAuthorityFactory = WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY_FACTORY;

    /** The default granted authority. */
    private GrantedAuthority defaultGrantedAuthority = WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY;

    /**
     * Instantiates a new windows authentication provider.
     */
    public WindowsAuthenticationProvider() {
        WindowsAuthenticationProvider.LOGGER.debug("[waffle.spring.WindowsAuthenticationProvider] loaded");
    }

    @Override
    public Authentication authenticate(final Authentication authentication) {
        final UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        IWindowsIdentity windowsIdentity;
        try {
            windowsIdentity = this.authProvider.logonUser(auth.getName(), auth.getCredentials().toString());
        } catch (final Win32Exception e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        WindowsAuthenticationProvider.LOGGER.debug("logged in user: {} ({})", windowsIdentity.getFqn(),
                windowsIdentity.getSidString());

        if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
            WindowsAuthenticationProvider.LOGGER.warn("guest login disabled: {}", windowsIdentity.getFqn());
            throw new GuestLoginDisabledAuthenticationException(windowsIdentity.getFqn());
        }

        final WindowsPrincipal windowsPrincipal = new WindowsPrincipal(windowsIdentity, this.principalFormat,
                this.roleFormat);
        WindowsAuthenticationProvider.LOGGER.debug("roles: {}", windowsPrincipal.getRolesString());

        final WindowsAuthenticationToken token = new WindowsAuthenticationToken(windowsPrincipal,
                this.grantedAuthorityFactory, this.defaultGrantedAuthority);

        WindowsAuthenticationProvider.LOGGER.info("successfully logged in user: {}", windowsIdentity.getFqn());
        return token;
    }

    /**
     * Supports.
     *
     * @param authentication
     *            the authentication
     * @return true, if successful
     */
    @Override
    public boolean supports(final Class<? extends Object> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Gets the principal format.
     *
     * @return the principal format
     */
    public PrincipalFormat getPrincipalFormat() {
        return this.principalFormat;
    }

    /**
     * Sets the principal format enum.
     *
     * @param value
     *            the new principal format enum
     */
    public void setPrincipalFormatEnum(final PrincipalFormat value) {
        this.principalFormat = value;
    }

    /**
     * Sets the principal format.
     *
     * @param value
     *            the new principal format
     */
    public void setPrincipalFormat(final String value) {
        this.setPrincipalFormatEnum(PrincipalFormat.valueOf(value.toUpperCase(Locale.ENGLISH)));
    }

    /**
     * Gets the role format.
     *
     * @return the role format
     */
    public PrincipalFormat getRoleFormat() {
        return this.roleFormat;
    }

    /**
     * Sets the role format enum.
     *
     * @param value
     *            the new role format enum
     */
    public void setRoleFormatEnum(final PrincipalFormat value) {
        this.roleFormat = value;
    }

    /**
     * Sets the role format.
     *
     * @param value
     *            the new role format
     */
    public void setRoleFormat(final String value) {
        this.setRoleFormatEnum(PrincipalFormat.valueOf(value.toUpperCase(Locale.ENGLISH)));
    }

    /**
     * Checks if is allow guest login.
     *
     * @return true, if is allow guest login
     */
    public boolean isAllowGuestLogin() {
        return this.allowGuestLogin;
    }

    /**
     * Sets the allow guest login.
     *
     * @param value
     *            the new allow guest login
     */
    public void setAllowGuestLogin(final boolean value) {
        this.allowGuestLogin = value;
    }

    /**
     * Gets the auth provider.
     *
     * @return the auth provider
     */
    public IWindowsAuthProvider getAuthProvider() {
        return this.authProvider;
    }

    /**
     * Sets the auth provider.
     *
     * @param value
     *            the new auth provider
     */
    public void setAuthProvider(final IWindowsAuthProvider value) {
        this.authProvider = value;
    }

    /**
     * Gets the granted authority factory.
     *
     * @return the granted authority factory
     */
    public GrantedAuthorityFactory getGrantedAuthorityFactory() {
        return this.grantedAuthorityFactory;
    }

    /**
     * Sets the granted authority factory.
     *
     * @param value
     *            the new granted authority factory
     */
    public void setGrantedAuthorityFactory(final GrantedAuthorityFactory value) {
        this.grantedAuthorityFactory = value;
    }

    /**
     * Gets the default granted authority.
     *
     * @return the default granted authority
     */
    public GrantedAuthority getDefaultGrantedAuthority() {
        return this.defaultGrantedAuthority;
    }

    /**
     * Sets the default granted authority.
     *
     * @param value
     *            the new default granted authority
     */
    public void setDefaultGrantedAuthority(final GrantedAuthority value) {
        this.defaultGrantedAuthority = value;
    }
}
