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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 *
 * <p>
 * Supports optional injection of spring security entities, allowing Waffle to act as an interface towards an identity
 * provider(the AD).
 * </p>
 *
 * <i>Below mentioned entities are verified to be set before invoked, inherited entities are not.</i>
 *
 * <ul>
 * <li>The <code>AuthenticationManager</code> allows for the service provider to authorize the principal.</li>
 *
 * <li>The <code>authenticationSuccessHandler</code> allows for the service provider to further populate the
 * {@link org.springframework.security.core.Authentication Authentication} object.</li>
 *
 * <li>The <code>AuthenticationFailureHandler</code> is called if the AuthenticationManager throws an
 * {@link org.springframework.security.core.AuthenticationException AuthenticationException}.</li>
 *
 * <li>The <code>AccessDeniedHandler</code> is called if the AuthenticationManager throws an
 * {@link org.springframework.security.access.AccessDeniedException AccessDeniedException}.</li>
 * </ul>
 * Example configuration:
 *
 * <pre>
 * {@code
 * <bean id="waffleNegotiateSecurityFilter"
 *      class="waffle.spring.DelegatingNegotiateSecurityFilter"
 *      scope="tenant">
 *      <property name="allowGuestLogin" value="false" />
 *      <property name="Provider" ref="waffleSecurityFilterProviderCollection" />
 *      <property name="authenticationManager" ref="authenticationManager" />
 *      <property name="authenticationSuccessHandler" ref="authenticationSuccessHandler" />
 *      <property name="authenticationFailureHandler" ref="authenticationFailureHandler" />
 *      <property name="accessDeniedHandler" ref="accessDeniedHandler" />
 *      <property name="defaultGrantedAuthority">
 *          <null />
 *      </property>
 * </bean>
 * }
 * </pre>
 */
public class DelegatingNegotiateSecurityFilter extends NegotiateSecurityFilter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiateSecurityFilter.class);

    /** The authentication manager. */
    private AuthenticationManager authenticationManager;

    /** The authentication success handler. */
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    /** The authentication failure handler. */
    private AuthenticationFailureHandler authenticationFailureHandler;

    /** The access denied handler. */
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * Instantiates a new delegating negotiate security filter.
     */
    public DelegatingNegotiateSecurityFilter() {
        super();
        DelegatingNegotiateSecurityFilter.LOGGER.debug("[waffle.spring.NegotiateSecurityFilter] loaded");
    }

    /**
     * Gets the access denied handler.
     *
     * @return the accessDeniedHandler
     */
    public AccessDeniedHandler getAccessDeniedHandler() {
        return this.accessDeniedHandler;
    }

    /**
     * Sets the access denied handler.
     *
     * @param value
     *            the accessDeniedHandler to set
     */
    public void setAccessDeniedHandler(final AccessDeniedHandler value) {
        this.accessDeniedHandler = value;
    }

    /**
     * Gets the authentication failure handler.
     *
     * @return the authenticationFailureHandler
     */
    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return this.authenticationFailureHandler;
    }

    /**
     * Sets the authentication failure handler.
     *
     * @param value
     *            the authenticationFailureHandler to set
     */
    public void setAuthenticationFailureHandler(final AuthenticationFailureHandler value) {
        this.authenticationFailureHandler = value;
    }

    @Override
    protected boolean setAuthentication(final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication) {
        try {
            if (this.authenticationManager != null) {
                DelegatingNegotiateSecurityFilter.LOGGER.debug("Delegating to custom authenticationmanager");
                final Authentication customAuthentication = this.authenticationManager.authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(customAuthentication);
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            if (this.authenticationSuccessHandler != null) {
                try {
                    this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                } catch (final IOException | ServletException e) {
                    DelegatingNegotiateSecurityFilter.LOGGER.warn("Error calling authenticationSuccessHandler: {}",
                            e.getMessage());
                    DelegatingNegotiateSecurityFilter.LOGGER.trace("", e);
                    return false;
                }
            }
        } catch (final AuthenticationException e) {
            DelegatingNegotiateSecurityFilter.LOGGER
                    .warn("Error authenticating user in custom authenticationmanager: {}", e.getMessage());
            this.sendAuthenticationFailed(request, response, e);
            return false;
        } catch (final AccessDeniedException e) {
            DelegatingNegotiateSecurityFilter.LOGGER.warn("Error authorizing user in custom authenticationmanager: {}",
                    e.getMessage());
            this.sendAccessDenied(request, response, e);
            return false;
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();

        if (this.getProvider() == null) {
            throw new ServletException("Missing NegotiateSecurityFilter.Provider");
        }
    }

    /**
     * Forward to authenticationFailureHandler.
     *
     * @param request
     *            the request
     * @param response
     *            HTTP Response
     * @param ae
     *            the ae
     */
    private void sendAuthenticationFailed(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException ae) {
        if (this.authenticationFailureHandler != null) {
            try {
                this.authenticationFailureHandler.onAuthenticationFailure(request, response, ae);
                return;
            } catch (final IOException e) {
                DelegatingNegotiateSecurityFilter.LOGGER.warn("IOException invoking authenticationFailureHandler: {}",
                        e.getMessage());
                DelegatingNegotiateSecurityFilter.LOGGER.trace("", e);
            } catch (final ServletException e) {
                DelegatingNegotiateSecurityFilter.LOGGER
                        .warn("ServletException invoking authenticationFailureHandler: {}", e.getMessage());
                DelegatingNegotiateSecurityFilter.LOGGER.trace("", e);
            }
        }
        super.sendUnauthorized(response, true);
    }

    /**
     * Forward to accessDeniedHandler.
     *
     * @param request
     *            the request
     * @param response
     *            HTTP Response
     * @param ae
     *            the ae
     */
    private void sendAccessDenied(final HttpServletRequest request, final HttpServletResponse response,
            final AccessDeniedException ae) {
        if (this.accessDeniedHandler != null) {
            try {
                this.accessDeniedHandler.handle(request, response, ae);
                return;
            } catch (final IOException e) {
                DelegatingNegotiateSecurityFilter.LOGGER.warn("IOException invoking accessDeniedHandler: {}",
                        e.getMessage());
                DelegatingNegotiateSecurityFilter.LOGGER.trace("", e);
            } catch (final ServletException e) {
                DelegatingNegotiateSecurityFilter.LOGGER.warn("ServletException invoking accessDeniedHandler: {}",
                        e.getMessage());
                DelegatingNegotiateSecurityFilter.LOGGER.trace("", e);
            }
        }
        // fallback
        this.sendUnauthorized(response, true);
    }

    /**
     * Gets the authentication success handler.
     *
     * @return the authenticationSuccessHandler
     */
    public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return this.authenticationSuccessHandler;
    }

    /**
     * Sets the authentication success handler.
     *
     * @param value
     *            the authenticationSuccessHandler to set
     */
    public void setAuthenticationSuccessHandler(final AuthenticationSuccessHandler value) {
        this.authenticationSuccessHandler = value;
    }

    /**
     * Gets the authentication manager.
     *
     * @return the authenticationManager
     */
    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    /**
     * Sets the authentication manager.
     *
     * @param value
     *            the authenticationManager to set
     */
    public void setAuthenticationManager(final AuthenticationManager value) {
        this.authenticationManager = value;
    }

}
