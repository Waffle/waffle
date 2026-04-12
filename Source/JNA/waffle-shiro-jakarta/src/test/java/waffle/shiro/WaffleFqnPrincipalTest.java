/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.shiro;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.mock.MockWindowsIdentity;

/**
 * Tests for {@link WaffleFqnPrincipal}.
 */
class WaffleFqnPrincipalTest {

    /** The principal. */
    private WaffleFqnPrincipal principal;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        final List<String> groups = new ArrayList<>();
        groups.add("DOMAIN\\group1");
        groups.add("DOMAIN\\group2");
        final MockWindowsIdentity identity = new MockWindowsIdentity("DOMAIN\\user1", groups);
        this.principal = new WaffleFqnPrincipal(identity);
    }

    /**
     * Test get fqn.
     */
    @Test
    void testGetFqn() {
        Assertions.assertEquals("DOMAIN\\user1", this.principal.getFqn());
    }

    /**
     * Test get group fqns.
     */
    @Test
    void testGetGroupFqns() {
        Assertions.assertEquals(2, this.principal.getGroupFqns().size());
        Assertions.assertTrue(this.principal.getGroupFqns().contains("DOMAIN\\group1"));
        Assertions.assertTrue(this.principal.getGroupFqns().contains("DOMAIN\\group2"));
    }

    /**
     * Test equals same fqn.
     */
    @Test
    void testEqualsSameFqn() {
        final List<String> groups = new ArrayList<>();
        final MockWindowsIdentity identity2 = new MockWindowsIdentity("DOMAIN\\user1", groups);
        final WaffleFqnPrincipal principal2 = new WaffleFqnPrincipal(identity2);
        Assertions.assertEquals(this.principal, principal2);
        Assertions.assertEquals(this.principal.hashCode(), principal2.hashCode());
    }

    /**
     * Test equals self.
     */
    @Test
    void testEqualsSelf() {
        Assertions.assertEquals(this.principal, this.principal);
    }

    /**
     * Test not equals different fqn.
     */
    @Test
    void testNotEqualsDifferentFqn() {
        final List<String> groups = new ArrayList<>();
        final MockWindowsIdentity identity2 = new MockWindowsIdentity("DOMAIN\\user2", groups);
        final WaffleFqnPrincipal principal2 = new WaffleFqnPrincipal(identity2);
        Assertions.assertNotEquals(this.principal, principal2);
    }

    /**
     * Test not equals null.
     */
    @Test
    void testNotEqualsNull() {
        Assertions.assertNotEquals(null, this.principal);
    }

    /**
     * Test not equals different type.
     */
    @Test
    void testNotEqualsDifferentType() {
        Assertions.assertNotEquals("string", this.principal);
    }

    /**
     * Test to string.
     */
    @Test
    void testToString() {
        final String str = this.principal.toString();
        Assertions.assertNotNull(str);
        Assertions.assertTrue(str.contains("DOMAIN\\user1"));
        Assertions.assertTrue(str.contains("WaffleFqnPrincipal"));
    }

    /**
     * Test group fqns is unmodifiable.
     */
    @Test
    void testGroupFqnsIsUnmodifiable() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> this.principal.getGroupFqns().add("DOMAIN\\newgroup"));
    }
}
