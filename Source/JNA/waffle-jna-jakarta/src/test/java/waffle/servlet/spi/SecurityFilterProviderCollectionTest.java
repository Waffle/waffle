/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.servlet.spi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * The Class SecurityFilterProviderCollectionTest.
 *
 * @author dblock[at]dblock[dot]org
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
