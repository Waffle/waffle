/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link WindowsAccount} value object.
 */
class WindowsAccountValueTest {

    /** The windows account mock. */
    @Mocked
    private IWindowsAccount mockAccount;

    /** The windows account under test. */
    private WindowsAccount windowsAccount;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsAccountValueTest.this.mockAccount.getSidString();
                this.result = "S-1-5-21-12345";
                WindowsAccountValueTest.this.mockAccount.getFqn();
                this.result = "DOMAIN\\testuser";
                WindowsAccountValueTest.this.mockAccount.getName();
                this.result = "testuser";
                WindowsAccountValueTest.this.mockAccount.getDomain();
                this.result = "DOMAIN";
            }
        });
        this.windowsAccount = new WindowsAccount(this.mockAccount);
    }

    /**
     * Test get sid string.
     */
    @Test
    void testGetSidString() {
        Assertions.assertEquals("S-1-5-21-12345", this.windowsAccount.getSidString());
    }

    /**
     * Test get fqn.
     */
    @Test
    void testGetFqn() {
        Assertions.assertEquals("DOMAIN\\testuser", this.windowsAccount.getFqn());
    }

    /**
     * Test get name.
     */
    @Test
    void testGetName() {
        Assertions.assertEquals("testuser", this.windowsAccount.getName());
    }

    /**
     * Test get domain.
     */
    @Test
    void testGetDomain() {
        Assertions.assertEquals("DOMAIN", this.windowsAccount.getDomain());
    }

    /**
     * Test equals same sid.
     */
    @Test
    void testEqualsSameSid() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsAccountValueTest.this.mockAccount.getSidString();
                this.result = "S-1-5-21-12345";
                WindowsAccountValueTest.this.mockAccount.getFqn();
                this.result = "DOMAIN\\other";
                WindowsAccountValueTest.this.mockAccount.getName();
                this.result = "other";
                WindowsAccountValueTest.this.mockAccount.getDomain();
                this.result = "DOMAIN";
            }
        });
        final WindowsAccount other = new WindowsAccount(this.mockAccount);
        Assertions.assertEquals(this.windowsAccount, other);
        Assertions.assertEquals(this.windowsAccount.hashCode(), other.hashCode());
    }

    /**
     * Test equals self.
     */
    @Test
    void testEqualsSelf() {
        Assertions.assertEquals(this.windowsAccount, this.windowsAccount);
    }

    /**
     * Test not equals null.
     */
    @Test
    void testNotEqualsNull() {
        Assertions.assertNotEquals(null, this.windowsAccount);
    }

    /**
     * Test not equals different type.
     */
    @Test
    void testNotEqualsDifferentType() {
        Assertions.assertNotEquals("string", this.windowsAccount);
    }
}
