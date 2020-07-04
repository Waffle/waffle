/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.apache;

import com.sun.jna.platform.win32.Win32Exception;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.realm.GenericPrincipal;
import org.slf4j.LoggerFactory;

import waffle.util.AuthorizationHeader;
import waffle.util.NtlmServletRequest;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * Mixed Negotiate + Form Authenticator.
 *
 * @author dblock[at]dblock[dot]org
 */
public class MixedAuthenticator extends WaffleAuthenticatorBase {

    /**
     * Instantiates a new mixed authenticator.
     */
    public MixedAuthenticator() {
        super();
        this.log = LoggerFactory.getLogger(MixedAuthenticator.class);
        this.info = "waffle.apache.MixedAuthenticator/1.0";
        this.log.debug("[waffle.apache.MixedAuthenticator] loaded");
    }

    @Override
    public synchronized void startInternal() throws LifecycleException {
        this.log.info("[waffle.apache.MixedAuthenticator] started");
        super.startInternal();
    }

    @Override
    public synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.log.info("[waffle.apache.MixedAuthenticator] stopped");
    }

    @Override
    public boolean authenticate(final Request request, final HttpServletResponse response,
            final LoginConfig loginConfig) {

        // realm: fail if no realm is configured
        if (this.context == null || this.context.getRealm() == null) {
            this.log.warn("missing context/realm");
            this.sendError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return false;
        }

        this.log.debug("{} {}, contentlength: {}", request.getMethod(), request.getRequestURI(),
                Integer.valueOf(request.getContentLength()));

        final boolean negotiateCheck = request.getParameter("j_negotiate_check") != null;
        this.log.debug("negotiateCheck: {}", Boolean.valueOf(negotiateCheck));
        final boolean securityCheck = request.getParameter("j_security_check") != null;
        this.log.debug("securityCheck: {}", Boolean.valueOf(securityCheck));

        final Principal principal = request.getUserPrincipal();

        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        final boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
        this.log.debug("authorization: {}, ntlm post: {}", authorizationHeader, Boolean.valueOf(ntlmPost));

        if (principal != null && !ntlmPost) {
            this.log.debug("previously authenticated user: {}", principal.getName());
            return true;
        } else if (negotiateCheck) {
            if (!authorizationHeader.isNull()) {
                boolean negotiateResult = this.negotiate(request, response, authorizationHeader);
                if (!negotiateResult) {
                    this.redirectTo(request, response, loginConfig.getErrorPage());
                }
                return negotiateResult;
            }
            this.log.debug("authorization required");
            this.sendUnauthorized(response);
            return false;
        } else if (securityCheck) {
            final boolean postResult = this.post(request, response);
            if (!postResult) {
                this.redirectTo(request, response, loginConfig.getErrorPage());
            }
            return postResult;
        } else {
            this.redirectTo(request, response, loginConfig.getLoginPage());
            return false;
        }
    }

    /**
     * Negotiate.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @param authorizationHeader
     *            the authorization header
     * @return true, if successful
     */
    private boolean negotiate(final Request request, final HttpServletResponse response,
            final AuthorizationHeader authorizationHeader) {

        final String securityPackage = authorizationHeader.getSecurityPackage();
        // maintain a connection-based session for NTLM tokens
        final String connectionId = NtlmServletRequest.getConnectionId(request);

        this.log.debug("security package: {}, connection id: {}", securityPackage, connectionId);

        final boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();

        if (ntlmPost) {
            // type 1 NTLM authentication message received
            this.auth.resetSecurityToken(connectionId);
        }

        final byte[] tokenBuffer = authorizationHeader.getTokenBytes();
        this.log.debug("token buffer: {} byte(s)", Integer.valueOf(tokenBuffer.length));

        // log the user in using the token
        IWindowsSecurityContext securityContext;
        try {
            securityContext = this.auth.acceptSecurityToken(connectionId, tokenBuffer, securityPackage);
        } catch (final Win32Exception e) {
            this.log.warn("error logging in user: {}", e.getMessage());
            this.log.trace("", e);
            this.sendUnauthorized(response);
            return false;
        }
        this.log.debug("continue required: {}", Boolean.valueOf(securityContext.isContinue()));

        final byte[] continueTokenBytes = securityContext.getToken();
        if (continueTokenBytes != null && continueTokenBytes.length > 0) {
            final String continueToken = Base64.getEncoder().encodeToString(continueTokenBytes);
            this.log.debug("continue token: {}", continueToken);
            response.addHeader("WWW-Authenticate", securityPackage + " " + continueToken);
        }

        try {
            if (securityContext.isContinue() || ntlmPost) {
                response.setHeader("Connection", "keep-alive");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                response.flushBuffer();
                return false;
            }
        } catch (final IOException e) {
            this.log.warn("error logging in user: {}", e.getMessage());
            this.log.trace("", e);
            this.sendUnauthorized(response);
            return false;
        }

        // create and register the user principal with the session
        final IWindowsIdentity windowsIdentity = securityContext.getIdentity();

        // disable guest login
        if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
            this.log.warn("guest login disabled: {}", windowsIdentity.getFqn());
            this.sendUnauthorized(response);
            return false;
        }

        try {

            this.log.debug("logged in user: {} ({})", windowsIdentity.getFqn(), windowsIdentity.getSidString());

            final GenericPrincipal genericPrincipal = this.createPrincipal(windowsIdentity);

            this.log.debug("roles: {}", String.join(", ", genericPrincipal.getRoles()));

            // create a session associated with this request if there's none
            final HttpSession session = request.getSession(true);
            this.log.debug("session id: {}", session == null ? "null" : session.getId());

            this.register(request, response, genericPrincipal, securityPackage, genericPrincipal.getName(), null);
            this.log.info("successfully logged in user: {}", genericPrincipal.getName());

        } finally {
            windowsIdentity.dispose();
        }

        return true;
    }

    /**
     * Post.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @return true, if successful
     */
    private boolean post(final Request request, final HttpServletResponse response) {

        final String username = request.getParameter("j_username");
        final String password = request.getParameter("j_password");

        this.log.debug("logging in: {}", username);

        IWindowsIdentity windowsIdentity;
        try {
            windowsIdentity = this.auth.logonUser(username, password);
        } catch (final Exception e) {
            this.log.error(e.getMessage());
            this.log.trace("", e);
            return false;
        }

        // disable guest login
        if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
            this.log.warn("guest login disabled: {}", windowsIdentity.getFqn());
            return false;
        }

        try {
            this.log.debug("successfully logged in {} ({})", username, windowsIdentity.getSidString());

            final GenericPrincipal genericPrincipal = this.createPrincipal(windowsIdentity);

            this.log.debug("roles: {}", String.join(", ", genericPrincipal.getRoles()));
            // create a session associated with this request if there's none
            final HttpSession session = request.getSession(true);
            this.log.debug("session id: {}", session == null ? "null" : session.getId());

            this.register(request, response, genericPrincipal, "FORM", genericPrincipal.getName(), null);
            this.log.info("successfully logged in user: {}", genericPrincipal.getName());
        } finally {
            windowsIdentity.dispose();
        }

        return true;
    }

    /**
     * Redirect to.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @param url
     *            the url
     */
    private void redirectTo(final Request request, final HttpServletResponse response, final String url) {
        try {
            this.log.debug("redirecting to: {}", url);
            final ServletContext servletContext = this.context.getServletContext();
            final RequestDispatcher disp = servletContext.getRequestDispatcher(url);
            disp.forward(request.getRequest(), response);
        } catch (final IOException | ServletException e) {
            this.log.error(e.getMessage());
            this.log.trace("", e);
            throw new RuntimeException(e);
        }
    }
}
