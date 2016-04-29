/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.shiro.negotiate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Derived from net.skorgenes.security.jsecurity.negotiate.NegotiateAuthenticationFilter. see:
 * https://bitbucket.org/lothor
 * /shiro-negotiate/src/7b25efde130b9cbcacf579b3f926c532d919aa23/src/main/java/net/skorgenes/
 * security/jsecurity/negotiate/NegotiateAuthenticationFilter.java?at=default
 *
 * @author Dan Rollo
 */
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;

import waffle.util.AuthorizationHeader;
import waffle.util.NtlmServletRequest;

/**
 * A authentication filter that implements the HTTP Negotiate mechanism. The current user is authenticated, providing
 * single-sign-on
 * 
 * @author Dan Rollo
 * @since 1.0.0
 */
public class NegotiateAuthenticationFilter extends AuthenticatingFilter {

    /**
     * This class's private logger.
     */
    private static final Logger       LOGGER              = LoggerFactory
                                                                  .getLogger(NegotiateAuthenticationFilter.class);

    // TODO things (sometimes) break, depending on what user account is running tomcat:
    // related to setSPN and running tomcat server as NT Service account vs. as normal user account.
    // http://waffle.codeplex.com/discussions/254748
    // setspn -A HTTP/<server-fqdn> <user_tomcat_running_under>
    /** The Constant PROTOCOLS. */
    private static final List<String> PROTOCOLS           = new ArrayList<>();

    /** The failure key attribute. */
    private String                    failureKeyAttribute = FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME;

    /** The remember me param. */
    private String                    rememberMeParam     = FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM;

    /**
     * Instantiates a new negotiate authentication filter.
     */
    public NegotiateAuthenticationFilter() {
        NegotiateAuthenticationFilter.PROTOCOLS.add("Negotiate");
        NegotiateAuthenticationFilter.PROTOCOLS.add("NTLM");
    }

    /**
     * Gets the remember me param.
     *
     * @return the remember me param
     */
    public String getRememberMeParam() {
        return this.rememberMeParam;
    }

