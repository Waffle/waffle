/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * Tests for {@link MockWindowsIdentity}.
 */
class MockWindowsIdentityTest {

    /** The identity. */
    private MockWindowsIdentity identity;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        final List<String> groups = new ArrayList<>();
        groups.add("Users");
        groups.add("Everyone");
        this.identity = new MockWindowsIdentity("DOMAIN\\testuser", groups);
    }

    /**
     * Test get fqn.
     */
    @Test
    void testGetFqn() {
        Assertions.assertEquals("DOMAIN\\testuser", this.identity.getFqn());
    }

    /**
     * Test get groups.
     */
    @Test
    void testGetGroups() {
        final IWindowsAccount[] groups = this.identity.getGroups();
        Assertions.assertNotNull(groups);
        Assertions.assertEquals(2, groups.length);
    }

    /**
     * Test get sid returns empty bytes.
     */
    @Test
    void testGetSidReturnsEmptyBytes() {
        final byte[] sid = this.identity.getSid();
        Assertions.assertNotNull(sid);
        Assertions.assertEquals(0, sid.length);
    }

    /**
     * Test get sid string.
     */
    @Test
    void testGetSidString() {
        final String sidString = this.identity.getSidString();
        Assertions.assertNotNull(sidString);
        Assertions.assertTrue(sidString.startsWith("S-"));
    }

    /**
     * Test is guest false for normal user.
     */
    @Test
    void testIsGuestFalseForNormalUser() {
        Assertions.assertFalse(this.identity.isGuest());
    }

    /**
     * Test is guest true for guest user.
     */
    @Test
    void testIsGuestTrueForGuestUser() {
        final MockWindowsIdentity guest = new MockWindowsIdentity("Guest", new ArrayList<>());
        Assertions.assertTrue(guest.isGuest());
    }

    /**
     * Test dispose does not throw.
     */
    @Test
    void testDisposeDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> this.identity.dispose());
    }

    /**
     * Test impersonate returns context.
     */
    @Test
    void testImpersonateReturnsContext() {
        final IWindowsImpersonationContext ctx = this.identity.impersonate();
        Assertions.assertNotNull(ctx);
        Assertions.assertDoesNotThrow(() -> ctx.revertToSelf());
    }
}
