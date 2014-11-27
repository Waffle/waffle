/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.googlecode.catchexception.CatchException;
import com.googlecode.catchexception.apis.BDDCatchException;

import waffle.mock.http.SimpleHttpRequest;

/**
 * @author dblock[at]dblock[dot]org
 */
public class AuthorizationHeaderTests {

    private static final String DIGEST_HEADER = "Digest username=\"admin\", realm=\"milton\", nonce=\"YjNjZDgxNDYtOGIwMS00NDk0LTlkMTItYzExMGJkNTcxZjli\", uri=\"/case-user-data/431b971d9e1441d381adb277de4f39f8/test\", response=\"30d2d15e89e0b7596325a12852ae6ca5\", qop=auth, nc=00000025, cnonce=\"fb2f97a275d3d9cb\"";

    @Test
    public void testIsNull() {
        SimpleHttpRequest request = new SimpleHttpRequest();
        AuthorizationHeader header = new AuthorizationHeader(request);
        assertTrue(header.isNull());
        request.addHeader("Authorization", "");
        assertTrue(header.isNull());
        request.addHeader("Authorization", "12344234");
        assertFalse(header.isNull());
    }

    @Test
    public void testGetSecurityPackage() {
        SimpleHttpRequest request = new SimpleHttpRequest();
        AuthorizationHeader header = new AuthorizationHeader(request);
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        assertEquals("NTLM", header.getSecurityPackage());
        request.addHeader("Authorization",
                "Negotiate TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        assertEquals("Negotiate", header.getSecurityPackage());
    }

    @Test
    public void testIsNtlmType1Message() {
        SimpleHttpRequest request = new SimpleHttpRequest();
        AuthorizationHeader header = new AuthorizationHeader(request);
        assertFalse(header.isNtlmType1Message());
        request.addHeader("Authorization", "");
        assertFalse(header.isNtlmType1Message());
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        assertTrue(header.isNtlmType1Message());
    }

    @Test
    public void testIsNtlmType1PostAuthorizationHeader() {
        SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContentLength(0);
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        // GET
        request.setMethod("GET");
        AuthorizationHeader header = new AuthorizationHeader(request);
        assertFalse(header.isNtlmType1PostAuthorizationHeader());
        // POST
        request.setMethod("POST");
        assertTrue(header.isNtlmType1PostAuthorizationHeader());
        // PUT
        request.setMethod("PUT");
        assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    @Test
    public void testIsSPNegoMessage() {
        SimpleHttpRequest request = new SimpleHttpRequest();
        AuthorizationHeader header = new AuthorizationHeader(request);
        assertFalse(header.isSPNegoMessage());
        request.addHeader("Authorization", "");
        assertFalse(header.isSPNegoMessage());
        request.addHeader(
                "Authorization",
                "Negotiate YHYGBisGAQUFAqBsMGqgMDAuBgorBgEEAYI3AgIKBgkqhkiC9xIBAgIGCSqGSIb3EgECAgYKKwYBBAGCNwICHqI2BDROVExNU1NQAAEAAACXsgjiAwADADEAAAAJAAkAKAAAAAYBsR0AAAAPR0xZQ0VSSU5FU0FE");
        assertTrue(header.isSPNegoMessage());
    }

    @Test
    public void testIsSPNegoPostAuthorizationHeader() {
        SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContentLength(0);
        request.addHeader(
                "Authorization",
                "Negotiate YHYGBisGAQUFAqBsMGqgMDAuBgorBgEEAYI3AgIKBgkqhkiC9xIBAgIGCSqGSIb3EgECAgYKKwYBBAGCNwICHqI2BDROVExNU1NQAAEAAACXsgjiAwADADEAAAAJAAkAKAAAAAYBsR0AAAAPR0xZQ0VSSU5FU0FE");
        // GET
        request.setMethod("GET");
        AuthorizationHeader header = new AuthorizationHeader(request);
        assertFalse(header.isNtlmType1PostAuthorizationHeader());
        // POST
        request.setMethod("POST");
        assertTrue(header.isNtlmType1PostAuthorizationHeader());
        // PUT
        request.setMethod("PUT");
        assertTrue(header.isNtlmType1PostAuthorizationHeader());
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
        request.addHeader("Authorization", DIGEST_HEADER);
        BDDCatchException.when(header).getTokenBytes();
        BDDCatchException.then(CatchException.caughtException()).isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid authorization header.");
    }
}
