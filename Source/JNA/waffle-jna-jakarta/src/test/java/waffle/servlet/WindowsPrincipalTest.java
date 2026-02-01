/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;

/**
 * The Class WindowsPrincipalTest.
 */
class WindowsPrincipalTest {

    /** The Constant TEST_FQN. */
    private static final String TEST_FQN = "ACME\\john.smith";

    /** The windows identity. */
    @Mocked
    private IWindowsIdentity windowsIdentity;

    /**
     * Test to string.
     */
    @Test
    void testToString() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        Assertions.assertEquals(WindowsPrincipalTest.TEST_FQN, principal.getName());
        Assertions.assertEquals(WindowsPrincipalTest.TEST_FQN, principal.toString());
    }

    /**
     * Test equals and hash code.
     */
    @Test
    void testEqualsAndHashCode() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        final WindowsPrincipal principal2 = new WindowsPrincipal(this.windowsIdentity);
        Assertions.assertTrue(principal.equals(principal2) && principal2.equals(principal));
        Assertions.assertEquals(principal.hashCode(), principal2.hashCode());
        Assertions.assertEquals(principal.hashCode(), WindowsPrincipalTest.TEST_FQN.hashCode());
    }

}
