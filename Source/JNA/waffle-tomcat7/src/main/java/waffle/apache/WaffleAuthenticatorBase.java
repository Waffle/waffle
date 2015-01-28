/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
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
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.slf4j.Logger;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import static java.util.Arrays.asList;

/**
 * The Class WaffleAuthenticatorBase.
 *
 * @author dblock[at]dblock[dot]org
 */
abstract class WaffleAuthenticatorBase extends AuthenticatorBase {

    /** The Constant SUPPORTED_PROTOCOLS. */
    private static final Set<String> SUPPORTED_PROTOCOLS = new LinkedHashSet<String>(asList("Negotiate", "NTLM"));

    /** The info. */
    protected String                 info;
    
    /** The log. */
    protected Logger                 log;
    
    /** The principal format. */
    protected PrincipalFormat        principalFormat     = PrincipalFormat.FQN;
    
    /** The role format. */
    protected PrincipalFormat        roleFormat          = PrincipalFormat.FQN;
    
    /** The allow guest login. */
    protected boolean                allowGuestLogin     = true;
    
    /** The protocols. */
    protected Set<String>            protocols           = SUPPORTED_PROTOCOLS;

    /** The auth continueContextTimeout configuration */
    protected int                    continueContextsTimeout = 30;

    /** The auth. */
    protected IWindowsAuthProvider   auth                = null;

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

    /* (non-Javadoc)
     * @see org.apache.catalina.authenticator.AuthenticatorBase#getInfo()
     */
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
    public void setPrincipalFormat(String format) {
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
    public void setRoleFormat(String format) {
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
        this.protocols = new LinkedHashSet<String>();
        final String[] protocolNames = value.split(",");
        for (String protocolName : protocolNames) {
            protocolName = protocolName.trim();
            if (!protocolName.isEmpty()) {
                this.log.debug("init protocol: {}", protocolName);
                if (SUPPORTED_PROTOCOLS.contains(protocolName)) {
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
            for (String protocol : this.protocols) {
                response.addHeader("WWW-Authenticate", protocol);
            }
            response.setHeader("Connection", "close");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
        } catch (IOException e) {
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
        } catch (IOException e) {
            this.log.error(e.getMessage());
            this.log.trace("{}", e);
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.catalina.authenticator.AuthenticatorBase#getAuthMethod()
     */
    @Override
    protected String getAuthMethod() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.catalina.authenticator.AuthenticatorBase#doLogin(org.apache.catalina.connector.Request, java.lang.String, java.lang.String)
     */
    @Override
    protected Principal doLogin(final Request request, final String username, final String password)
            throws ServletException {
        this.log.debug("logging in: {}", username);
        IWindowsIdentity windowsIdentity;
        try {
            windowsIdentity = this.auth.logonUser(username, password);
        } catch (Exception e) {
            this.log.error(e.getMessage());
            this.log.trace("{}", e);
            return super.doLogin(request, username, password);
        }
        // disable guest login
        if (!this.allowGuestLogin && windowsIdentity.isGuest()) {
            this.log.warn("guest login disabled: {}", windowsIdentity.getFqn());
            return super.doLogin(request, username, password);
        }
        try {
            this.log.debug("successfully logged in {} ({})", username, windowsIdentity.getSidString());
            final GenericWindowsPrincipal windowsPrincipal = new GenericWindowsPrincipal(windowsIdentity,
                    this.principalFormat, this.roleFormat);
            this.log.debug("roles: {}", windowsPrincipal.getRolesString());
            return windowsPrincipal;
        } finally {
            windowsIdentity.dispose();
        }
    }

    /**
     * Hook to the start and to set up the dependencies.
     * @throws LifecycleException
     */
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        this.log.debug("Creating a windows authentication provider with continueContextTimeout property set to: {}", this.continueContextsTimeout);
        this.auth = new WindowsAuthProviderImpl(this.continueContextsTimeout);
        super.startInternal();
    }

}
