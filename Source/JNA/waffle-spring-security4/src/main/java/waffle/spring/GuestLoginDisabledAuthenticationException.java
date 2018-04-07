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
package waffle.spring;

import org.springframework.security.core.AuthenticationException;

/**
 * Guest login is disabled authentication exception.
 *
 * @author dblock[at]dblock[dot]org
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
