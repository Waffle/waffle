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
import waffle.windows.auth.PrincipalFormat;

/**
 * The Class WindowsPrincipalTest.
 */
class WindowsPrincipalTest {

    /** The Constant TEST_FQN. */
    private static final String TEST_FQN = "ACME\\john.smith";

    /** The Constant TEST_SID. */
    private static final String TEST_SID = "S-1-5-21-12345";

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

    /**
     * Test equals with self.
     */
    @Test
    void testEqualsWithSelf() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        Assertions.assertEquals(principal, principal);
    }

    /**
     * Test not equals with other type.
     */
    @Test
    void testNotEqualsWithOtherType() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        Assertions.assertNotEquals("string", principal);
    }

    /**
     * Test sid format.
     */
    @Test
    void testSidFormat() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getSidString();
                this.result = WindowsPrincipalTest.TEST_SID;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity, PrincipalFormat.SID,
                PrincipalFormat.SID);
        Assertions.assertEquals(WindowsPrincipalTest.TEST_FQN, principal.getName());
        Assertions.assertTrue(principal.hasRole(WindowsPrincipalTest.TEST_SID));
    }

    /**
     * Test both format.
     */
    @Test
    void testBothFormat() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getSidString();
                this.result = WindowsPrincipalTest.TEST_SID;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity, PrincipalFormat.BOTH,
                PrincipalFormat.BOTH);
        Assertions.assertTrue(principal.hasRole(WindowsPrincipalTest.TEST_FQN));
        Assertions.assertTrue(principal.hasRole(WindowsPrincipalTest.TEST_SID));
    }

    /**
     * Test none format.
     */
    @Test
    void testNoneFormat() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getSidString();
                this.result = WindowsPrincipalTest.TEST_SID;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity, PrincipalFormat.NONE,
                PrincipalFormat.NONE);
        Assertions.assertFalse(principal.hasRole(WindowsPrincipalTest.TEST_FQN));
        Assertions.assertFalse(principal.hasRole(WindowsPrincipalTest.TEST_SID));
    }

    /**
     * Test get roles string.
     */
    @Test
    void testGetRolesString() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        final String rolesString = principal.getRolesString();
        Assertions.assertNotNull(rolesString);
        Assertions.assertTrue(rolesString.contains(WindowsPrincipalTest.TEST_FQN));
    }

    /**
     * Test get groups.
     */
    @Test
    void testGetGroups() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        Assertions.assertNotNull(principal.getGroups());
        Assertions.assertEquals(0, principal.getGroups().size());
    }

    /**
     * Test get sid.
     */
    @Test
    void testGetSid() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getSid();
                this.result = new byte[] { 1, 2, 3 };
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        Assertions.assertNotNull(principal.getSid());
        Assertions.assertEquals(3, principal.getSid().length);
    }

    /**
     * Test get identity.
     */
    @Test
    void testGetIdentity() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        Assertions.assertNotNull(principal.getIdentity());
    }

}
