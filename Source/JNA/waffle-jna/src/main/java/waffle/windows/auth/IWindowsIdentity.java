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
 * A Windows Identity.
 *
 * @author dblock[at]dblock[dot]org
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
