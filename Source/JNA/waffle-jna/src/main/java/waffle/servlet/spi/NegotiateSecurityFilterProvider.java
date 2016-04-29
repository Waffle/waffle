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
package waffle.servlet.spi;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;

import waffle.util.AuthorizationHeader;
import waffle.util.NtlmServletRequest;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * A negotiate security filter provider.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterProvider implements SecurityFilterProvider {

    /** The Constant LOGGER. */
    private static final Logger        LOGGER           = LoggerFactory
                                                                .getLogger(NegotiateSecurityFilterProvider.class);

    /** The Constant WWW_AUTHENTICATE. */
    private static final String        WWW_AUTHENTICATE = "WWW-Authenticate";

    /** The Constant PROTOCOLS. */
    private static final String        PROTOCOLS        = "protocols";

    /** The Constant NEGOTIATE. */
    private static final String        NEGOTIATE        = "Negotiate";

    /** The Constant NTLM. */
    private static final String        NTLM             = "NTLM";

    /** The protocols. */
    private List<String>               protocols        = new ArrayList<>();

    /** The auth. */
    private final IWindowsAuthProvider auth;

    /**
     * Instantiates a new negotiate security filter provider.
     *
     * @param newAuthProvider
     *            the new auth provider
     */
    public NegotiateSecurityFilterProvider(final IWindowsAuthProvider newAuthProvider) {
        this.auth = newAuthProvider;
        this.protocols.add(NegotiateSecurityFilterProvider.NEGOTIATE);
        this.protocols.add(NegotiateSecurityFilterProvider.NTLM);
    }

    /**
     * Gets the protocols.
     *
     * @return the protocols
     */
    public List<String> getProtocols() {
        return this.protocols;
    }

    /**
     * Sets the protocols.
     *
     * @param values
     *            the new protocols
     */
    public void setProtocols(final List<String> values) {
        this.protocols = values;
    }

    /*
     * (non-Javadoc)
     * @see waffle.servlet.spi.SecurityFilterProvider#sendUnauthorized(javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void sendUnauthorized(final HttpServletResponse response) {
        final Iterator<String> protocolsIterator = this.protocols.iterator();
        while (protocolsIterator.hasNext()) {
            response.addHeader(NegotiateSecurityFilterProvider.WWW_AUTHENTICATE, protocolsIterator.next());
        }
    }

    /*
     * (non-Javadoc)
     * @see waffle.servlet.spi.SecurityFilterProvider#isPrincipalException(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean isPrincipalException(final HttpServletRequest request) {
        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        final boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
        NegotiateSecurityFilterProvider.LOGGER.debug("authorization: {}, ntlm post: {}", authorizationHeader,
                Boolean.valueOf(ntlmPost));
        return ntlmPost;
    }

    /*
     * (non-Javadoc)
     * @see waffle.servlet.spi.SecurityFilterProvider#doFilter(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public IWindowsIdentity doFilter(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {

        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        final boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();

        // maintain a connection-based session for NTLM tokens
        final String connectionId = NtlmServletRequest.getConnectionId(request);
        final String securityPackage = authorizationHeader.getSecurityPackage();
        NegotiateSecurityFilterProvider.LOGGER.debug("security package: {}, connection id: {}", securityPackage,
                connectionId);

        if (ntlmPost) {
            // type 2 NTLM authentication message received
            this.auth.resetSecurityToken(connectionId);
        }

        final byte[] tokenBuffer = authorizationHeader.getTokenBytes();
        NegotiateSecurityFilterProvider.LOGGER.debug("token buffer: {} byte(s)", Integer.valueOf(tokenBuffer.length));
        final IWindowsSecurityContext securityContext = this.auth.acceptSecurityToken(connectionId, tokenBuffer,
                securityPackage);

        final byte[] continueTokenBytes = securityContext.getToken();
        if (continueTokenBytes != null && continueTokenBytes.length > 0) {
            final String continueToken = BaseEncoding.base64().encode(continueTokenBytes);
            NegotiateSecurityFilterProvider.LOGGER.debug("continue token: {}", continueToken);
            response.addHeader(NegotiateSecurityFilterProvider.WWW_AUTHENTICATE, securityPackage + " " + continueToken);
        }

        NegotiateSecurityFilterProvider.LOGGER.debug("continue required: {}",
                Boolean.valueOf(securityContext.isContinue()));
        if (securityContext.isContinue() || ntlmPost) {
            response.setHeader("Connection", "keep-alive");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
            return null;
        }

        final IWindowsIdentity identity = securityContext.getIdentity();
        securityContext.dispose();
        return identity;
    }

    /*
     * (non-Javadoc)
     * @see waffle.servlet.spi.SecurityFilterProvider#isSecurityPackageSupported(java.lang.String)
     */
    @Override
    public boolean isSecurityPackageSupported(final String securityPackage) {
        for (final String protocol : this.protocols) {
            if (protocol.equalsIgnoreCase(securityPackage)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see waffle.servlet.spi.SecurityFilterProvider#initParameter(java.lang.String, java.lang.String)
     */
    @Override
    public void initParameter(final String parameterName, final String parameterValue) {
        if (parameterName.equals(NegotiateSecurityFilterProvider.PROTOCOLS)) {
            this.protocols = new ArrayList<>();
            final String[] protocolNames = parameterValue.split("\\s+");
            for (String protocolName : protocolNames) {
                protocolName = protocolName.trim();
                if (protocolName.length() > 0) {
                    NegotiateSecurityFilterProvider.LOGGER.debug("init protocol: {}", protocolName);
                    if (protocolName.equals(NegotiateSecurityFilterProvider.NEGOTIATE)
                            || protocolName.equals(NegotiateSecurityFilterProvider.NTLM)) {
                        this.protocols.add(protocolName);
                    } else {
                        NegotiateSecurityFilterProvider.LOGGER.error("unsupported protocol: {}", protocolName);
                        throw new RuntimeException("Unsupported protocol: " + protocolName);
                    }
                }
            }
        } else {
            throw new InvalidParameterException(parameterName);
        }
    }
}
