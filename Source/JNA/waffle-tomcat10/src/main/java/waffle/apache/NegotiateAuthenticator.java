/*
 * MIT License
 *
 * Copyright (c) 2010-2022 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.apache;

import com.sun.jna.platform.win32.Win32Exception;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;
import org.slf4j.LoggerFactory;

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

    @Override
    public synchronized void startInternal() throws LifecycleException {
        this.log.info("[waffle.apache.NegotiateAuthenticator] started");
        super.startInternal();
    }

    @Override
    public synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.log.info("[waffle.apache.NegotiateAuthenticator] stopped");
    }

    @Override
    public boolean authenticate(final Request request, final HttpServletResponse response) {

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
                if (securityContext.isContinue()) {
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

                final GenericPrincipal genericPrincipal = this.createPrincipal(windowsIdentity);

                if (this.log.isDebugEnabled()) {
                    this.log.debug("roles: {}", String.join(", ", genericPrincipal.getRoles()));
                }

                principal = genericPrincipal;

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

    /**
     * XXX The 'doAuthenticate' is intended to replace 'authenticate' for needs like ours. In order to support old and
     * new at this time, we will continue to have both for time being.
     */
    @Override
    protected boolean doAuthenticate(final Request request, final HttpServletResponse response) throws IOException {
        return this.authenticate(request, response);
    }

}
