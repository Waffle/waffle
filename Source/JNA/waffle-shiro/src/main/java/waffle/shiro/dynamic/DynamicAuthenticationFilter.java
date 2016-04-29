/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.shiro.dynamic;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.shiro.negotiate.NegotiateAuthenticationFilter;

/**
 * When combined with the {@link waffle.shiro.negotiate.NegotiateAuthenticationStrategy}, this filter can be used to
 * allow a client to choose which authentication filter is used at runtime. This filter assumes the shiro.ini is
 * configured with both the {@link waffle.shiro.negotiate.NegotiateAuthenticationRealm} and some User/Password Realm
 * like: {@link waffle.shiro.GroupMappingWaffleRealm}.
 * 
 * Requires use of {@link waffle.shiro.negotiate.NegotiateAuthenticationStrategy} when more than one realm is configured
 * in shiro.ini (which should be the case for multiple authentication type options).
 * 
 * To use {@link waffle.shiro.negotiate.NegotiateAuthenticationRealm}, the client must pass the parameter
 * {@link #PARAM_NAME_AUTHTYPE} with a value of {@link #PARAM_VAL_AUTHTYPE_NEGOTIATE}.
 * 
 * Example shiro.ini snippet below:
 * 
 * <pre>
 *  # =======================
 *  # Shiro INI configuration
 *  # =======================
 * 
 *  [main]
 * 
 *  # Setup custom AuthenticationRealm
 *  waffleRealmSSO = waffle.shiro.negotiate.NegotiateAuthenticationRealm
 *  waffleUserPass = waffle.shiro.GroupMappingWaffleRealm
 *  securityManager.realms = $waffleRealmSSO, $waffleUserPass
 * 
 * 
 *  # Use the configured native session manager:
 *  sessionManager = org.apache.shiro.web.session.mgt.DefaultWebSessionManager
 *  securityManager.sessionManager = $sessionManager
 * 
 *  # the following call is only necessary in a web-configured ShiroFilter (otherwise
 *  # a native session manager is already enabled):
 *  securityManager.sessionMode = native
 * 
 * 
 *  # cookie for single sign on
 *  cookie = org.apache.shiro.web.servlet.SimpleCookie
 *  cookie.name = SSOcookie
 *  cookie.path = /
 *  securityManager.sessionManager.sessionIdCookie = $cookie
 * 
 * 
 *  authcStrategy = waffle.shiro.negotiate.NegotiateAuthenticationStrategy
 *  securityManager.authenticator.authenticationStrategy = $authcStrategy
 * 
 *  # Waffle filter
 *  waffleFilter = waffle.shiro.dynamic.DynamicAuthenticationFilter
 * 
 *  #Configure filter chains and filter parameters
 *  authc.loginUrl = /login.jsp
 *  waffleFilter.loginUrl = /login.jsp
 *  logout.redirectUrl = login.jsp
 * 
 *  ...
 * 
 *  [urls]
 *  # The 'urls' section is used for url-based security
 *  /logout = logout
 *  /* = waffleFilter
 * 
 * </pre>
 * 
 * @author Dan Rollo Date: 2/21/13 Time: 9:08 PM
 */
