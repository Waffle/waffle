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
 * The Class SPNegoMessageTests.
 *
 * @author ari.suutari[at]syncrontech[dot]com
 */
public class SPNegoMessageTests {

    // Different SPNEGO messages. For details and specification,
    // see https://msdn.microsoft.com/en-us/library/ms995330.aspx

    /** The Constant negTokenInitOk. */
    private static final byte[] negTokenInitOk = { 0x60, 0x76, 0x06, 0x06, 0x2B, 0x06, 0x01, 0x05, 0x05, 0x02 };

    /** The Constant negTokenInitTooShort. */
    private static final byte[] negTokenInitTooShort = { 0x60, 0x76, 0x06, 0x06, 0x2B, 0x06, 0x01, 0x05, 0x05 };

    /** The Constant negTokenArgOk. */
    private static final byte[] negTokenArgOk = { (byte) 0xA1, 0x33, 0x30, 0x31, 0x0, 0x03, 0x0A, 0x01, 0x01, 0x0, 0x2A,
            0x04, 0x28, 0x4E, 0x54, 0x4C, 0x4D, 0x53, 0x53, 0x50, 0x00, 0x01, 0x00, 0x00, 0x00, 0x0, 0x0, 0x08, 0x0,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01,
            0x0, 0x1D, 0x00, 0x00, 0x00, 0x0F };

    /** The Constant negTokenArgTooShort. */
    private static final byte[] negTokenArgTooShort = { (byte) 0xA1, 0x33, 0x30, 0x31, 0x0, 0x03 };

    /** The Constant badMessage. */
    private static final byte[] badMessage = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    /**
     * Test is neg token init.
     */
    @Test
    void testIsNegTokenInit() {
        Assertions.assertTrue(SPNegoMessage.isNegTokenInit(SPNegoMessageTests.negTokenInitOk));
        Assertions.assertFalse(SPNegoMessage.isNegTokenInit(SPNegoMessageTests.negTokenInitTooShort));
        Assertions.assertFalse(SPNegoMessage.isNegTokenInit(SPNegoMessageTests.badMessage));
    }

    /**
     * Test is neg token arg.
     */
    @Test
    void testIsNegTokenArg() {
        Assertions.assertTrue(SPNegoMessage.isNegTokenArg(SPNegoMessageTests.negTokenArgOk));
        Assertions.assertFalse(SPNegoMessage.isNegTokenArg(SPNegoMessageTests.negTokenArgTooShort));
        Assertions.assertFalse(SPNegoMessage.isNegTokenArg(SPNegoMessageTests.badMessage));
    }
}
