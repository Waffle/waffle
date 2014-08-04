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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
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

    private static final Logger     LOGGER                  = LoggerFactory
                                                                    .getLogger(WindowsAuthenticationProvider.class);
    private PrincipalFormat         principalFormat         = PrincipalFormat.fqn;
    private PrincipalFormat         roleFormat              = PrincipalFormat.fqn;
    private boolean                 allowGuestLogin         = true;
    private IWindowsAuthProvider    authProvider;
    private GrantedAuthorityFactory grantedAuthorityFactory = WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY_FACTORY;
    private GrantedAuthority        defaultGrantedAuthority = WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY;

    public WindowsAuthenticationProvider() {
        LOGGER.debug("[waffle.spring.WindowsAuthenticationProvider] loaded");
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        IWindowsIdentity windowsIdentity = this.authProvider
                .logonUser(auth.getName(), auth.getCredentials().toString());
        LOGGER.debug("logged in user: {} ({})", windowsIdentity.getFqn(), windowsIdentity.getSidString());

        if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
            LOGGER.warn("guest login disabled: {}", windowsIdentity.getFqn());
            throw new GuestLoginDisabledAuthenticationException(windowsIdentity.getFqn());
        }

        WindowsPrincipal windowsPrincipal = new WindowsPrincipal(windowsIdentity, this.principalFormat, this.roleFormat);
        LOGGER.debug("roles: {}", windowsPrincipal.getRolesString());

        WindowsAuthenticationToken token = new WindowsAuthenticationToken(windowsPrincipal,
                this.grantedAuthorityFactory, this.defaultGrantedAuthority);

        LOGGER.info("successfully logged in user: {}", windowsIdentity.getFqn());
        return token;
    }

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public PrincipalFormat getPrincipalFormat() {
        return this.principalFormat;
    }

    public void setPrincipalFormat(PrincipalFormat value) {
        this.principalFormat = value;
    }

    public PrincipalFormat getRoleFormat() {
        return this.roleFormat;
    }

    public void setRoleFormat(PrincipalFormat value) {
        this.roleFormat = value;
    }

    public boolean isAllowGuestLogin() {
        return this.allowGuestLogin;
    }

    public void setAllowGuestLogin(boolean value) {
        this.allowGuestLogin = value;
    }

    public IWindowsAuthProvider getAuthProvider() {
        return this.authProvider;
    }

    public void setAuthProvider(IWindowsAuthProvider value) {
        this.authProvider = value;
    }

    public GrantedAuthorityFactory getGrantedAuthorityFactory() {
        return this.grantedAuthorityFactory;
    }

    public void setGrantedAuthorityFactory(GrantedAuthorityFactory value) {
        this.grantedAuthorityFactory = value;
    }

    public GrantedAuthority getDefaultGrantedAuthority() {
        return this.defaultGrantedAuthority;
    }

    public void setDefaultGrantedAuthority(GrantedAuthority value) {
        this.defaultGrantedAuthority = value;
    }
}
