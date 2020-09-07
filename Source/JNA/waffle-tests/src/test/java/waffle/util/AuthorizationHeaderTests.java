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
package waffle.util;

import org.assertj.core.api.BDDSoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.mock.http.SimpleHttpRequest;

/**
 * The Class AuthorizationHeaderTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class AuthorizationHeaderTests {

    /** The Constant DIGEST_HEADER. */
    private static final String DIGEST_HEADER = "Digest username=\"admin\", realm=\"milton\", nonce=\"YjNjZDgxNDYtOGIwMS00NDk0LTlkMTItYzExMGJkNTcxZjli\", uri=\"/case-user-data/431b971d9e1441d381adb277de4f39f8/test\", response=\"30d2d15e89e0b7596325a12852ae6ca5\", qop=auth, nc=00000025, cnonce=\"fb2f97a275d3d9cb\"";

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
        request.addHeader("Authorization", AuthorizationHeaderTests.DIGEST_HEADER);

        final BDDSoftAssertions softly = new BDDSoftAssertions();
        softly.thenThrownBy(() -> header.getTokenBytes()).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid authorization header");
    }
}
