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
package waffle.apache;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.deploy.LoginConfig;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;

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
    public boolean authenticate(Request request, HttpServletResponse response, LoginConfig loginConfig) {

        // realm: fail if no realm is configured
        if (this.context == null || this.context.getRealm() == null) {
            this.log.warn("missing context/realm");
            sendError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return false;
        }

        this.log.debug("{} {}, contentlength: {}", request.getMethod(), request.getRequestURI(),
                Integer.valueOf(request.getContentLength()));

        boolean negotiateCheck = request.getParameter("j_negotiate_check") != null;
        this.log.debug("negotiateCheck: {}", Boolean.valueOf(negotiateCheck));
        boolean securityCheck = request.getParameter("j_security_check") != null;
        this.log.debug("securityCheck: {}", Boolean.valueOf(securityCheck));

        Principal principal = request.getUserPrincipal();

        AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
        this.log.debug("authorization: {}, ntlm post: {}", authorizationHeader, Boolean.valueOf(ntlmPost));

        if (principal != null && !ntlmPost) {
            this.log.debug("previously authenticated user: {}", principal.getName());
            return true;
        } else if (negotiateCheck) {
            if (!authorizationHeader.isNull()) {
                return negotiate(request, response, authorizationHeader);
            }
            this.log.debug("authorization required");
            sendUnauthorized(response);
            return false;
        } else if (securityCheck) {
            boolean postResult = post(request, response);
            if (postResult) {
                redirectTo(request, response, request.getServletPath());
            } else {
                redirectTo(request, response, loginConfig.getErrorPage());
            }
            return postResult;
        } else {
            redirectTo(request, response, loginConfig.getLoginPage());
            return false;
        }
    }

    private boolean negotiate(Request request, HttpServletResponse response, AuthorizationHeader authorizationHeader) {

        String securityPackage = authorizationHeader.getSecurityPackage();
        // maintain a connection-based session for NTLM tokens
        String connectionId = NtlmServletRequest.getConnectionId(request);

        this.log.debug("security package: {}, connection id: {}", securityPackage, connectionId);

        boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();

        if (ntlmPost) {
            // type 1 NTLM authentication message received
            this.auth.resetSecurityToken(connectionId);
        }

        // log the user in using the token
        IWindowsSecurityContext securityContext = null;

        try {
            byte[] tokenBuffer = authorizationHeader.getTokenBytes();
            this.log.debug("token buffer: {} byte(s)", Integer.valueOf(tokenBuffer.length));
            securityContext = this.auth.acceptSecurityToken(connectionId, tokenBuffer, securityPackage);
            this.log.debug("continue required: {}", Boolean.valueOf(securityContext.isContinue()));

            byte[] continueTokenBytes = securityContext.getToken();
            if (continueTokenBytes != null && continueTokenBytes.length > 0) {
                String continueToken = BaseEncoding.base64().encode(continueTokenBytes);
                this.log.debug("continue token: {}", continueToken);
                response.addHeader("WWW-Authenticate", securityPackage + " " + continueToken);
            }

            if (securityContext.isContinue() || ntlmPost) {
                response.setHeader("Connection", "keep-alive");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                response.flushBuffer();
                return false;
            }

        } catch (IOException e) {
            this.log.warn("error logging in user: {}", e.getMessage());
            this.log.trace("{}", e);
            sendUnauthorized(response);
            return false;
        }

        // create and register the user principal with the session
        IWindowsIdentity windowsIdentity = securityContext.getIdentity();

        // disable guest login
        if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
            this.log.warn("guest login disabled: {}", windowsIdentity.getFqn());
            sendUnauthorized(response);
            return false;
        }

        try {

            this.log.debug("logged in user: {} ({})", windowsIdentity.getFqn(), windowsIdentity.getSidString());

            GenericWindowsPrincipal windowsPrincipal = new GenericWindowsPrincipal(windowsIdentity,
                    this.principalFormat, this.roleFormat);

            this.log.debug("roles: {}", windowsPrincipal.getRolesString());

            // create a session associated with this request if there's none
            HttpSession session = request.getSession(true);
            this.log.debug("session id: {}", session.getId());

            register(request, response, windowsPrincipal, securityPackage, windowsPrincipal.getName(), null);
            this.log.info("successfully logged in user: {}", windowsPrincipal.getName());

        } finally {
            windowsIdentity.dispose();
        }

        return true;
    }

    private boolean post(Request request, HttpServletResponse response) {

        String username = request.getParameter("j_username");
        String password = request.getParameter("j_password");

        this.log.debug("logging in: {}", username);

        IWindowsIdentity windowsIdentity = null;
        try {
            windowsIdentity = this.auth.logonUser(username, password);
        } catch (Exception e) {
            this.log.error(e.getMessage());
            this.log.trace("{}", e);
            return false;
        }

        // disable guest login
        if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
            this.log.warn("guest login disabled: {}", windowsIdentity.getFqn());
            return false;
        }

        try {
            this.log.debug("successfully logged in {} ({})", username, windowsIdentity.getSidString());

            GenericWindowsPrincipal windowsPrincipal = new GenericWindowsPrincipal(windowsIdentity,
                    this.principalFormat, this.roleFormat);

            this.log.debug("roles: {}", windowsPrincipal.getRolesString());

            // create a session associated with this request if there's none
            HttpSession session = request.getSession(true);
            this.log.debug("session id: {}", session.getId());

            register(request, response, windowsPrincipal, "FORM", windowsPrincipal.getName(), null);
            this.log.info("successfully logged in user: {}", windowsPrincipal.getName());
        } finally {
            windowsIdentity.dispose();
        }

        return true;
    }

    private void redirectTo(Request request, HttpServletResponse response, String url) {
        try {
            this.log.debug("redirecting to: {}", url);
            ServletContext servletContext = this.context.getServletContext();
            RequestDispatcher disp = servletContext.getRequestDispatcher(url);
            disp.forward(request.getRequest(), response);
        } catch (IOException e) {
            this.log.error(e.getMessage());
            this.log.trace("{}", e);
            throw new RuntimeException(e);
        } catch (ServletException e) {
            this.log.error(e.getMessage());
            this.log.trace("{}", e);
            throw new RuntimeException(e);
        }
    }
}
