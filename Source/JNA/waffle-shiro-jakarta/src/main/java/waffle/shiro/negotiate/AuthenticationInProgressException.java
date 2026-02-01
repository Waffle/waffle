/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.shiro.negotiate;

import org.apache.shiro.authc.AuthenticationException;

/**
 * Thrown when the negotiate authentication is being established and requires an extra roundtrip to the client.
 * <p>
 * Derived from net.skorgenes.security.jsecurity.negotiate.NegotiateAuthenticationFilter.
 *
 * @since 1.0.0
 */
public class AuthenticationInProgressException extends AuthenticationException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2684886728102100988L;

}
