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

    private static final Logger     _log                     = LoggerFactory
                                                                     .getLogger(WindowsAuthenticationProvider.class);
    private PrincipalFormat         _principalFormat         = PrincipalFormat.fqn;
    private PrincipalFormat         _roleFormat              = PrincipalFormat.fqn;
    private boolean                 _allowGuestLogin         = true;
    private IWindowsAuthProvider    _authProvider;
    private GrantedAuthorityFactory _grantedAuthorityFactory = WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY_FACTORY;
    private GrantedAuthority        _defaultGrantedAuthority = WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY;

    public WindowsAuthenticationProvider() {
        _log.debug("[waffle.spring.WindowsAuthenticationProvider] loaded");
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        IWindowsIdentity windowsIdentity = _authProvider.logonUser(auth.getName(), auth.getCredentials().toString());
        _log.debug("logged in user: {} ({})", windowsIdentity.getFqn(), windowsIdentity.getSidString());

        if (!_allowGuestLogin && windowsIdentity.isGuest()) {
            _log.warn("guest login disabled: {}", windowsIdentity.getFqn());
            throw new GuestLoginDisabledAuthenticationException(windowsIdentity.getFqn());
        }

        WindowsPrincipal windowsPrincipal = new WindowsPrincipal(windowsIdentity, _principalFormat, _roleFormat);
        _log.debug("roles: {}", windowsPrincipal.getRolesString());

        WindowsAuthenticationToken token = new WindowsAuthenticationToken(windowsPrincipal, _grantedAuthorityFactory,
                _defaultGrantedAuthority);

        _log.info("successfully logged in user: {}", windowsIdentity.getFqn());
        return token;
    }

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public PrincipalFormat getPrincipalFormat() {
        return _principalFormat;
    }

    public void setPrincipalFormat(PrincipalFormat principalFormat) {
        _principalFormat = principalFormat;
    }

    public PrincipalFormat getRoleFormat() {
        return _roleFormat;
    }

    public void setRoleFormat(PrincipalFormat principalFormat) {
        _roleFormat = principalFormat;
    }

    public boolean isAllowGuestLogin() {
        return _allowGuestLogin;
    }

    public void setAllowGuestLogin(boolean allowGuestLogin) {
        _allowGuestLogin = allowGuestLogin;
    }

    public IWindowsAuthProvider getAuthProvider() {
        return _authProvider;
    }

    public void setAuthProvider(IWindowsAuthProvider authProvider) {
        _authProvider = authProvider;
    }

    public GrantedAuthorityFactory getGrantedAuthorityFactory() {
        return _grantedAuthorityFactory;
    }

    public void setGrantedAuthorityFactory(GrantedAuthorityFactory grantedAuthorityFactory) {
        _grantedAuthorityFactory = grantedAuthorityFactory;
    }

    public GrantedAuthority getDefaultGrantedAuthority() {
        return _defaultGrantedAuthority;
    }

    public void setDefaultGrantedAuthority(GrantedAuthority defaultGrantedAuthority) {
        _defaultGrantedAuthority = defaultGrantedAuthority;
    }
}
