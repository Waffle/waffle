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
package waffle.apache;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.deploy.LoginConfig;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;
import com.sun.jna.platform.win32.Win32Exception;

import waffle.util.AuthorizationHeader;
import waffle.util.NtlmServletRequest;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * An Apache Negotiate (NTLM, Kerberos) Authenticator.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateAuthenticator extends WaffleAuthenticatorBase {

    /**
     * Instantiates a new negotiate authenticator.
     */
    public NegotiateAuthenticator() {
        super();
        this.log = LoggerFactory.getLogger(NegotiateAuthenticator.class);
        this.info = "waffle.apache.NegotiateAuthenticator/1.0";
        this.log.debug("[waffle.apache.NegotiateAuthenticator] loaded");
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.authenticator.AuthenticatorBase#startInternal()
     */
    @Override
    public synchronized void startInternal() throws LifecycleException {
        this.log.info("[waffle.apache.NegotiateAuthenticator] started");
        super.startInternal();
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.authenticator.AuthenticatorBase#stopInternal()
     */
    @Override
    public synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.log.info("[waffle.apache.NegotiateAuthenticator] stopped");
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.authenticator.AuthenticatorBase#authenticate(org.apache.catalina.connector.Request,
     * javax.servlet.http.HttpServletResponse, org.apache.catalina.deploy.LoginConfig)
     */
    @Override
    public boolean authenticate(final Request request, final HttpServletResponse response, final LoginConfig loginConfig) {

        Principal principal = request.getUserPrincipal();
        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        final boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();

        this.log.debug("{} {}, contentlength: {}", request.getMethod(), request.getRequestURI(),
                Integer.valueOf(request.getContentLength()));
        this.log.debug("authorization: {}, ntlm post: {}", authorizationHeader, Boolean.valueOf(ntlmPost));

        if (principal != null && !ntlmPost) {
            // user already authenticated
            this.log.debug("previously authenticated user: {}", principal.getName());
            return true;
        }

        // authenticate user
        if (!authorizationHeader.isNull()) {

            final String securityPackage = authorizationHeader.getSecurityPackage();
            // maintain a connection-based session for NTLM tokens
            final String connectionId = NtlmServletRequest.getConnectionId(request);

            this.log.debug("security package: {}, connection id: {}", securityPackage, connectionId);

            if (ntlmPost) {
                // type 1 NTLM authentication message received
                this.auth.resetSecurityToken(connectionId);
            }

            // log the user in using the token
            IWindowsSecurityContext securityContext;

            try {
                final byte[] tokenBuffer = authorizationHeader.getTokenBytes();
                this.log.debug("token buffer: {} byte(s)", Integer.valueOf(tokenBuffer.length));
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
                    final String continueToken = BaseEncoding.base64().encode(continueTokenBytes);
                    this.log.debug("continue token: {}", continueToken);
                    response.addHeader("WWW-Authenticate", securityPackage + " " + continueToken);
                }

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

            // realm: fail if no realm is configured
            if (this.context == null || this.context.getRealm() == null) {
                this.log.warn("missing context/realm");
                this.sendError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
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

                final GenericWindowsPrincipal windowsPrincipal = new GenericWindowsPrincipal(windowsIdentity,
                        this.principalFormat, this.roleFormat);

                this.log.debug("roles: {}", windowsPrincipal.getRolesString());

                principal = windowsPrincipal;

                // create a session associated with this request if there's none
                final HttpSession session = request.getSession(true);
                this.log.debug("session id: {}", session == null ? "null" : session.getId());

                // register the authenticated principal
                this.register(request, response, principal, securityPackage, principal.getName(), null);
                this.log.info("successfully logged in user: {}", principal.getName());

            } finally {
                windowsIdentity.dispose();
                securityContext.dispose();
            }

            return true;
        }

        this.log.debug("authorization required");
        this.sendUnauthorized(response);
        return false;
    }
}
