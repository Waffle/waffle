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
package waffle.windows.auth;

/**
 * Windows account.
 *
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsAccount {

    /**
     * Security identifier.
     *
     * @return String in the S- format.
     */
    String getSidString();

    /**
     * Fully qualified username.
     *
     * @return String.
     */
    String getFqn();

    /**
     * User name.
     *
     * @return String.
     */
    String getName();

    /**
     * Domain name.
     *
     * @return String.
     */
    String getDomain();
}
