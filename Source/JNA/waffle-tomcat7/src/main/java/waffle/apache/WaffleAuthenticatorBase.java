/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;
import org.slf4j.Logger;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * The Class WaffleAuthenticatorBase.
 *
 * @author dblock[at]dblock[dot]org
 */
abstract class WaffleAuthenticatorBase extends AuthenticatorBase {

    /** The Constant SUPPORTED_PROTOCOLS. */
    private static final Set<String> SUPPORTED_PROTOCOLS = new LinkedHashSet<>(Arrays.asList("Negotiate", "NTLM"));

    /** The info. */
    protected String info;

    /** The log. */
    protected Logger log;

    /** The principal format. */
    protected PrincipalFormat principalFormat = PrincipalFormat.FQN;

    /** The role format. */
    protected PrincipalFormat roleFormat = PrincipalFormat.FQN;

    /** The allow guest login. */
    protected boolean allowGuestLogin = true;

    /** The protocols. */
    protected Set<String> protocols = WaffleAuthenticatorBase.SUPPORTED_PROTOCOLS;

    /** The auth continueContextsTimeout configuration. */
    protected int continueContextsTimeout = WindowsAuthProviderImpl.CONTINUE_CONTEXT_TIMEOUT;

    /** The auth. */
    protected IWindowsAuthProvider auth;

    /**
     * Gets the continue context time out configuration.
     *
     * @return the continue contexts timeout
     */
    public int getContinueContextsTimeout() {
        return this.continueContextsTimeout;
    }

    /**
     * Sets the continue context time out configuration.
     *
     * @param continueContextsTimeout
     *            the new continue contexts timeout
     */
    public void setContinueContextsTimeout(final int continueContextsTimeout) {
        this.continueContextsTimeout = continueContextsTimeout;
    }

    /**
     * Windows authentication provider.
     *
     * @return IWindowsAuthProvider.
     */
    public IWindowsAuthProvider getAuth() {
        return this.auth;
    }

    /**
     * Set Windows auth provider.
     *
     * @param provider
     *            Class implements IWindowsAuthProvider.
     */
    public void setAuth(final IWindowsAuthProvider provider) {
        this.auth = provider;
    }

    @Override
    public String getInfo() {
        return this.info;
    }

    /**
     * Set the principal format.
     *
     * @param format
     *            Principal format.
     */
    public void setPrincipalFormat(final String format) {
        this.principalFormat = PrincipalFormat.valueOf(format.toUpperCase(Locale.ENGLISH));
        this.log.debug("principal format: {}", this.principalFormat);
    }

    /**
     * Principal format.
     *
     * @return Principal format.
     */
    public PrincipalFormat getPrincipalFormat() {
        return this.principalFormat;
    }

    /**
     * Set the principal format.
     *
     * @param format
     *            Role format.
     */
    public void setRoleFormat(final String format) {
        this.roleFormat = PrincipalFormat.valueOf(format.toUpperCase(Locale.ENGLISH));
        this.log.debug("role format: {}", this.roleFormat);
    }

    /**
     * Principal format.
     *
     * @return Role format.
     */
    public PrincipalFormat getRoleFormat() {
        return this.roleFormat;
    }

    /**
     * True if Guest login permitted.
     *
     * @return True if Guest login permitted, false otherwise.
     */
    public boolean isAllowGuestLogin() {
        return this.allowGuestLogin;
    }

    /**
     * Set whether Guest login is permitted. Default is true, if the Guest account is enabled, an invalid
     * username/password results in a Guest login.
     *
     * @param value
     *            True or false.
     */
    public void setAllowGuestLogin(final boolean value) {
        this.allowGuestLogin = value;
    }

    /**
     * Set the authentication protocols. Default is "Negotiate, NTLM".
     *
     * @param value
     *            Authentication protocols
     */
    public void setProtocols(final String value) {
        this.protocols = new LinkedHashSet<>();
        final String[] protocolNames = value.split(",", -1);
        for (String protocolName : protocolNames) {
            protocolName = protocolName.trim();
            if (!protocolName.isEmpty()) {
                this.log.debug("init protocol: {}", protocolName);
                if (WaffleAuthenticatorBase.SUPPORTED_PROTOCOLS.contains(protocolName)) {
                    this.protocols.add(protocolName);
                } else {
                    this.log.error("unsupported protocol: {}", protocolName);
                    throw new RuntimeException("Unsupported protocol: " + protocolName);
                }
            }
        }
    }

    /**
     * Send a 401 Unauthorized along with protocol authentication headers.
     *
     * @param response
     *            HTTP Response
     */
    protected void sendUnauthorized(final HttpServletResponse response) {
        try {
            for (final String protocol : this.protocols) {
                response.addHeader("WWW-Authenticate", protocol);
            }
            response.setHeader("Connection", "close");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send an error code.
     *
     * @param response
     *            HTTP Response
     * @param code
     *            Error Code
     */
    protected void sendError(final HttpServletResponse response, final int code) {
        try {
            response.sendError(code);
        } catch (final IOException e) {
            this.log.error(e.getMessage());
            this.log.trace("", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getAuthMethod() {
        return null;
    }

    @Override
    protected Principal doLogin(final Request request, final String username, final String password)
            throws ServletException {
        this.log.debug("logging in: {}", username);
        IWindowsIdentity windowsIdentity;
        try {
            windowsIdentity = this.auth.logonUser(username, password);
        } catch (final Exception e) {
            this.log.error(e.getMessage());
            this.log.trace("", e);
            return super.doLogin(request, username, password);
        }
        // disable guest login
        if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
            this.log.warn("guest login disabled: {}", windowsIdentity.getFqn());
            return super.doLogin(request, username, password);
        }
        try {
            this.log.debug("successfully logged in {} ({})", username, windowsIdentity.getSidString());
            final GenericPrincipal genericPrincipal = this.createPrincipal(windowsIdentity);
            this.log.debug("roles: {}", String.join(", ", genericPrincipal.getRoles()));
            return genericPrincipal;
        } finally {
            windowsIdentity.dispose();
        }
    }

    /**
     * This method will create an instance of a IWindowsIdentity based GenericPrincipal. It is used for creating custom
     * implementation within subclasses.
     *
     * @param windowsIdentity
     *            the windows identity to initialize the principal
     * @return the Generic Principal
     */
    protected GenericPrincipal createPrincipal(final IWindowsIdentity windowsIdentity) {
        return new GenericWindowsPrincipal(windowsIdentity, this.principalFormat, this.roleFormat);
    }

    /**
     * Hook to the start and to set up the dependencies.
     *
     * @throws LifecycleException
     *             the lifecycle exception
     */
    @Override
    public synchronized void startInternal() throws LifecycleException {
        this.log.debug("Creating a windows authentication provider with continueContextsTimeout property set to: {}",
                this.continueContextsTimeout);
        this.auth = new WindowsAuthProviderImpl(this.continueContextsTimeout);
        super.startInternal();
    }

}
