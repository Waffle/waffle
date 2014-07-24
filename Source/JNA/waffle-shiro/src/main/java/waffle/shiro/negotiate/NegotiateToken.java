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
package waffle.shiro.negotiate;

/**
 * Derived from net.skorgenes.security.jsecurity.negotiate.NegotiateToken.
 * see: https://bitbucket.org/lothor/shiro-negotiate/src/7b25efde130b9cbcacf579b3f926c532d919aa23/src/main/java/net/skorgenes/security/jsecurity/negotiate/NegotiateAuthenticationFilter.java?at=default
 *
 * @author Dan Rollo
 * Date: 1/15/13
 * Time: 10:54 PM
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
    private static final long serialVersionUID = 1345343228636916781L;

    private final byte[]      in;

    private byte[]            out;

    private Subject           subject;

    private Object            principal;

    private final String      connectionId;
    private final String      securityPackage;
    private final boolean     ntlmPost;

    /**
     * Whether or not 'rememberMe' should be enabled for the corresponding login attempt; default is <code>false</code>
     */
    private final boolean     rememberMe;

    /**
     * The location from where the login attempt occurs, or <code>null</code> if not known or explicitly omitted.
     */
    private final String      host;

    public NegotiateToken(final byte[] in, final byte[] out, final String connectionId, final String securityPackage,
            final boolean ntlmPost, final boolean rememberMe, final String host) {
        this.in = in;
        this.out = out;
        this.connectionId = connectionId;
        this.securityPackage = securityPackage;
        this.ntlmPost = ntlmPost;

        this.rememberMe = rememberMe;
        this.host = host;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public String getSecurityPackage() {
        return securityPackage;
    }

    public boolean isNtlmPost() {
        return ntlmPost;
    }

    @Override
    public Object getCredentials() {
        return subject;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    byte[] getOut() {
        return out;
    }

    public void setOut(final byte[] outToken) {
        this.out = (outToken != null ? outToken.clone() : null);
    }

    public void setSubject(final Subject subject) {
        this.subject = subject;
    }

    public byte[] getIn() {
        return in.clone();
    }

    public Subject getSubject() {
        return subject;
    }

    public AuthenticationInfo createInfo() {
        return new NegotiateInfo(subject, "NegotiateWaffleRealm");
    }

    public void setPrincipal(final Object principal) {
        this.principal = principal;
    }

    /**
     * Returns <tt>true</tt> if the submitting user wishes their identity (principal(s)) to be remembered across
     * sessions, <tt>false</tt> otherwise. Unless overridden, this value is <tt>false</tt> by default.
     * 
     * @return <tt>true</tt> if the submitting user wishes their identity (principal(s)) to be remembered across
     *         sessions, <tt>false</tt> otherwise (<tt>false</tt> by default).
     * @since 0.9
     */
    @Override
    public boolean isRememberMe() {
        return rememberMe;
    }

    /**
     * Returns the host name or IP string from where the authentication attempt occurs. May be <tt>null</tt> if the host
     * name/IP is unknown or explicitly omitted. It is up to the Authenticator implementation processing this token if
     * an authentication attempt without a host is valid or not.
     * <p/>
     * <p>
     * (Shiro's default Authenticator allows <tt>null</tt> hosts to support localhost and proxy server environments).
     * </p>
     * 
     * @return the host from where the authentication attempt occurs, or <tt>null</tt> if it is unknown or explicitly
     *         omitted.
     * @since 1.0
     */
    @Override
    public String getHost() {
        return host;
    }
}