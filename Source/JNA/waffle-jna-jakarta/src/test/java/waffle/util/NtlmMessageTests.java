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

/**
 * The Class NtlmMessageTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class NtlmMessageTests {

    /**
     * Test is ntlm message.
     */
    @Test
    void testIsNtlmMessage() {
        Assertions.assertFalse(NtlmMessage.isNtlmMessage(null));
        final byte[] ntlmSignature = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00 };
        Assertions.assertTrue(NtlmMessage.isNtlmMessage(ntlmSignature));
        final byte[] shortMessage = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50 };
        Assertions.assertFalse(NtlmMessage.isNtlmMessage(shortMessage));
        final byte[] longMessage = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00, 0x00 };
        Assertions.assertTrue(NtlmMessage.isNtlmMessage(longMessage));
        final byte[] badMessage = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        Assertions.assertFalse(NtlmMessage.isNtlmMessage(badMessage));
    }

    /**
     * Test get ntlm message type.
     */
    @Test
    void testGetNtlmMessageType() {
        final byte[] ntlmMessageType1 = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02,
                0x02, 0x00, 0x00 };
        Assertions.assertEquals(1, NtlmMessage.getMessageType(ntlmMessageType1));
        final byte[] ntlmMessageType2 = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x02, 0x00, 0x00, 0x01, 0x23, 0x45, 0x67 };
        Assertions.assertEquals(2, NtlmMessage.getMessageType(ntlmMessageType2));
        final byte[] ntlmMessageType3 = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x02, 0x00, 0x00, 0x01, 0x23, 0x45, 0x67 };
        Assertions.assertEquals(3, NtlmMessage.getMessageType(ntlmMessageType3));
    }
}
