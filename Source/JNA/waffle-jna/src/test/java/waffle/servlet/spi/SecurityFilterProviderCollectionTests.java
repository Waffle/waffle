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
package waffle.servlet.spi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * The Class SecurityFilterProviderCollectionTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class SecurityFilterProviderCollectionTests {

    /**
     * Test default collection.
     *
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    @Test
    public void testDefaultCollection() throws ClassNotFoundException {
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
    public void testGetByClassNameInvalid() throws ClassNotFoundException {
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
    public void testIsSecurityPackageSupported() {
        final SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(
                new WindowsAuthProviderImpl());
        Assertions.assertTrue(coll.isSecurityPackageSupported("NTLM"));
        Assertions.assertTrue(coll.isSecurityPackageSupported("Negotiate"));
        Assertions.assertTrue(coll.isSecurityPackageSupported("Basic"));
        Assertions.assertFalse(coll.isSecurityPackageSupported(""));
        Assertions.assertFalse(coll.isSecurityPackageSupported("Invalid"));
    }
}
