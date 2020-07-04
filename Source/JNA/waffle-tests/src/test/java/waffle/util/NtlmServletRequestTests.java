/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.mock.http.SimpleHttpRequest;

/**
 * The Class NtlmServletRequestTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class NtlmServletRequestTests {

    /**
     * Test get connection id.
     */
    @Test
    public void testGetConnectionId() {
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
