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
package waffle.servlet.spi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;

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
