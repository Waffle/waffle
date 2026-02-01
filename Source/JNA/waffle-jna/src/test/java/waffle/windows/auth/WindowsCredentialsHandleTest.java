/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;

/**
 * The Class WindowsCredentialsHandleTest.
 */
class WindowsCredentialsHandleTest {

    /**
     * Test get current.
     */
    @Test
    void testGetCurrent() {
        final IWindowsCredentialsHandle handle = WindowsCredentialsHandleImpl.getCurrent("Negotiate");
        Assertions.assertNotNull(handle);
        handle.initialize();
        handle.dispose();
    }
}
