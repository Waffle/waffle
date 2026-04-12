/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util;

import org.assertj.core.api.BDDSoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.mock.http.SimpleHttpRequest;

/**
 * The Class AuthorizationHeaderTest.
 */
class AuthorizationHeaderTest {

    /** The Constant DIGEST_HEADER. */
    private static final String DIGEST_HEADER = "Digest username=\"admin\", realm=\"milton\", nonce=\"YjNjZDgxNDYtOGIwMS00NDk0LTlkMTItYzExMGJkNTcxZjli\", uri=\"/case-user-data/431b971d9e1441d381adb277de4f39f8/test\", response=\"30d2d15e89e0b7596325a12852ae6ca5\", qop=auth, nc=00000025, cnonce=\"fb2f97a275d3d9cb\"";

    /**
     * Test get header with lowercase header name.
     */
    @Test
    void testGetHeaderWithLowercaseHeaderName() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertNull(header.getHeader());
        request.addHeader("authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        Assertions.assertNotNull(header.getHeader());
        Assertions.assertFalse(header.isNull());
    }

    /**
     * Test is null.
     */
    @Test
    void testIsNull() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertTrue(header.isNull());
        request.addHeader("Authorization", "");
        Assertions.assertTrue(header.isNull());
        request.addHeader("Authorization", "12344234");
        Assertions.assertFalse(header.isNull());
    }

    /**
     * Test get security package.
     */
    @Test
    void testGetSecurityPackage() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        Assertions.assertEquals("NTLM", header.getSecurityPackage());
        request.addHeader("Authorization",
                "Negotiate TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        Assertions.assertEquals("Negotiate", header.getSecurityPackage());
    }

    /**
     * Test is ntlm type1 message.
     */
    @Test
    void testIsNtlmType1Message() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertFalse(header.isNtlmType1Message());
        request.addHeader("Authorization", "");
        Assertions.assertFalse(header.isNtlmType1Message());
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        Assertions.assertTrue(header.isNtlmType1Message());
    }

    /**
     * Test is ntlm type1 post authorization header.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeader() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContentLength(0);
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        // GET
        request.setMethod("GET");
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertFalse(header.isNtlmType1PostAuthorizationHeader());
        // POST
        request.setMethod("POST");
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
        // PUT
        request.setMethod("PUT");
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is sp nego message.
     */
    @Test
    void testIsSPNegTokenInitMessage() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertFalse(header.isSPNegTokenInitMessage());
        request.addHeader("Authorization", "");
        Assertions.assertFalse(header.isSPNegTokenInitMessage());
        request.addHeader("Authorization",
                "Negotiate YHYGBisGAQUFAqBsMGqgMDAuBgorBgEEAYI3AgIKBgkqhkiC9xIBAgIGCSqGSIb3EgECAgYKKwYBBAGCNwICHqI2BDROVExNU1NQAAEAAACXsgjiAwADADEAAAAJAAkAKAAAAAYBsR0AAAAPR0xZQ0VSSU5FU0FE");
        Assertions.assertTrue(header.isSPNegTokenInitMessage());
    }

    /**
     * Test is sp nego post authorization header.
     */
    @Test
    void testIsSPNegoPostAuthorizationHeader() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContentLength(0);
        request.addHeader("Authorization",
                "Negotiate YHYGBisGAQUFAqBsMGqgMDAuBgorBgEEAYI3AgIKBgkqhkiC9xIBAgIGCSqGSIb3EgECAgYKKwYBBAGCNwICHqI2BDROVExNU1NQAAEAAACXsgjiAwADADEAAAAJAAkAKAAAAAYBsR0AAAAPR0xZQ0VSSU5FU0FE");
        // GET
        request.setMethod("GET");
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertFalse(header.isNtlmType1PostAuthorizationHeader());
        // POST
        request.setMethod("POST");
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
        // PUT
        request.setMethod("PUT");
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * This test was designed to specifically test a try/catch that was added around base64 processing to ensure that we
     * push out a more readable error condition when unsupported type is sent in. Specifically, this is testing the
     * Digest which is closely related to NTLM but not supported in Waffle.
     */
    @Test
    void testIsDigestAuthorizationHeaderFailure() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        request.addHeader("Authorization", AuthorizationHeaderTest.DIGEST_HEADER);

        final BDDSoftAssertions softly = new BDDSoftAssertions();
        softly.thenThrownBy(() -> header.getTokenBytes()).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid authorization header");
    }

    /**
     * Test is bearer authorization header returns true.
     */
    @Test
    void testIsBearerAuthorizationHeaderTrue() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.addHeader("Authorization", "Bearer sometoken");
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertTrue(header.isBearerAuthorizationHeader());
    }

    /**
     * Test is bearer authorization header returns false for NTLM.
     */
    @Test
    void testIsBearerAuthorizationHeaderFalseForNtlm() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertFalse(header.isBearerAuthorizationHeader());
    }

    /**
     * Test is bearer authorization header returns false when null.
     */
    @Test
    void testIsBearerAuthorizationHeaderFalseWhenNull() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertFalse(header.isBearerAuthorizationHeader());
    }

    /**
     * Test to string when null.
     */
    @Test
    void testToStringWhenNull() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertEquals("<none>", header.toString());
    }

    /**
     * Test to string when header present.
     */
    @Test
    void testToStringWhenHeaderPresent() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final String headerValue = "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==";
        request.addHeader("Authorization", headerValue);
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertEquals(headerValue, header.toString());
    }

    /**
     * Test is ntlm type1 post authorization header with DELETE method.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderWithDelete() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContentLength(0);
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        request.setMethod("DELETE");
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is ntlm type1 post authorization header with non-zero content length.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderWithContent() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContentLength(100);
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        request.setMethod("POST");
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertFalse(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test get security package throws when no space in header.
     */
    @Test
    void testGetSecurityPackageThrowsWhenNoSpace() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.addHeader("Authorization", "NoSpaceHere");
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertThrows(RuntimeException.class, header::getSecurityPackage);
    }

    /**
     * Test get security package throws when header is null.
     */
    @Test
    void testGetSecurityPackageThrowsWhenNull() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assertions.assertThrows(RuntimeException.class, header::getSecurityPackage);
    }
}
