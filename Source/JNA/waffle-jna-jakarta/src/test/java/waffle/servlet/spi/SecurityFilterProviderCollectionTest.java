/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet.spi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mockit.Mocked;
import mockit.Verifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * The Class SecurityFilterProviderCollectionTest.
 */
class SecurityFilterProviderCollectionTest {

    /** The response. */
    @Mocked
    private HttpServletResponse response;

    /** The request. */
    @Mocked
    private HttpServletRequest request;

    /** The mock auth. */
    @Mocked
    private IWindowsAuthProvider mockAuth;

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

    /**
     * Test send unauthorized calls providers.
     */
    @Test
    void testSendUnauthorized() {
        final SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(this.mockAuth);
        coll.sendUnauthorized(this.response);
        new Verifications() {
            {
                // Both negotiate and basic providers should add headers
                SecurityFilterProviderCollectionTest.this.response.addHeader("WWW-Authenticate", anyString);
                this.minTimes = 1;
            }
        };
    }

    /**
     * Test constructor with provider array.
     *
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    @Test
    void testConstructorWithProviderArray() throws ClassNotFoundException {
        final NegotiateSecurityFilterProvider negotiateProvider = new NegotiateSecurityFilterProvider(this.mockAuth);
        final BasicSecurityFilterProvider basicProvider = new BasicSecurityFilterProvider(this.mockAuth);
        final SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(
                new SecurityFilterProvider[] { negotiateProvider, basicProvider });
        Assertions.assertEquals(2, coll.size());
        Assertions.assertNotNull(coll.getByClassName(NegotiateSecurityFilterProvider.class.getName()));
        Assertions.assertNotNull(coll.getByClassName(BasicSecurityFilterProvider.class.getName()));
    }

    /**
     * Test constructor with class names.
     */
    @Test
    void testConstructorWithClassNames() {
        final String[] providerNames = { NegotiateSecurityFilterProvider.class.getName(),
                BasicSecurityFilterProvider.class.getName() };
        final SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(providerNames,
                this.mockAuth);
        Assertions.assertEquals(2, coll.size());
    }

    /**
     * Test constructor with invalid class name.
     */
    @Test
    void testConstructorWithInvalidClassName() {
        final String[] providerNames = { "com.invalid.NonExistentClass" };
        Assertions.assertThrows(RuntimeException.class,
                () -> new SecurityFilterProviderCollection(providerNames, this.mockAuth));
    }

    /**
     * Test is principal exception.
     */
    @Test
    void testIsPrincipalException() {
        final SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(this.mockAuth);
        // Basic provider always returns false; Negotiate checks for NTLM POST
        // With no authorization header set, result is false
        Assertions.assertFalse(coll.isPrincipalException(this.request));
    }
}
