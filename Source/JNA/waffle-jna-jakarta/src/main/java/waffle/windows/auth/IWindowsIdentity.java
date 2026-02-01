/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

/**
 * A Windows Identity.
 */
public interface IWindowsIdentity {

    /**
     * Sid.
     *
     * @return String.
     */
    String getSidString();

    /**
     * Sid.
     *
     * @return Array of bytes.
     */
    byte[] getSid();

    /**
     * Fully qualified name.
     *
     * @return String.
     */
    String getFqn();

    /**
     * Group memberships.
     *
     * @return Array of accounts.
     */
    IWindowsAccount[] getGroups();

    /**
     * Impersonate a logged on user.
     *
     * @return An impersonation context.
     */
    IWindowsImpersonationContext impersonate();

    /**
     * Dispose of the Windows identity.
     */
    void dispose();

    /**
     * Returns true if the identity represents a Guest account.
     *
     * @return True if the identity represents a Guest account, false otherwise.
     */
    boolean isGuest();
}
