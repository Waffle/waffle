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
package waffle.servlet.spi;

import java.io.IOException;
import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;

/**
 * A Basic authentication security filter provider. http://tools.ietf.org/html/rfc2617
 * 
 * @author dblock[at]dblock[dot]org
 */
public class BasicSecurityFilterProvider implements SecurityFilterProvider {

    private static final Logger  LOGGER = LoggerFactory.getLogger(BasicSecurityFilterProvider.class);
    private String               realm  = "BasicSecurityFilterProvider";
    private IWindowsAuthProvider auth;

    public BasicSecurityFilterProvider(IWindowsAuthProvider auth) {
        this.auth = auth;
    }

    @Override
    public IWindowsIdentity doFilter(HttpServletRequest request, HttpServletResponse response) throws IOException {

        AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        String usernamePassword = new String(authorizationHeader.getTokenBytes());
        String[] usernamePasswordArray = usernamePassword.split(":", 2);
        if (usernamePasswordArray.length != 2) {
            throw new RuntimeException("Invalid username:password in Authorization header.");
        }
        LOGGER.debug("logging in user: {}", usernamePasswordArray[0]);
        return this.auth.logonUser(usernamePasswordArray[0], usernamePasswordArray[1]);
    }

    @Override
    public boolean isPrincipalException(HttpServletRequest request) {
        return false;
    }

    @Override
    public boolean isSecurityPackageSupported(String securityPackage) {
        return securityPackage.equalsIgnoreCase("Basic");
    }

    @Override
    public void sendUnauthorized(HttpServletResponse response) {
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
    public void setRealm(String value) {
        this.realm = value;
    }

    /**
     * Init configuration parameters.
     */
    @Override
    public void initParameter(String parameterName, String parameterValue) {
        if (parameterName.equals("realm")) {
            setRealm(parameterValue);
        } else {
            throw new InvalidParameterException(parameterName);
        }
    }
}
