/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

import com.sun.jna.platform.win32.Sspi.CredHandle;

/**
 * Windows credentials handle.
 */
public interface IWindowsCredentialsHandle {
    /**
     * Initialize.
     */
    void initialize();

    /**
     * Dispose.
     */
    void dispose();

    /**
     * Return a security handle.
     *
     * @return CredHandle.
     */
    CredHandle getHandle();
}
