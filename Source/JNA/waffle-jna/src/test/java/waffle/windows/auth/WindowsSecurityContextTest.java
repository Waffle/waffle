/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

/**
 * The Class WindowsSecurityContextTest.
 */
class WindowsSecurityContextTest {

    /**
     * Test negotiate.
     */
    @Test
    void testNegotiate() {
        final String securityPackage = "Negotiate";
        // security context
        final IWindowsSecurityContext ctx = WindowsSecurityContextImpl.getCurrent(securityPackage,
                WindowsAccountImpl.getCurrentUsername());
        Assertions.assertTrue(ctx.isContinue());
        Assertions.assertEquals(securityPackage, ctx.getSecurityPackage());
        assertThat(ctx.getToken()).isNotEmpty();
        ctx.dispose();
    }
}
