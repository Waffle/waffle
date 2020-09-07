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
package waffle.shiro.negotiate;

import javax.security.auth.Subject;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 * Information about a user authenticated via the HTTP Negotiate authentication mechanism.
 *
 * Derived from net.skorgenes.security.jsecurity.negotiate.NegotiateToken. see:
 * https://bitbucket.org/lothor/shiro-negotiate
 * /src/7b25efde130b/src/main/java/net/skorgenes/security/jsecurity/negotiate/NegotiateInfo.java?at=default
 *
 * @author Dan Rollo
 * @since 1.0.0
 */
public class NegotiateInfo implements AuthenticationInfo {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1537448549089922914L;

    /** The subject. */
    private final Subject subject;

    /** The realm name. */
    private final String realmName;

    /**
     * Creates a new info object.
     *
     * @param newSubject
     *            a subject containing the authenticated users {@link waffle.servlet.WindowsPrincipal}.
     * @param newRealmName
     *            a <code>String</code> containing the name of the authentication realm
     */
    public NegotiateInfo(final Subject newSubject, final String newRealmName) {
        this.subject = newSubject;
        this.realmName = newRealmName;
    }

    /**
     * Creates a new principal collection using the subject as the principal.
     *
     * @return a new principal collection using the subject as the principal
     */
    @Override
    public PrincipalCollection getPrincipals() {
        return new SimplePrincipalCollection(this.subject.getPrincipals(), this.realmName);
    }

    /**
     * Returns the subject.
     *
     * @return the subject
     */
    @Override
    public Object getCredentials() {
        return this.subject;
    }
}
