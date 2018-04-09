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
package waffle.servlet.spi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;

/**
 * A Basic authentication security filter provider. https://tools.ietf.org/html/rfc2617
 *
 * @author dblock[at]dblock[dot]org
 */
public class BasicSecurityFilterProvider implements SecurityFilterProvider {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicSecurityFilterProvider.class);

    /** The realm. */
    private String realm = "BasicSecurityFilterProvider";

    /** The auth. */
    private final IWindowsAuthProvider auth;

    /**
     * Instantiates a new basic security filter provider.
     *
     * @param newAuthProvider
     *            the new auth provider
     */
    public BasicSecurityFilterProvider(final IWindowsAuthProvider newAuthProvider) {
        this.auth = newAuthProvider;
    }

    @Override
    public IWindowsIdentity doFilter(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {

        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        final String usernamePassword = new String(authorizationHeader.getTokenBytes(), StandardCharsets.UTF_8);
        final String[] usernamePasswordArray = usernamePassword.split(":", 2);
        if (usernamePasswordArray.length != 2) {
            throw new RuntimeException("Invalid username:password in Authorization header.");
        }
        BasicSecurityFilterProvider.LOGGER.debug("logging in user: {}", usernamePasswordArray[0]);
        return this.auth.logonUser(usernamePasswordArray[0], usernamePasswordArray[1]);
    }

    @Override
    public boolean isPrincipalException(final HttpServletRequest request) {
        return false;
    }

    @Override
    public boolean isSecurityPackageSupported(final String securityPackage) {
        return "Basic".equalsIgnoreCase(securityPackage);
    }

    @Override
    public void sendUnauthorized(final HttpServletResponse response) {
        response.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
    }

    /**
     * Protection space.
     *
     * @return Name of the protection space.
     */
    public String getRealm() {
        return this.realm;
    }

    /**
     * Set the protection space.
     *
     * @param value
     *            Protection space name.
     */
    public void setRealm(final String value) {
        this.realm = value;
    }

    /**
     * Init configuration parameters.
     *
     * @param parameterName
     *            the parameter name
     * @param parameterValue
     *            the parameter value
     */
    @Override
    public void initParameter(final String parameterName, final String parameterValue) {
        if ("realm".equals(parameterName)) {
            this.setRealm(parameterValue);
        } else {
            throw new InvalidParameterException(parameterName);
        }
    }
}
