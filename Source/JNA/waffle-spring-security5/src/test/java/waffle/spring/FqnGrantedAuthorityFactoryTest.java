/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.WindowsAccount;

/**
 * The Class FqnGrantedAuthorityFactoryTest.
 */
class FqnGrantedAuthorityFactoryTest {

    /** The group. */
    private WindowsAccount group;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.group = new WindowsAccount(new MockWindowsAccount("group"));
    }

    /**
     * Test prefix and uppercase.
     */
    @Test
    void testPrefixAndUppercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", true);
        Assertions.assertEquals(new SimpleGrantedAuthority("PREFIX_GROUP"), factory.createGrantedAuthority(this.group));
    }

    /**
     * Test prefix and lowercase.
     */
    @Test
    void testPrefixAndLowercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", false);
        Assertions.assertEquals(new SimpleGrantedAuthority("prefix_group"), factory.createGrantedAuthority(this.group));
    }

    /**
     * Test no prefix and uppercase.
     */
    @Test
    void testNoPrefixAndUppercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, true);
        Assertions.assertEquals(new SimpleGrantedAuthority("GROUP"), factory.createGrantedAuthority(this.group));
    }

    /**
     * Test no prefix and lowercase.
     */
    @Test
    void testNoPrefixAndLowercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, false);
        Assertions.assertEquals(new SimpleGrantedAuthority("group"), factory.createGrantedAuthority(this.group));
    }

}
