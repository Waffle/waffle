/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.shiro.negotiate;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link NegotiateInfo}.
 */
class NegotiateInfoTest {

    /**
     * Creates a subject with a principal for testing.
     *
     * @return a subject with a test principal
     */
    private static Subject createSubjectWithPrincipal() {
        final Set<Principal> principals = new HashSet<>();
        principals.add(() -> "testUser");
        return new Subject(false, principals, new HashSet<>(), new HashSet<>());
    }

    /**
     * Test get principals.
     */
    @Test
    void testGetPrincipals() {
        final Subject subject = NegotiateInfoTest.createSubjectWithPrincipal();
        final NegotiateInfo info = new NegotiateInfo(subject, "TestRealm");
        Assertions.assertNotNull(info.getPrincipals());
    }

    /**
     * Test get credentials.
     */
    @Test
    void testGetCredentials() {
        final Subject subject = NegotiateInfoTest.createSubjectWithPrincipal();
        final NegotiateInfo info = new NegotiateInfo(subject, "TestRealm");
        Assertions.assertEquals(subject, info.getCredentials());
    }

    /**
     * Test principals realm name.
     */
    @Test
    void testPrincipalsRealmName() {
        final Subject subject = NegotiateInfoTest.createSubjectWithPrincipal();
        final NegotiateInfo info = new NegotiateInfo(subject, "TestRealm");
        final org.apache.shiro.subject.PrincipalCollection principals = info.getPrincipals();
        Assertions.assertNotNull(principals.getRealmNames());
        Assertions.assertTrue(principals.getRealmNames().contains("TestRealm"));
    }
}
