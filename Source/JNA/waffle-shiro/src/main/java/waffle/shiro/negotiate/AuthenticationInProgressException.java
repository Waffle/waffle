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

/**
 * Derived from net.skorgenes.security.jsecurity.negotiate.NegotiateAuthenticationFilter.
 *
 * @author Dan Rollo
 */
import org.apache.shiro.authc.AuthenticationException;

/**
 * Thrown when the negotiate authentication is being established and requires an extra roundtrip to the client.
 * 
 * @author Dan Rollo
 * @since 1.0.0
 */
public class AuthenticationInProgressException extends AuthenticationException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2684886728102100988L;
}