public class DynamicAuthenticationFilter extends FormAuthenticationFilter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER                       = LoggerFactory
                                                                     .getLogger(DynamicAuthenticationFilter.class);

    /** The Constant PARAM_NAME_AUTHTYPE. */
    public static final String  PARAM_NAME_AUTHTYPE          = "authType";

    /** The Constant PARAM_VAL_AUTHTYPE_NEGOTIATE. */
    public static final String  PARAM_VAL_AUTHTYPE_NEGOTIATE = "j_negotiate";

    /**
     * Wrapper to make protected methods in different package callable from here.
     */
    private static final class WrapNegotiateAuthenticationFilter extends NegotiateAuthenticationFilter {

        /** The parent. */
        private final DynamicAuthenticationFilter parent;

        /**
         * Instantiates a new wrap negotiate authentication filter.
         *
         * @param newParent
         *            the new parent
         */
        WrapNegotiateAuthenticationFilter(final DynamicAuthenticationFilter newParent) {
            this.parent = newParent;
        }

        /*
         * (non-Javadoc)
         * @see waffle.shiro.negotiate.NegotiateAuthenticationFilter#onAccessDenied(javax.servlet.ServletRequest,
         * javax.servlet.ServletResponse)
         */
        @Override
        public boolean onAccessDenied(final ServletRequest request, final ServletResponse response) throws Exception {
            return super.onAccessDenied(request, response);
        }

        /*
         * (non-Javadoc)
         * @see
         * waffle.shiro.negotiate.NegotiateAuthenticationFilter#onLoginSuccess(org.apache.shiro.authc.AuthenticationToken
         * , org.apache.shiro.subject.Subject, javax.servlet.ServletRequest, javax.servlet.ServletResponse)
         */
        @SuppressWarnings("synthetic-access")
        @Override
        protected boolean onLoginSuccess(final AuthenticationToken token, final Subject subject,
                final ServletRequest request, final ServletResponse response) throws Exception {
            return this.parent.onLoginSuccess(token, subject, request, response);
        }
    }

    /** The filter negotiate. */
    private final WrapNegotiateAuthenticationFilter filterNegotiate = new WrapNegotiateAuthenticationFilter(this);

    /**
     * Wrapper to make protected methods in different package callable from here.
     */
    private static final class WrapFormAuthenticationFilter extends FormAuthenticationFilter {

        /** The parent. */
        private final DynamicAuthenticationFilter parent;

        /**
         * Instantiates a new wrap form authentication filter.
         *
         * @param newParent
         *            the new parent
         */
        WrapFormAuthenticationFilter(final DynamicAuthenticationFilter newParent) {
            this.parent = newParent;
        }

        /*
         * (non-Javadoc)
         * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onAccessDenied(javax.servlet.ServletRequest,
         * javax.servlet.ServletResponse)
         */
        @Override
        public boolean onAccessDenied(final ServletRequest request, final ServletResponse response) throws Exception {
            return super.onAccessDenied(request, response);
        }

        /*
         * (non-Javadoc)
         * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onLoginSuccess(org.apache.shiro.authc.
         * AuthenticationToken, org.apache.shiro.subject.Subject, javax.servlet.ServletRequest,
         * javax.servlet.ServletResponse)
         */
        @SuppressWarnings("synthetic-access")
        @Override
        protected boolean onLoginSuccess(final AuthenticationToken token, final Subject subject,
                final ServletRequest request, final ServletResponse response) throws Exception {
            return this.parent.onLoginSuccess(token, subject, request, response);
        }
    }

    /** The filter form authc. */
    private final WrapFormAuthenticationFilter filterFormAuthc = new WrapFormAuthenticationFilter(this);

    /**
     * Call
     * {@link org.apache.shiro.web.filter.AccessControlFilter#onAccessDenied(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}
     * for the user selected authentication type, which performs login logic.
     * 
     * {@inheritDoc}
     */
    @Override
    protected boolean executeLogin(final ServletRequest request, final ServletResponse response) throws Exception {
        if (this.isAuthTypeNegotiate(request)) {
            DynamicAuthenticationFilter.LOGGER.debug("using filterNegotiate");
            return this.filterNegotiate.onAccessDenied(request, response);
        }
        DynamicAuthenticationFilter.LOGGER.debug("using filterFormAuthc");
        return this.filterFormAuthc.onAccessDenied(request, response);
    }

    /**
     * Checks if is auth type negotiate.
     *
     * @param request
     *            the request
     * @return true, if is auth type negotiate
     */
    boolean isAuthTypeNegotiate(final ServletRequest request) {
        final String authType = request.getParameter(DynamicAuthenticationFilter.PARAM_NAME_AUTHTYPE);
        return authType != null && DynamicAuthenticationFilter.PARAM_VAL_AUTHTYPE_NEGOTIATE.equalsIgnoreCase(authType);
    }

}
