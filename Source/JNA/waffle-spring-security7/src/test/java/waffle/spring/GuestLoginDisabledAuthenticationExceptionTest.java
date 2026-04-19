/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link GuestLoginDisabledAuthenticationException}.
 */
class GuestLoginDisabledAuthenticationExceptionTest {

    /**
     * Test message is preserved.
     */
    @Test
    void testMessage() {
        final GuestLoginDisabledAuthenticationException ex = new GuestLoginDisabledAuthenticationException("Guest");
        Assertions.assertEquals("Guest", ex.getMessage());
    }

    /**
     * Test that it is an AuthenticationException.
     */
    @Test
    void testIsAuthenticationException() {
        final GuestLoginDisabledAuthenticationException ex = new GuestLoginDisabledAuthenticationException("test");
        Assertions.assertInstanceOf(org.springframework.security.core.AuthenticationException.class, ex);
    }

    /**
     * Test with null message.
     */
    @Test
    void testNullMessage() {
        final GuestLoginDisabledAuthenticationException ex = new GuestLoginDisabledAuthenticationException(null);
        Assertions.assertNull(ex.getMessage());
    }
}
