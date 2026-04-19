/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SimpleHttpSession}.
 */
class SimpleHttpSessionTest {

    /** The session. */
    private SimpleHttpSession session;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.session = new SimpleHttpSession();
    }

    /**
     * Test set and get attribute.
     */
    @Test
    void testSetAndGetAttribute() {
        this.session.setAttribute("key", "value");
        Assertions.assertEquals("value", this.session.getAttribute("key"));
    }

    /**
     * Test get attribute returns null when not set.
     */
    @Test
    void testGetAttributeNullWhenNotSet() {
        Assertions.assertNull(this.session.getAttribute("nonexistent"));
    }

    /**
     * Test remove attribute.
     */
    @Test
    void testRemoveAttribute() {
        this.session.setAttribute("key", "value");
        this.session.removeAttribute("key");
        Assertions.assertNull(this.session.getAttribute("key"));
    }

    /**
     * Test get attribute names returns null.
     */
    @Test
    void testGetAttributeNamesReturnsNull() {
        Assertions.assertNull(this.session.getAttributeNames());
    }

    /**
     * Test get creation time returns zero.
     */
    @Test
    void testGetCreationTimeReturnsZero() {
        Assertions.assertEquals(0L, this.session.getCreationTime());
    }

    /**
     * Test get id returns null.
     */
    @Test
    void testGetIdReturnsNull() {
        Assertions.assertNull(this.session.getId());
    }

    /**
     * Test get last accessed time returns zero.
     */
    @Test
    void testGetLastAccessedTimeReturnsZero() {
        Assertions.assertEquals(0L, this.session.getLastAccessedTime());
    }

    /**
     * Test get max inactive interval returns zero.
     */
    @Test
    void testGetMaxInactiveIntervalReturnsZero() {
        Assertions.assertEquals(0, this.session.getMaxInactiveInterval());
    }

    /**
     * Test get servlet context returns null.
     */
    @Test
    void testGetServletContextReturnsNull() {
        Assertions.assertNull(this.session.getServletContext());
    }

    /**
     * Test invalidate does not throw.
     */
    @Test
    void testInvalidateDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> this.session.invalidate());
    }

    /**
     * Test is new returns false.
     */
    @Test
    void testIsNewReturnsFalse() {
        Assertions.assertFalse(this.session.isNew());
    }

    /**
     * Test set max inactive interval does not throw.
     */
    @Test
    void testSetMaxInactiveIntervalDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> this.session.setMaxInactiveInterval(300));
    }
}
