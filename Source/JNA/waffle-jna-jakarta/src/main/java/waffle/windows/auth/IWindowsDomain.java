/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

/**
 * A Windows Domain.
 */
public interface IWindowsDomain {

    /**
     * Fully qualified domain name.
     *
     * @return String.
     */
    String getFqn();

    /**
     * Trust direction.
     *
     * @return String.
     */
    String getTrustDirectionString();

    /**
     * Trust type.
     *
     * @return String.
     */
    String getTrustTypeString();
}
