/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.util;

import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Assert;
import org.junit.Test;

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
    public void testIsNull() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assert.assertTrue(header.isNull());
        request.addHeader("Authorization", "");
        Assert.assertTrue(header.isNull());
        request.addHeader("Authorization", "12344234");
        Assert.assertFalse(header.isNull());
    }

    /**
     * Test get security package.
     */
    @Test
    public void testGetSecurityPackage() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        Assert.assertEquals("NTLM", header.getSecurityPackage());
        request.addHeader("Authorization",
                "Negotiate TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        Assert.assertEquals("Negotiate", header.getSecurityPackage());
    }

    /**
     * Test is ntlm type1 message.
     */
    @Test
    public void testIsNtlmType1Message() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assert.assertFalse(header.isNtlmType1Message());
        request.addHeader("Authorization", "");
        Assert.assertFalse(header.isNtlmType1Message());
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        Assert.assertTrue(header.isNtlmType1Message());
    }

    /**
     * Test is ntlm type1 post authorization header.
     */
    @Test
    public void testIsNtlmType1PostAuthorizationHeader() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContentLength(0);
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        // GET
        request.setMethod("GET");
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assert.assertFalse(header.isNtlmType1PostAuthorizationHeader());
        // POST
        request.setMethod("POST");
        Assert.assertTrue(header.isNtlmType1PostAuthorizationHeader());
        // PUT
        request.setMethod("PUT");
        Assert.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is sp nego message.
     */
    @Test
    public void testIsSPNegTokenInitMessage() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assert.assertFalse(header.isSPNegTokenInitMessage());
        request.addHeader("Authorization", "");
        Assert.assertFalse(header.isSPNegTokenInitMessage());
        request.addHeader(
                "Authorization",
                "Negotiate YHYGBisGAQUFAqBsMGqgMDAuBgorBgEEAYI3AgIKBgkqhkiC9xIBAgIGCSqGSIb3EgECAgYKKwYBBAGCNwICHqI2BDROVExNU1NQAAEAAACXsgjiAwADADEAAAAJAAkAKAAAAAYBsR0AAAAPR0xZQ0VSSU5FU0FE");
        Assert.assertTrue(header.isSPNegTokenInitMessage());
    }

    /**
     * Test is sp nego post authorization header.
     */
    @Test
    public void testIsSPNegoPostAuthorizationHeader() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContentLength(0);
        request.addHeader(
                "Authorization",
                "Negotiate YHYGBisGAQUFAqBsMGqgMDAuBgorBgEEAYI3AgIKBgkqhkiC9xIBAgIGCSqGSIb3EgECAgYKKwYBBAGCNwICHqI2BDROVExNU1NQAAEAAACXsgjiAwADADEAAAAJAAkAKAAAAAYBsR0AAAAPR0xZQ0VSSU5FU0FE");
        // GET
        request.setMethod("GET");
        final AuthorizationHeader header = new AuthorizationHeader(request);
        Assert.assertFalse(header.isNtlmType1PostAuthorizationHeader());
        // POST
        request.setMethod("POST");
        Assert.assertTrue(header.isNtlmType1PostAuthorizationHeader());
        // PUT
        request.setMethod("PUT");
        Assert.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * This test was designed to specifically test a try/catch that was added around base64 processing to ensure that we
     * push out a more readable error condition when unsupported type is sent in. Specifically, this is testing the
     * Digest which is closely related to NTLM but not supported in Waffle.
     */
    @Test
    public void testIsDigestAuthorizationHeaderFailure() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final AuthorizationHeader header = new AuthorizationHeader(request);
        request.addHeader("Authorization", AuthorizationHeaderTests.DIGEST_HEADER);

        final BDDSoftAssertions softly = new BDDSoftAssertions();
        softly.thenThrownBy(new ThrowingCallable() {

            @Override
            public void call() throws Exception {
                header.getTokenBytes();
            }
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("Invalid authorization header");
    }
}
