/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util;

import javax.servlet.http.HttpServletRequest;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link NtlmServletRequest}.
 */
class NtlmServletRequestTest {

    /** The mocked HTTP servlet request. */
    @Mocked
    private HttpServletRequest request;

    /**
     * Test get connection id uses remote host when available.
     */
    @Test
    void testGetConnectionIdUsesRemoteHost() {
        new Expectations() {
            {
                NtlmServletRequestTest.this.request.getRemoteHost();
                this.result = "hostname.example.com";
                NtlmServletRequestTest.this.request.getRemotePort();
                this.result = 8080;
            }
        };
        Assertions.assertEquals("hostname.example.com:8080", NtlmServletRequest.getConnectionId(this.request));
    }

    /**
     * Test get connection id falls back to remote addr when remote host is null.
     */
    @Test
    void testGetConnectionIdFallsBackToRemoteAddr() {
        new Expectations() {
            {
                NtlmServletRequestTest.this.request.getRemoteHost();
                this.result = null;
                NtlmServletRequestTest.this.request.getRemoteAddr();
                this.result = "192.168.1.100";
                NtlmServletRequestTest.this.request.getRemotePort();
                this.result = 12345;
            }
        };
        Assertions.assertEquals("192.168.1.100:12345", NtlmServletRequest.getConnectionId(this.request));
    }

    /**
     * Test get connection id with null remote host and null remote addr uses empty string.
     */
    @Test
    void testGetConnectionIdWithBothHostAndAddrNull() {
        new Expectations() {
            {
                NtlmServletRequestTest.this.request.getRemoteHost();
                this.result = null;
                NtlmServletRequestTest.this.request.getRemoteAddr();
                this.result = null;
                NtlmServletRequestTest.this.request.getRemotePort();
                this.result = 9000;
            }
        };
        Assertions.assertEquals(":9000", NtlmServletRequest.getConnectionId(this.request));
    }

    /**
     * Test get connection id format is host colon port.
     */
    @Test
    void testGetConnectionIdFormat() {
        new Expectations() {
            {
                NtlmServletRequestTest.this.request.getRemoteHost();
                this.result = "client.local";
                NtlmServletRequestTest.this.request.getRemotePort();
                this.result = 4321;
            }
        };
        final String connectionId = NtlmServletRequest.getConnectionId(this.request);
        Assertions.assertTrue(connectionId.contains(":"));
        Assertions.assertTrue(connectionId.startsWith("client.local"));
        Assertions.assertTrue(connectionId.endsWith("4321"));
    }

}
