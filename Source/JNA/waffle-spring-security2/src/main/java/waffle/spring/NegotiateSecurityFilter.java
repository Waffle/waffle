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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import waffle.servlet.WindowsPrincipal;
import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

/**
 * A Spring Negotiate security filter.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilter extends GenericFilterBean {

    private static final Logger              LOGGER                  = LoggerFactory
                                                                             .getLogger(NegotiateSecurityFilter.class);
    private SecurityFilterProviderCollection provider;
    private PrincipalFormat                  principalFormat         = PrincipalFormat.fqn;
    private PrincipalFormat                  roleFormat              = PrincipalFormat.fqn;
    private boolean                          allowGuestLogin         = true;

    private GrantedAuthorityFactory          grantedAuthorityFactory = WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY_FACTORY;
    private GrantedAuthority                 defaultGrantedAuthority = WindowsAuthenticationToken.DEFAULT_GRANTED_AUTHORITY;

    public NegotiateSecurityFilter() {
        super();
        LOGGER.debug("[waffle.spring.NegotiateSecurityFilter] loaded");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        LOGGER.debug("{} {}, contentlength: {}", request.getMethod(), request.getRequestURI(),
                Integer.valueOf(request.getContentLength()));

        AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);

        // authenticate user
        if (!authorizationHeader.isNull()
                && this.provider.isSecurityPackageSupported(authorizationHeader.getSecurityPackage())) {

            // log the user in using the token
            IWindowsIdentity windowsIdentity = null;

            try {
                windowsIdentity = this.provider.doFilter(request, response);
                if (windowsIdentity == null) {
                    return;
                }
            } catch (IOException e) {
                LOGGER.warn("error logging in user: {}", e.getMessage());
                LOGGER.trace("{}", e);
                sendUnauthorized(response, true);
                return;
            }

            if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
                LOGGER.warn("guest login disabled: {}", windowsIdentity.getFqn());
                sendUnauthorized(response, true);
                return;
            }

            try {
                LOGGER.debug("logged in user: {} ({})", windowsIdentity.getFqn(), windowsIdentity.getSidString());

                WindowsPrincipal principal = new WindowsPrincipal(windowsIdentity, this.principalFormat,
                        this.roleFormat);

                LOGGER.debug("roles: {}", principal.getRolesString());

                Authentication authentication = new WindowsAuthenticationToken(principal, this.grantedAuthorityFactory,
                        this.defaultGrantedAuthority);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                LOGGER.info("successfully logged in user: {}", windowsIdentity.getFqn());

            } finally {
                windowsIdentity.dispose();
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();

        if (this.provider == null) {
            throw new ServletException("Missing NegotiateSecurityFilter.Provider");
        }
    }

    /**
     * Send a 401 Unauthorized along with protocol authentication headers.
     * 
     * @param response
     *            HTTP Response
     * @param close
     *            Close connection.
     */
    private void sendUnauthorized(HttpServletResponse response, boolean close) {
        try {
            this.provider.sendUnauthorized(response);
            if (close) {
                response.setHeader("Connection", "close");
            } else {
                response.setHeader("Connection", "keep-alive");
            }
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public SecurityFilterProviderCollection getProvider() {
        return this.provider;
    }

    public void setProvider(SecurityFilterProviderCollection value) {
        this.provider = value;
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
