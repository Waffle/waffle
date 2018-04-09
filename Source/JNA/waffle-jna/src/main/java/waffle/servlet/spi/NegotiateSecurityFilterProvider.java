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
package waffle.servlet.spi;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiateSecurityFilterProvider.class);

    /** The Constant WWW_AUTHENTICATE. */
    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    /** The Constant PROTOCOLS. */
    private static final String PROTOCOLS = "protocols";

    /** The Constant NEGOTIATE. */
    private static final String NEGOTIATE = "Negotiate";

    /** The Constant NTLM. */
    private static final String NTLM = "NTLM";

    /** The protocols. */
    private List<String> protocols = new ArrayList<>();

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

    @Override
    public void sendUnauthorized(final HttpServletResponse response) {
        for (final String protocol : this.protocols) {
            response.addHeader(NegotiateSecurityFilterProvider.WWW_AUTHENTICATE, protocol);
        }
    }

    @Override
    public boolean isPrincipalException(final HttpServletRequest request) {
        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        final boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
        NegotiateSecurityFilterProvider.LOGGER.debug("authorization: {}, ntlm post: {}", authorizationHeader,
                Boolean.valueOf(ntlmPost));
        return ntlmPost;
    }

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
            final String continueToken = Base64.getEncoder().encodeToString(continueTokenBytes);
            NegotiateSecurityFilterProvider.LOGGER.debug("continue token: {}", continueToken);
            response.addHeader(NegotiateSecurityFilterProvider.WWW_AUTHENTICATE, securityPackage + " " + continueToken);
        }

        NegotiateSecurityFilterProvider.LOGGER.debug("continue required: {}",
                Boolean.valueOf(securityContext.isContinue()));
        if (securityContext.isContinue()) {
            response.setHeader("Connection", "keep-alive");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
            return null;
        }

        final IWindowsIdentity identity = securityContext.getIdentity();
        securityContext.dispose();
        return identity;
    }

    @Override
    public boolean isSecurityPackageSupported(final String securityPackage) {
        for (final String protocol : this.protocols) {
            if (protocol.equalsIgnoreCase(securityPackage)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initParameter(final String parameterName, final String parameterValue) {
        if (NegotiateSecurityFilterProvider.PROTOCOLS.equals(parameterName)) {
            this.protocols = new ArrayList<>();
            final String[] protocolNames = parameterValue.split("\\s+");
            for (String protocolName : protocolNames) {
                protocolName = protocolName.trim();
                if (protocolName.length() > 0) {
                    NegotiateSecurityFilterProvider.LOGGER.debug("init protocol: {}", protocolName);
                    if (NegotiateSecurityFilterProvider.NEGOTIATE.equals(protocolName)
                            || NegotiateSecurityFilterProvider.NTLM.equals(protocolName)) {
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
