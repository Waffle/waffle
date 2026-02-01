/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

/**
 * A Windows Computer.
 */
public interface IWindowsComputer {

    /**
     * Computer name.
     *
     * @return String.
     */
    String getComputerName();

    /**
     * Member of (domain).
     *
     * @return String.
     */
    String getMemberOf();

    /**
     * Join status.
     *
     * @return String.
     */
    String getJoinStatus();

    /**
     * Groups.
     *
     * @return Array of group names.
     */
    String[] getGroups();
}
