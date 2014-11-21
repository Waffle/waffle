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
package waffle.util;

import javax.servlet.http.HttpServletRequest;

import com.google.common.io.BaseEncoding;

/**
 * Authorization header.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class AuthorizationHeader {

    private HttpServletRequest request;

    public AuthorizationHeader(final HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
    }

    public String getHeader() {
        return this.request.getHeader("Authorization");
    }

    public boolean isNull() {
        return getHeader() == null || getHeader().length() == 0;
    }

    /**
     * Returns a supported security package string.
     * 
     * @return Negotiate or NTLM.
     */
    public String getSecurityPackage() {
        final String header = getHeader();

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
        return isNull() ? "<none>" : getHeader();
    }

    public String getToken() {
        return getHeader().substring(getSecurityPackage().length() + 1);
    }

    public byte[] getTokenBytes() {
        try {
            return BaseEncoding.base64().decode(getToken());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid authorization header.");
        }
    }

    public boolean isNtlmType1Message() {
        if (isNull()) {
            return false;
        }

        final byte[] tokenBytes = getTokenBytes();
        if (!NtlmMessage.isNtlmMessage(tokenBytes)) {
            return false;
        }

        return 1 == NtlmMessage.getMessageType(tokenBytes);
    }

    public boolean isSPNegoMessage() {

        if (isNull()) {
            return false;
        }

        final byte[] tokenBytes = getTokenBytes();
        if (!SPNegoMessage.isSPNegoMessage(tokenBytes)) {
            return false;
        }

        return true;
    }

    /**
     * When using NTLM authentication and the browser is making a POST request, it preemptively sends a Type 2
     * authentication message (without the POSTed data). The server responds with a 401, and the browser sends a Type 3
     * request with the POSTed data. This is to avoid the situation where user's credentials might be potentially
     * invalid, and all this data is being POSTed across the wire.
     * 
     * @return True if request is an NTLM POST or PUT with an Authorization header and no data.
     */
    public boolean isNtlmType1PostAuthorizationHeader() {
        if (!this.request.getMethod().equals("POST") && !this.request.getMethod().equals("PUT")) {
            return false;
        }

        if (this.request.getContentLength() != 0) {
            return false;
        }

        return isNtlmType1Message() || isSPNegoMessage();
    }
}
