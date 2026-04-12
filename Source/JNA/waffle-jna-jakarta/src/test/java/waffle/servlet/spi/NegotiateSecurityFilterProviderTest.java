/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet.spi;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import mockit.Mocked;
import mockit.Verifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.IWindowsAuthProvider;

/**
 * Tests for {@link NegotiateSecurityFilterProvider}.
 */
class NegotiateSecurityFilterProviderTest {

    /** The auth provider. */
    @Mocked
    private IWindowsAuthProvider auth;

    /** The response. */
    @Mocked
    private HttpServletResponse response;

    /** The provider. */
    private NegotiateSecurityFilterProvider provider;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.provider = new NegotiateSecurityFilterProvider(this.auth);
    }

    /**
     * Test get protocols default.
     */
    @Test
    void testGetProtocolsDefault() {
        final List<String> protocols = this.provider.getProtocols();
        Assertions.assertEquals(2, protocols.size());
        Assertions.assertTrue(protocols.contains("Negotiate"));
        Assertions.assertTrue(protocols.contains("NTLM"));
    }

    /**
     * Test set protocols.
     */
    @Test
    void testSetProtocols() {
        final List<String> newProtocols = Arrays.asList("NTLM");
        this.provider.setProtocols(newProtocols);
        Assertions.assertEquals(1, this.provider.getProtocols().size());
        Assertions.assertTrue(this.provider.getProtocols().contains("NTLM"));
    }

    /**
     * Test is security package supported negotiate.
     */
    @Test
    void testIsSecurityPackageSupportedNegotiate() {
        Assertions.assertTrue(this.provider.isSecurityPackageSupported("Negotiate"));
        Assertions.assertTrue(this.provider.isSecurityPackageSupported("negotiate"));
        Assertions.assertTrue(this.provider.isSecurityPackageSupported("NEGOTIATE"));
    }

    /**
     * Test is security package supported ntlm.
     */
    @Test
    void testIsSecurityPackageSupportedNtlm() {
        Assertions.assertTrue(this.provider.isSecurityPackageSupported("NTLM"));
        Assertions.assertTrue(this.provider.isSecurityPackageSupported("ntlm"));
    }

    /**
     * Test is security package supported unsupported.
     */
    @Test
    void testIsSecurityPackageSupportedUnsupported() {
        Assertions.assertFalse(this.provider.isSecurityPackageSupported("Basic"));
        Assertions.assertFalse(this.provider.isSecurityPackageSupported("Bearer"));
    }

    /**
     * Test send unauthorized adds both negotiate and ntlm headers.
     */
    @Test
    void testSendUnauthorized() {
        this.provider.sendUnauthorized(this.response);
        new Verifications() {
            {
                NegotiateSecurityFilterProviderTest.this.response.addHeader("WWW-Authenticate", "Negotiate");
                this.times = 1;
                NegotiateSecurityFilterProviderTest.this.response.addHeader("WWW-Authenticate", "NTLM");
                this.times = 1;
            }
        };
    }

    /**
     * Test send unauthorized with custom protocols.
     */
    @Test
    void testSendUnauthorizedWithCustomProtocols() {
        this.provider.setProtocols(Arrays.asList("Negotiate"));
        this.provider.sendUnauthorized(this.response);
        new Verifications() {
            {
                NegotiateSecurityFilterProviderTest.this.response.addHeader("WWW-Authenticate", "Negotiate");
                this.times = 1;
                NegotiateSecurityFilterProviderTest.this.response.addHeader("WWW-Authenticate", "NTLM");
                this.times = 0;
            }
        };
    }

    /**
     * Test init parameter protocols.
     */
    @Test
    void testInitParameterProtocols() {
        this.provider.initParameter("protocols", "NTLM");
        Assertions.assertEquals(1, this.provider.getProtocols().size());
        Assertions.assertTrue(this.provider.getProtocols().contains("NTLM"));
    }

    /**
     * Test init parameter protocols whitespace separated.
     */
    @Test
    void testInitParameterProtocolsWhitespaceSeparated() {
        this.provider.initParameter("protocols", "Negotiate NTLM");
        Assertions.assertEquals(2, this.provider.getProtocols().size());
    }

    /**
     * Test init parameter unsupported protocol throws.
     */
    @Test
    void testInitParameterUnsupportedProtocolThrows() {
        Assertions.assertThrows(RuntimeException.class, () -> this.provider.initParameter("protocols", "Basic"));
    }

    /**
     * Test init parameter unknown parameter throws.
     */
    @Test
    void testInitParameterUnknownParameterThrows() {
        Assertions.assertThrows(Exception.class, () -> this.provider.initParameter("unknownParam", "value"));
    }
}
