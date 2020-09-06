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

import java.security.Principal;

/**
 * Derived from net.skorgenes.security.jsecurity.negotiate.NegotiateToken. see:
 *
 * https://bitbucket.org/lothor/shiro-negotiate
 *
 * /src/7b25efde130b9cbcacf579b3f926c532d919aa23/src/main/java/net/skorgenes/
 *
 * security/jsecurity/negotiate/NegotiateAuthenticationFilter.java?at=default
 *
 * @author Dan Rollo
 */
import javax.security.auth.Subject;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

/**
 * An authentication token wrapping a Waffle Negotiate token.
 *
 * @author Dan Rollo
 * @since 1.0.0
 */
public class NegotiateToken implements HostAuthenticationToken, RememberMeAuthenticationToken {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1345343228636916781L;

    /** The in. */
    private final byte[] in;

    /** The out. */
    private byte[] out;

    /** The subject. */
    private Subject subject;

    /** The principal. */
    private Principal principal;

    /** The connection id. */
    private final String connectionId;

    /** The security package. */
    private final String securityPackage;

    /** The ntlm post. */
    private final boolean ntlmPost;

    /**
     * Whether or not 'rememberMe' should be enabled for the corresponding login attempt; default is <code>false</code>.
     */
    private final boolean rememberMe;

    /**
     * The location from where the login attempt occurs, or <code>null</code> if not known or explicitly omitted.
     */
    private final String host;

    /**
     * Instantiates a new negotiate token.
     *
     * @param newIn
     *            the new in
     * @param newOut
     *            the new out
     * @param newConnectionId
     *            the new connection id
     * @param newSecurityPackage
     *            the new security package
     * @param newNtlmPost
     *            the new ntlm post
     * @param newRememberMe
     *            the new remember me
     * @param newHost
     *            the new host
     */
    public NegotiateToken(final byte[] newIn, final byte[] newOut, final String newConnectionId,
            final String newSecurityPackage, final boolean newNtlmPost, final boolean newRememberMe,
            final String newHost) {
        this.in = newIn;
        this.out = newOut;
        this.connectionId = newConnectionId;
        this.securityPackage = newSecurityPackage;
        this.ntlmPost = newNtlmPost;

        this.rememberMe = newRememberMe;
        this.host = newHost;
    }

    /**
     * Gets the connection id.
     *
     * @return the connection id
     */
    public String getConnectionId() {
        return this.connectionId;
    }

    /**
     * Gets the security package.
     *
     * @return the security package
     */
    public String getSecurityPackage() {
        return this.securityPackage;
    }

    /**
     * Checks if is ntlm post.
     *
     * @return true, if is ntlm post
     */
    public boolean isNtlmPost() {
        return this.ntlmPost;
    }

    @Override
    public Object getCredentials() {
        return this.subject;
    }

    @Override
    public Principal getPrincipal() {
        return this.principal;
    }

    /**
     * Gets the out.
     *
     * @return the out
     */
    byte[] getOut() {
        return this.out;
    }

    /**
     * Sets the out.
     *
     * @param outToken
     *            the new out
     */
    public void setOut(final byte[] outToken) {
        this.out = outToken != null ? outToken.clone() : null;
    }

    /**
     * Sets the subject.
     *
     * @param value
     *            the new subject
     */
    public void setSubject(final Subject value) {
        this.subject = value;
    }

    /**
     * Gets the in.
     *
     * @return the in
     */
    public byte[] getIn() {
        return this.in.clone();
    }

    /**
     * Gets the subject.
     *
     * @return the subject
     */
    public Subject getSubject() {
        return this.subject;
    }

    /**
     * Creates the info.
     *
     * @return the authentication info
     */
    public AuthenticationInfo createInfo() {
        return new NegotiateInfo(this.subject, "NegotiateWaffleRealm");
    }

    /**
     * Sets the principal.
     *
     * @param value
     *            the new principal
     */
    public void setPrincipal(final Principal value) {
        this.principal = value;
    }

    /**
     * Returns <code>true</code> if the submitting user wishes their identity (principal(s)) to be remembered across
     * sessions, <code>false</code> otherwise. Unless overridden, this value is <code>false</code> by default.
     *
     * @return <code>true</code> if the submitting user wishes their identity (principal(s)) to be remembered across
     *         sessions, <code>false</code> otherwise (<code>false</code> by default).
     * @since 0.9
     */
    @Override
    public boolean isRememberMe() {
        return this.rememberMe;
    }

    /**
     * Returns the host name or IP string from where the authentication attempt occurs. May be <code>null</code> if the
     * host name/IP is unknown or explicitly omitted. It is up to the Authenticator implementation processing this token
     * if an authentication attempt without a host is valid or not.
     *
     * <p>
     * (Shiro's default Authenticator allows <code>null</code> hosts to support localhost and proxy server
     * environments).
     * </p>
     *
     * @return the host from where the authentication attempt occurs, or <code>null</code> if it is unknown or
     *         explicitly omitted.
     * @since 1.0
     */
    @Override
    public String getHost() {
        return this.host;
    }
}
