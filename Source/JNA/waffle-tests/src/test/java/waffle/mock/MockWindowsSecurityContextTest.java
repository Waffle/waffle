/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * Tests for {@link MockWindowsSecurityContext}.
 */
class MockWindowsSecurityContextTest {

    /** The security context. */
    private MockWindowsSecurityContext securityContext;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.securityContext = new MockWindowsSecurityContext("DOMAIN\\testuser");
    }

    /**
     * Test get principal name.
     */
    @Test
    void testGetPrincipalName() {
        Assertions.assertEquals("DOMAIN\\testuser", this.securityContext.getPrincipalName());
    }

    /**
     * Test get security package.
     */
    @Test
    void testGetSecurityPackage() {
        Assertions.assertEquals("Mock", this.securityContext.getSecurityPackage());
    }

    /**
     * Test get token returns empty bytes.
     */
    @Test
    void testGetTokenReturnsEmptyBytes() {
        final byte[] token = this.securityContext.getToken();
        Assertions.assertNotNull(token);
        Assertions.assertEquals(0, token.length);
    }

    /**
     * Test is continue returns false.
     */
    @Test
    void testIsContinueReturnsFalse() {
        Assertions.assertFalse(this.securityContext.isContinue());
    }

    /**
     * Test get handle returns not null.
     */
    @Test
    void testGetHandleReturnsNotNull() {
        Assertions.assertNotNull(this.securityContext.getHandle());
    }

    /**
     * Test get identity.
     */
    @Test
    void testGetIdentity() {
        final IWindowsIdentity identity = this.securityContext.getIdentity();
        Assertions.assertNotNull(identity);
        Assertions.assertEquals("DOMAIN\\testuser", identity.getFqn());
    }

    /**
     * Test impersonate returns context.
     */
    @Test
    void testImpersonateReturnsContext() {
        final IWindowsImpersonationContext ctx = this.securityContext.impersonate();
        Assertions.assertNotNull(ctx);
        Assertions.assertDoesNotThrow(() -> ctx.revertToSelf());
    }

    /**
     * Test dispose does not throw.
     */
    @Test
    void testDisposeDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> this.securityContext.dispose());
    }

    /**
     * Test initialize no args does not throw.
     */
    @Test
    void testInitializeNoArgsDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> this.securityContext.initialize());
    }

    /**
     * Test initialize with args does not throw.
     */
    @Test
    void testInitializeWithArgsDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> this.securityContext.initialize(null, null, null));
    }
}
