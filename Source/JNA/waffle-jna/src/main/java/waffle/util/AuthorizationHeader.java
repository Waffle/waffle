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
package waffle.util;

import java.util.Base64;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authorization header.
 *
 * @author dblock[at]dblock[dot]org
 */
public class AuthorizationHeader {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationHeader.class);

    /** The request. */
    private final HttpServletRequest request;

    /**
     * Instantiates a new authorization header.
     *
     * @param httpServletRequest
     *            the http servlet request
     */
    public AuthorizationHeader(final HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
    }

    /**
     * Gets the header.
     *
     * @return the header
     */
    public String getHeader() {
        return this.request.getHeader("Authorization");
    }

    /**
     * Checks if is null.
     *
     * @return true, if is null
     */
    public boolean isNull() {
        return this.getHeader() == null || this.getHeader().length() == 0;
    }

    /**
     * Returns a supported security package string.
     *
     * Authorization: NTLM the_token Authorization: Negotiate the_token Authorization: Bearer the_token
     *
     * @return AuthenticationScheme as SecurityPackage e.g. Negotiate, NTLM, Bearer.
     */
    public String getSecurityPackage() {
        final String header = this.getHeader();

        if (header == null) {
            throw new RuntimeException("Missing Authorization: header");
        }

        final int space = header.indexOf(' ');
        if (space > 0) {
            return header.substring(0, space);
        }

        throw new RuntimeException("Invalid Authorization header: " + header);
    }

    @Override
    public String toString() {
        return this.isNull() ? "<none>" : this.getHeader();
    }

    /**
     * Gets the token.
     *
     * @return the token
     */
    public String getToken() {
        return this.getHeader().substring(this.getSecurityPackage().length() + 1);
    }

    /**
     * Gets the token bytes.
     *
     * @return the token bytes
     */
    public byte[] getTokenBytes() {
        try {
            return Base64.getDecoder().decode(this.getToken());
        } catch (final IllegalArgumentException e) {
            AuthorizationHeader.LOGGER.debug("", e);
            throw new RuntimeException("Invalid authorization header.");
        }
    }

    /**
     * Checks if is ntlm type1 message.
     *
     * @return true, if is ntlm type1 message
     */
    public boolean isNtlmType1Message() {
        if (this.isNull()) {
            return false;
        }

        final byte[] tokenBytes = this.getTokenBytes();
        if (!NtlmMessage.isNtlmMessage(tokenBytes)) {
            return false;
        }

        return 1 == NtlmMessage.getMessageType(tokenBytes);
    }

    /**
     * Checks if is SP nego message.
     *
     * @return true, if is SP nego message that contains NegTokenInit
     */
    public boolean isSPNegTokenInitMessage() {

        if (this.isNull()) {
            return false;
        }

        final byte[] tokenBytes = this.getTokenBytes();
        return SPNegoMessage.isNegTokenInit(tokenBytes);
    }

    /**
     * When using NTLM authentication and the browser is making a POST request, it preemptively sends a Type 2
     * authentication message (without the POSTed data). The server responds with a 401, and the browser sends a Type 3
     * request with the POSTed data. This is to avoid the situation where user's credentials might be potentially
     * invalid, and all this data is being POSTed across the wire.
     *
     * @return True if request is an NTLM POST, PUT, or DELETE with an Authorization header and no data.
     */
    public boolean isNtlmType1PostAuthorizationHeader() {
        if (!"POST".equals(this.request.getMethod()) && !"PUT".equals(this.request.getMethod())
                && !"DELETE".equals(this.request.getMethod())) {
            return false;
        }

        if (this.request.getContentLength() != 0) {
            return false;
        }

        return this.isNtlmType1Message() || this.isSPNegTokenInitMessage();
    }

    public boolean isBearerAuthorizationHeader() {
        if (this.isNull()) {
            return false;
        }

        return this.getSecurityPackage().toUpperCase(Locale.ENGLISH).equalsIgnoreCase("BEARER");
    }
}
