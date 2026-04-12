/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.shiro.negotiate;

import java.security.Principal;

import javax.security.auth.Subject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link NegotiateToken}.
 */
class NegotiateTokenTest {

    /** The token. */
    private NegotiateToken token;

    /** The in bytes. */
    private static final byte[] IN_BYTES = { 0x01, 0x02, 0x03 };

    /** The out bytes. */
    private static final byte[] OUT_BYTES = { 0x04, 0x05, 0x06 };

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.token = new NegotiateToken(NegotiateTokenTest.IN_BYTES, NegotiateTokenTest.OUT_BYTES, "conn-001",
                "Negotiate", false, false, "localhost");
    }

    /**
     * Test get connection id.
     */
    @Test
    void testGetConnectionId() {
        Assertions.assertEquals("conn-001", this.token.getConnectionId());
    }

    /**
     * Test get security package.
     */
    @Test
    void testGetSecurityPackage() {
        Assertions.assertEquals("Negotiate", this.token.getSecurityPackage());
    }

    /**
     * Test is ntlm post false.
     */
    @Test
    void testIsNtlmPostFalse() {
        Assertions.assertFalse(this.token.isNtlmPost());
    }

    /**
     * Test is ntlm post true.
     */
    @Test
    void testIsNtlmPostTrue() {
        final NegotiateToken ntlmToken = new NegotiateToken(NegotiateTokenTest.IN_BYTES, NegotiateTokenTest.OUT_BYTES,
                "conn-001", "NTLM", true, false, null);
        Assertions.assertTrue(ntlmToken.isNtlmPost());
    }

    /**
     * Test get in.
     */
    @Test
    void testGetIn() {
        final byte[] in = this.token.getIn();
        Assertions.assertNotNull(in);
        Assertions.assertArrayEquals(NegotiateTokenTest.IN_BYTES, in);
    }

    /**
     * Test get in returns clone.
     */
    @Test
    void testGetInReturnsClone() {
        final byte[] in1 = this.token.getIn();
        final byte[] in2 = this.token.getIn();
        Assertions.assertNotSame(in1, in2);
    }

    /**
     * Test set out.
     */
    @Test
    void testSetOut() {
        final byte[] newOut = { 0x07, 0x08 };
        this.token.setOut(newOut);
        Assertions.assertArrayEquals(newOut, this.token.getOut());
    }

    /**
     * Test set out null.
     */
    @Test
    void testSetOutNull() {
        this.token.setOut(null);
        Assertions.assertNull(this.token.getOut());
    }

    /**
     * Test set and get subject.
     */
    @Test
    void testSetAndGetSubject() {
        final Subject subject = new Subject();
        this.token.setSubject(subject);
        Assertions.assertEquals(subject, this.token.getSubject());
        Assertions.assertEquals(subject, this.token.getCredentials());
    }

    /**
     * Test set and get principal.
     */
    @Test
    void testSetAndGetPrincipal() {
        final Principal principal = () -> "testprincipal";
        this.token.setPrincipal(principal);
        Assertions.assertEquals(principal, this.token.getPrincipal());
    }

    /**
     * Test is remember me false.
     */
    @Test
    void testIsRememberMeFalse() {
        Assertions.assertFalse(this.token.isRememberMe());
    }

    /**
     * Test is remember me true.
     */
    @Test
    void testIsRememberMeTrue() {
        final NegotiateToken rememberToken = new NegotiateToken(NegotiateTokenTest.IN_BYTES,
                NegotiateTokenTest.OUT_BYTES, "conn-001", "Negotiate", false, true, null);
        Assertions.assertTrue(rememberToken.isRememberMe());
    }

    /**
     * Test get host.
     */
    @Test
    void testGetHost() {
        Assertions.assertEquals("localhost", this.token.getHost());
    }

    /**
     * Test get host null.
     */
    @Test
    void testGetHostNull() {
        final NegotiateToken nullHostToken = new NegotiateToken(NegotiateTokenTest.IN_BYTES,
                NegotiateTokenTest.OUT_BYTES, "conn-001", "Negotiate", false, false, null);
        Assertions.assertNull(nullHostToken.getHost());
    }

    /**
     * Test create info.
     */
    @Test
    void testCreateInfo() {
        final java.util.Set<java.security.Principal> principals = new java.util.HashSet<>();
        principals.add(() -> "testUser");
        final Subject subject = new Subject(false, principals, new java.util.HashSet<>(), new java.util.HashSet<>());
        this.token.setSubject(subject);
        final org.apache.shiro.authc.AuthenticationInfo info = this.token.createInfo();
        Assertions.assertNotNull(info);
        Assertions.assertNotNull(info.getCredentials());
        Assertions.assertNotNull(info.getPrincipals());
    }
}
