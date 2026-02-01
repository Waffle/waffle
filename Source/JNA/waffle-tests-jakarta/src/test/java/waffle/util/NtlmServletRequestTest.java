/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.mock.http.SimpleHttpRequest;

/**
 * The Class NtlmServletRequestTest.
 */
class NtlmServletRequestTest {

    /**
     * Test get connection id.
     */
    @Test
    void testGetConnectionId() {
        SimpleHttpRequest.resetRemotePort();
        final SimpleHttpRequest request1 = new SimpleHttpRequest();
        Assertions.assertEquals(":1", NtlmServletRequest.getConnectionId(request1));
        final SimpleHttpRequest request2 = new SimpleHttpRequest();
        Assertions.assertEquals(":2", NtlmServletRequest.getConnectionId(request2));
        request2.setRemoteAddr("192.168.1.1");
        Assertions.assertEquals("192.168.1.1:2", NtlmServletRequest.getConnectionId(request2));
        request2.setRemoteHost("codeplex.com");
        Assertions.assertEquals("codeplex.com:2", NtlmServletRequest.getConnectionId(request2));
    }
}
