/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet.spi;

import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.IWindowsAuthProvider;

/**
 * Tests for {@link BasicSecurityFilterProvider}.
 */
class BasicSecurityFilterProviderTest {

    /** The auth provider. */
    @Mocked
    private IWindowsAuthProvider auth;

    /** The response. */
    @Mocked
    private HttpServletResponse response;

    /** The request. */
    @Mocked
    private HttpServletRequest request;

    /** The provider. */
    private BasicSecurityFilterProvider provider;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.provider = new BasicSecurityFilterProvider(this.auth);
    }

    /**
     * Test get realm default.
     */
    @Test
    void testGetRealmDefault() {
        Assertions.assertEquals("BasicSecurityFilterProvider", this.provider.getRealm());
    }

    /**
     * Test set realm.
     */
    @Test
    void testSetRealm() {
        this.provider.setRealm("MyRealm");
        Assertions.assertEquals("MyRealm", this.provider.getRealm());
    }

    /**
     * Test is security package supported basic.
     */
    @Test
    void testIsSecurityPackageSupportedBasic() {
        Assertions.assertTrue(this.provider.isSecurityPackageSupported("Basic"));
    }

    /**
     * Test is security package supported case insensitive.
     */
    @Test
    void testIsSecurityPackageSupportedCaseInsensitive() {
        Assertions.assertTrue(this.provider.isSecurityPackageSupported("BASIC"));
        Assertions.assertTrue(this.provider.isSecurityPackageSupported("basic"));
    }

    /**
     * Test is security package supported unsupported.
     */
    @Test
    void testIsSecurityPackageSupportedUnsupported() {
        Assertions.assertFalse(this.provider.isSecurityPackageSupported("NTLM"));
        Assertions.assertFalse(this.provider.isSecurityPackageSupported("Negotiate"));
    }

    /**
     * Test is principal exception returns false.
     */
    @Test
    void testIsPrincipalExceptionReturnsFalse() {
        Assertions.assertFalse(this.provider.isPrincipalException(this.request));
    }

    /**
     * Test send unauthorized adds www-authenticate header.
     */
    @Test
    void testSendUnauthorized() {
        this.provider.sendUnauthorized(this.response);
        new Verifications() {
            {
                BasicSecurityFilterProviderTest.this.response.addHeader("WWW-Authenticate",
                        "Basic realm=\"BasicSecurityFilterProvider\"");
                this.times = 1;
            }
        };
    }

    /**
     * Test send unauthorized with custom realm.
     */
    @Test
    void testSendUnauthorizedWithCustomRealm() {
        this.provider.setRealm("CustomRealm");
        this.provider.sendUnauthorized(this.response);
        new Verifications() {
            {
                BasicSecurityFilterProviderTest.this.response.addHeader("WWW-Authenticate",
                        "Basic realm=\"CustomRealm\"");
                this.times = 1;
            }
        };
    }

    /**
     * Test init parameter realm.
     */
    @Test
    void testInitParameterRealm() {
        this.provider.initParameter("realm", "TestRealm");
        Assertions.assertEquals("TestRealm", this.provider.getRealm());
    }

    /**
     * Test init parameter unknown throws.
     */
    @Test
    void testInitParameterUnknownThrows() {
        Assertions.assertThrows(InvalidParameterException.class,
                () -> this.provider.initParameter("unknownParam", "value"));
    }

    /**
     * Test send unauthorized with empty realm expectation.
     */
    @Test
    void testSendUnauthorizedWithRealmViaInit() {
        new Expectations() {
            {
                // No expectations for response - just verifying via Verifications
            }
        };
        this.provider.initParameter("realm", "InitRealm");
        this.provider.sendUnauthorized(this.response);
        new Verifications() {
            {
                BasicSecurityFilterProviderTest.this.response.addHeader("WWW-Authenticate",
                        "Basic realm=\"InitRealm\"");
                this.times = 1;
            }
        };
    }
}
