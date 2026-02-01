/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

/**
 * Windows account.
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
