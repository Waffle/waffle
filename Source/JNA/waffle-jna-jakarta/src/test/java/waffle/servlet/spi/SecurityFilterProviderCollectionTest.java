/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet.spi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * The Class SecurityFilterProviderCollectionTest.
 */
class SecurityFilterProviderCollectionTest {

    /**
     * Test default collection.
     *
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    @Test
    void testDefaultCollection() throws ClassNotFoundException {
        final SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(
                new WindowsAuthProviderImpl());
        Assertions.assertEquals(2, coll.size());
        Assertions.assertNotNull(coll.getByClassName(NegotiateSecurityFilterProvider.class.getName()));
        Assertions.assertNotNull(coll.getByClassName(BasicSecurityFilterProvider.class.getName()));
    }

    /**
     * Test get by class name invalid.
     *
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    @Test
    void testGetByClassNameInvalid() throws ClassNotFoundException {
        final SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(
                new WindowsAuthProviderImpl());
        Assertions.assertThrows(ClassNotFoundException.class, () -> {
            coll.getByClassName("classDoesNotExist");
        });
    }

    /**
     * Test is security package supported.
     */
    @Test
    void testIsSecurityPackageSupported() {
        final SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(
                new WindowsAuthProviderImpl());
        Assertions.assertTrue(coll.isSecurityPackageSupported("NTLM"));
        Assertions.assertTrue(coll.isSecurityPackageSupported("Negotiate"));
        Assertions.assertTrue(coll.isSecurityPackageSupported("Basic"));
        Assertions.assertFalse(coll.isSecurityPackageSupported(""));
        Assertions.assertFalse(coll.isSecurityPackageSupported("Invalid"));
    }
}
