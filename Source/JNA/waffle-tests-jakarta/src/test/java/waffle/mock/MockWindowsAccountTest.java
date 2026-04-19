/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MockWindowsAccount}.
 */
class MockWindowsAccountTest {

    /**
     * Test fqn with domain.
     */
    @Test
    void testFqnWithDomain() {
        final MockWindowsAccount account = new MockWindowsAccount("DOMAIN\\user1");
        Assertions.assertEquals("DOMAIN\\user1", account.getFqn());
        Assertions.assertEquals("DOMAIN", account.getDomain());
        Assertions.assertEquals("user1", account.getName());
        Assertions.assertNotNull(account.getSidString());
    }

    /**
     * Test fqn without domain separator.
     */
    @Test
    void testFqnWithoutDomain() {
        final MockWindowsAccount account = new MockWindowsAccount("simpleuser");
        Assertions.assertEquals("simpleuser", account.getFqn());
        Assertions.assertNull(account.getDomain());
        Assertions.assertEquals("simpleuser", account.getName());
        Assertions.assertNotNull(account.getSidString());
    }

    /**
     * Test constructor with explicit SID.
     */
    @Test
    void testConstructorWithExplicitSid() {
        final MockWindowsAccount account = new MockWindowsAccount("DOMAIN\\user1", "S-1-5-21-999");
        Assertions.assertEquals("DOMAIN\\user1", account.getFqn());
        Assertions.assertEquals("S-1-5-21-999", account.getSidString());
    }

    /**
     * Test constants are accessible.
     */
    @Test
    void testConstants() {
        Assertions.assertNotNull(MockWindowsAccount.TEST_USER_NAME);
        Assertions.assertNotNull(MockWindowsAccount.TEST_PASSWORD);
    }
}
