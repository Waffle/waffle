/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * Tests for {@link MockWindowsAuthProvider}.
 */
class MockWindowsAuthProviderTest {

    /** The provider. */
    private MockWindowsAuthProvider provider;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.provider = new MockWindowsAuthProvider();
    }

    /**
     * Test get current computer returns null.
     */
    @Test
    void testGetCurrentComputerReturnsNull() {
        final IWindowsComputer computer = this.provider.getCurrentComputer();
        Assertions.assertNull(computer);
    }

    /**
     * Test get domains returns empty array.
     */
    @Test
    void testGetDomainsReturnsEmptyArray() {
        final IWindowsDomain[] domains = this.provider.getDomains();
        Assertions.assertNotNull(domains);
        Assertions.assertEquals(0, domains.length);
    }

    /**
     * Test logon domain user returns null.
     */
    @Test
    void testLogonDomainUserReturnsNull() {
        final IWindowsIdentity identity = this.provider.logonDomainUser("user", "domain", "password");
        Assertions.assertNull(identity);
    }

    /**
     * Test logon domain user ex returns null.
     */
    @Test
    void testLogonDomainUserExReturnsNull() {
        final IWindowsIdentity identity = this.provider.logonDomainUserEx("user", "domain", "password", 0, 0);
        Assertions.assertNull(identity);
    }

    /**
     * Test lookup account returns null.
     */
    @Test
    void testLookupAccountReturnsNull() {
        Assertions.assertNull(this.provider.lookupAccount("someuser"));
    }

    /**
     * Test reset security token does not throw.
     */
    @Test
    void testResetSecurityTokenDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> this.provider.resetSecurityToken("connection1"));
    }

    /**
     * Test add group.
     */
    @Test
    void testAddGroup() {
        this.provider.addGroup("Administrators");
        // acceptSecurityToken creates its own hardcoded context with 2 default groups (Users, Everyone);
        // addGroup affects logonUser, not acceptSecurityToken.
        final IWindowsSecurityContext ctx = this.provider.acceptSecurityToken("conn1",
                "testuser".getBytes(StandardCharsets.UTF_8), "Mock");
        final IWindowsIdentity identity = ctx.getIdentity();
        Assertions.assertEquals(2, identity.getGroups().length);
    }

    /**
     * Test accept security token returns non null.
     */
    @Test
    void testAcceptSecurityTokenReturnsNonNull() {
        final byte[] token = "testuser".getBytes(StandardCharsets.UTF_8);
        final IWindowsSecurityContext ctx = this.provider.acceptSecurityToken("connection1", token, "NTLM");
        Assertions.assertNotNull(ctx);
        Assertions.assertEquals("testuser", ctx.getIdentity().getFqn());
    }

    /**
     * Test accept security token uses token as username.
     */
    @Test
    void testAcceptSecurityTokenUsesTokenAsUsername() {
        final String username = "DOMAIN\\alice";
        final byte[] token = username.getBytes(StandardCharsets.UTF_8);
        final IWindowsSecurityContext ctx = this.provider.acceptSecurityToken("conn2", token, "Negotiate");
        Assertions.assertNotNull(ctx);
        Assertions.assertEquals(username, ctx.getIdentity().getFqn());
    }

    /**
     * Test initial groups contain users and everyone.
     */
    @Test
    void testInitialGroupsContainUsersAndEveryone() {
        final IWindowsSecurityContext ctx = this.provider.acceptSecurityToken("conn3",
                "anyuser".getBytes(StandardCharsets.UTF_8), "NTLM");
        final IWindowsIdentity identity = ctx.getIdentity();
        final List<String> groupNames = new java.util.ArrayList<>();
        for (final waffle.windows.auth.IWindowsAccount g : identity.getGroups()) {
            groupNames.add(g.getFqn());
        }
        Assertions.assertTrue(groupNames.contains("Users"));
        Assertions.assertTrue(groupNames.contains("Everyone"));
    }

}
