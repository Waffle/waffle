/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.WindowsAccount;

/**
 * The Class FqnGrantedAuthorityFactoryTests.
 */
public class FqnGrantedAuthorityFactoryTests {

    /** The group. */
    private WindowsAccount group;

    /**
     * Sets the up.
     */
    @BeforeEach
    public void setUp() {
        this.group = new WindowsAccount(new MockWindowsAccount("group"));
    }

    /**
     * Test prefix and uppercase.
     */
    @Test
    public void testPrefixAndUppercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", true);
        Assertions.assertEquals(new SimpleGrantedAuthority("PREFIX_GROUP"), factory.createGrantedAuthority(this.group));
    }

    /**
     * Test prefix and lowercase.
     */
    @Test
    public void testPrefixAndLowercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", false);
        Assertions.assertEquals(new SimpleGrantedAuthority("prefix_group"), factory.createGrantedAuthority(this.group));
    }

    /**
     * Test no prefix and uppercase.
     */
    @Test
    public void testNoPrefixAndUppercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, true);
        Assertions.assertEquals(new SimpleGrantedAuthority("GROUP"), factory.createGrantedAuthority(this.group));
    }

    /**
     * Test no prefix and lowercase.
     */
    @Test
    public void testNoPrefixAndLowercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, false);
        Assertions.assertEquals(new SimpleGrantedAuthority("group"), factory.createGrantedAuthority(this.group));
    }

}
