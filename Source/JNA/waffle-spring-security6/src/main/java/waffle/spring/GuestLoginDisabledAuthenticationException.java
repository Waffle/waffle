/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring;

import org.springframework.security.core.AuthenticationException;

/**
 * Guest login is disabled authentication exception.
 */
public class GuestLoginDisabledAuthenticationException extends AuthenticationException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new guest login disabled authentication exception.
     *
     * @param msg
     *            the msg
     */
    public GuestLoginDisabledAuthenticationException(final String msg) {
        super(msg);
    }
}
