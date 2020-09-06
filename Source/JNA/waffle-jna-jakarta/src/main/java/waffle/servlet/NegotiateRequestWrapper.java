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
package waffle.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.security.Principal;

/**
 * Negotiate Request wrapper.
 *
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateRequestWrapper extends HttpServletRequestWrapper {

    /** The principal. */
    private final WindowsPrincipal principal;

    /**
     * Instantiates a new negotiate request wrapper.
     *
     * @param newRequest
     *            the new request
     * @param newPrincipal
     *            the new principal
     */
    public NegotiateRequestWrapper(final HttpServletRequest newRequest, final WindowsPrincipal newPrincipal) {
        super(newRequest);
        this.principal = newPrincipal;
    }

    /**
     * User principal.
     *
     * @return the user principal
     */
    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }

    /**
     * Authentication type.
     *
     * @return the auth type
     */
    @Override
    public String getAuthType() {
        return "NEGOTIATE";
    }

    /**
     * Remote username.
     *
     * @return the remote user
     */
    @Override
    public String getRemoteUser() {
        return this.principal.getName();
    }

    /**
     * Returns true if the user is in a given role.
     *
     * @param role
     *            the role
     * @return true, if is user in role
     */
    @Override
    public boolean isUserInRole(final String role) {
        return this.principal.hasRole(role);
    }
}