    /**
     * Sets the request parameter name to look for when acquiring the rememberMe boolean value. Unless overridden by
     * calling this method, the default is <code>rememberMe</code>. <br>
     * <br>
     * RememberMe will be <code>true</code> if the parameter value equals any of those supported by
     * {@link org.apache.shiro.web.util.WebUtils#isTrue(javax.servlet.ServletRequest, String)
     * WebUtils.isTrue(request,value)}, <code>false</code> otherwise.
     * 
     * @param value
     *            the name of the request param to check for acquiring the rememberMe boolean value.
     */
    public void setRememberMeParam(final String value) {
        this.rememberMeParam = value;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.shiro.web.filter.authc.AuthenticatingFilter#isRememberMe(javax.servlet.ServletRequest)
     */
    @Override
    protected boolean isRememberMe(final ServletRequest request) {
        return WebUtils.isTrue(request, this.getRememberMeParam());
    }

    /*
     * (non-Javadoc)
     * @see org.apache.shiro.web.filter.authc.AuthenticatingFilter#createToken(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse)
     */
    @Override
    protected AuthenticationToken createToken(final ServletRequest request, final ServletResponse response) {
        final String authorization = this.getAuthzHeader(request);
        final String[] elements = authorization.split(" ");
        final byte[] inToken = BaseEncoding.base64().decode(elements[1]);

        // maintain a connection-based session for NTLM tokens
        // TODO see about changing this parameter to ServletRequest in waffle
        final String connectionId = NtlmServletRequest.getConnectionId((HttpServletRequest) request);
        final String securityPackage = elements[0];

        // TODO see about changing this parameter to ServletRequest in waffle
        final AuthorizationHeader authorizationHeader = new AuthorizationHeader((HttpServletRequest) request);
        final boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();

        NegotiateAuthenticationFilter.LOGGER.debug("security package: {}, connection id: {}, ntlmPost: {}",
                securityPackage, connectionId, Boolean.valueOf(ntlmPost));

        final boolean rememberMe = this.isRememberMe(request);
        final String host = this.getHost(request);

        return new NegotiateToken(inToken, new byte[0], connectionId, securityPackage, ntlmPost, rememberMe, host);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.shiro.web.filter.authc.AuthenticatingFilter#onLoginSuccess(org.apache.shiro.authc.AuthenticationToken,
     * org.apache.shiro.subject.Subject, javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    protected boolean onLoginSuccess(final AuthenticationToken token, final Subject subject,
            final ServletRequest request, final ServletResponse response) throws Exception {
        request.setAttribute("MY_SUBJECT", ((NegotiateToken) token).getSubject());
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.shiro.web.filter.authc.AuthenticatingFilter#onLoginFailure(org.apache.shiro.authc.AuthenticationToken,
     * org.apache.shiro.authc.AuthenticationException, javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    protected boolean onLoginFailure(final AuthenticationToken token, final AuthenticationException e,
            final ServletRequest request, final ServletResponse response) {
        if (e instanceof AuthenticationInProgressException) {
            // negotiate is processing
            final String protocol = this.getAuthzHeaderProtocol(request);
            NegotiateAuthenticationFilter.LOGGER.debug("Negotiation in progress for protocol: {}", protocol);
            this.sendChallengeDuringNegotiate(protocol, response, ((NegotiateToken) token).getOut());
            return false;
        }
        NegotiateAuthenticationFilter.LOGGER.warn("login exception: {}", e.getMessage());

        // do not send token.out bytes, this was a login failure.
        this.sendChallengeOnFailure(response);

        this.setFailureAttribute(request, e);
        return true;
    }

    /**
     * Sets the failure attribute.
     *
     * @param request
     *            the request
     * @param ae
     *            the ae
     */
    protected void setFailureAttribute(final ServletRequest request, final AuthenticationException ae) {
        final String className = ae.getClass().getName();
        request.setAttribute(this.getFailureKeyAttribute(), className);
    }

    /**
     * Gets the failure key attribute.
     *
     * @return the failure key attribute
     */
    public String getFailureKeyAttribute() {
        return this.failureKeyAttribute;
    }

    /**
     * Sets the failure key attribute.
     *
     * @param value
     *            the new failure key attribute
     */
    public void setFailureKeyAttribute(final String value) {
        this.failureKeyAttribute = value;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.shiro.web.filter.AccessControlFilter#onAccessDenied(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse)
     */
    @Override
    protected boolean onAccessDenied(final ServletRequest request, final ServletResponse response) throws Exception {
        // false by default or we wouldn't be in
        boolean loggedIn = false;
        // this method
        if (this.isLoginAttempt(request)) {
            loggedIn = this.executeLogin(request, response);
        } else {
            NegotiateAuthenticationFilter.LOGGER.debug("authorization required, supported protocols: {}",
                    NegotiateAuthenticationFilter.PROTOCOLS);
            this.sendChallengeInitiateNegotiate(response);
        }
        return loggedIn;
    }

    /**
     * Determines whether the incoming request is an attempt to log in.
     * <p/>
     * The default implementation obtains the value of the request's
     * {@link org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter#AUTHORIZATION_HEADER AUTHORIZATION_HEADER}
     * , and if it is not <code>null</code>, delegates to
     * {@link org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter#isLoginAttempt(String)
     * isLoginAttempt(authzHeaderValue)}. If the header is <code>null</code>, <code>false</code> is returned.
     * 
     * @param request
     *            incoming ServletRequest
     * @return true if the incoming request is an attempt to log in based, false otherwise
     */
    private boolean isLoginAttempt(final ServletRequest request) {
        final String authzHeader = this.getAuthzHeader(request);
        return authzHeader != null && this.isLoginAttempt(authzHeader);
    }

    /**
     * Returns the {@link org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter#AUTHORIZATION_HEADER
     * AUTHORIZATION_HEADER} from the specified ServletRequest.
     * <p/>
     * This implementation merely casts the request to an <code>HttpServletRequest</code> and returns the header:
     * <p/>
     * <code>HttpServletRequest httpRequest = {@link WebUtils#toHttp(javax.servlet.ServletRequest) toHttp(reaquest)};<br/>
     * return httpRequest.getHeader({@link org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter#AUTHORIZATION_HEADER AUTHORIZATION_HEADER});</code>
     * 
     * @param request
     *            the incoming <code>ServletRequest</code>
     * @return the <code>Authorization</code> header's value.
     */
    private String getAuthzHeader(final ServletRequest request) {
        final HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader("Authorization");
    }

    /**
     * Gets the authz header protocol.
     *
     * @param request
     *            the request
     * @return the authz header protocol
     */
    private String getAuthzHeaderProtocol(final ServletRequest request) {
        final String authzHeader = this.getAuthzHeader(request);
        return authzHeader.substring(0, authzHeader.indexOf(" "));
    }

    /**
     * Default implementation that returns <code>true</code> if the specified <code>authzHeader</code> starts with the
     * same (case-insensitive) characters specified by any of the configured protocols (Negotiate or NTLM),
     * <code>false</code> otherwise.
     * 
     * @param authzHeader
     *            the 'Authorization' header value (guaranteed to be non-null if the
     *            {@link #isLoginAttempt(javax.servlet.ServletRequest)} method is not overriden).
     * @return <code>true</code> if the authzHeader value matches any of the configured protocols (Negotiate or NTLM).
     */
    boolean isLoginAttempt(final String authzHeader) {
        for (final String protocol : NegotiateAuthenticationFilter.PROTOCOLS) {
            if (authzHeader.toLowerCase().startsWith(protocol.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Builds the challenge for authorization by setting a HTTP <code>401</code> (Unauthorized) status as well as the
     * response's {@link org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter#AUTHENTICATE_HEADER
     * AUTHENTICATE_HEADER}.
     * 
     * @param protocols
     *            protocols for which to send a challenge. In initial cases, will be all supported protocols. In the
     *            midst of negotiation, will be only the protocol being negotiated.
     * 
     * @param response
     *            outgoing ServletResponse
     * @param out
     *            token.out or null
     */
    private void sendChallenge(final List<String> protocols, final ServletResponse response, final byte[] out) {
        final HttpServletResponse httpResponse = WebUtils.toHttp(response);
        this.sendAuthenticateHeader(protocols, out, httpResponse);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * Send challenge initiate negotiate.
     *
     * @param response
     *            the response
     */
    void sendChallengeInitiateNegotiate(final ServletResponse response) {
        this.sendChallenge(NegotiateAuthenticationFilter.PROTOCOLS, response, null);
    }

    /**
     * Send challenge during negotiate.
     *
     * @param protocol
     *            the protocol
     * @param response
     *            the response
     * @param out
     *            the out
     */
    void sendChallengeDuringNegotiate(final String protocol, final ServletResponse response, final byte[] out) {
        final List<String> protocolsList = new ArrayList<>();
        protocolsList.add(protocol);
        this.sendChallenge(protocolsList, response, out);
    }

    /**
     * Send challenge on failure.
     *
     * @param response
     *            the response
     */
    void sendChallengeOnFailure(final ServletResponse response) {
        final HttpServletResponse httpResponse = WebUtils.toHttp(response);
        this.sendUnauthorized(NegotiateAuthenticationFilter.PROTOCOLS, null, httpResponse);
        httpResponse.setHeader("Connection", "close");
        try {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.flushBuffer();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send authenticate header.
     *
     * @param protocolsList
     *            the protocols list
     * @param out
     *            the out
     * @param httpResponse
     *            the http response
     */
    private void sendAuthenticateHeader(final List<String> protocolsList, final byte[] out,
            final HttpServletResponse httpResponse) {
        this.sendUnauthorized(protocolsList, out, httpResponse);
        httpResponse.setHeader("Connection", "keep-alive");
    }

    /**
     * Send unauthorized.
     *
     * @param protocols
     *            the protocols
     * @param out
     *            the out
     * @param response
     *            the response
     */
    private void sendUnauthorized(final List<String> protocols, final byte[] out, final HttpServletResponse response) {
        for (final String protocol : protocols) {
            if (out == null || out.length == 0) {
                response.addHeader("WWW-Authenticate", protocol);
            } else {
                response.setHeader("WWW-Authenticate", protocol + " " + BaseEncoding.base64().encode(out));
            }
        }
    }

}