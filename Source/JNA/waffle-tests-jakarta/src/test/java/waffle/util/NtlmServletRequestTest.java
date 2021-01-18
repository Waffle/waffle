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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.mock.http.SimpleHttpRequest;

/**
 * The Class NtlmServletRequestTest.
 *
 * @author dblock[at]dblock[dot]org
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
