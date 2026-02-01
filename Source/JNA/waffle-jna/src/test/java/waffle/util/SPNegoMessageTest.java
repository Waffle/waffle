/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class SPNegoMessageTest.
 */
class SPNegoMessageTest {

    // Different SPNEGO messages. For details and specification,
    // see https://msdn.microsoft.com/en-us/library/ms995330.aspx

    /** The Constant NEG_TOKEN_INIT_OK. */
    private static final byte[] NEG_TOKEN_INIT_OK = { 0x60, 0x76, 0x06, 0x06, 0x2B, 0x06, 0x01, 0x05, 0x05, 0x02 };

    /** The Constant NEG_TOKEN_INIT_TOO_SHORT. */
    private static final byte[] NEG_TOKEN_INIT_TOO_SHORT = { 0x60, 0x76, 0x06, 0x06, 0x2B, 0x06, 0x01, 0x05, 0x05 };

    /** The Constant NEG_TOKEN_ARG_OK. */
    private static final byte[] NEG_TOKEN_ARG_OK = { (byte) 0xA1, 0x33, 0x30, 0x31, 0x0, 0x03, 0x0A, 0x01, 0x01, 0x0,
            0x2A, 0x04, 0x28, 0x4E, 0x54, 0x4C, 0x4D, 0x53, 0x53, 0x50, 0x00, 0x01, 0x00, 0x00, 0x00, 0x0, 0x0, 0x08,
            0x0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06,
            0x01, 0x0, 0x1D, 0x00, 0x00, 0x00, 0x0F };

    /** The Constant NEG_TOKEN_ARG_TOO_SHORT. */
    private static final byte[] NEG_TOKEN_ARG_TOO_SHORT = { (byte) 0xA1, 0x33, 0x30, 0x31, 0x0, 0x03 };

    /** The Constant BAD_MESSAGE. */
    private static final byte[] BAD_MESSAGE = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    /**
     * Test is neg token init.
     */
    @Test
    void testIsNegTokenInit() {
        Assertions.assertTrue(SPNegoMessage.isNegTokenInit(SPNegoMessageTest.NEG_TOKEN_INIT_OK));
        Assertions.assertFalse(SPNegoMessage.isNegTokenInit(SPNegoMessageTest.NEG_TOKEN_INIT_TOO_SHORT));
        Assertions.assertFalse(SPNegoMessage.isNegTokenInit(SPNegoMessageTest.BAD_MESSAGE));
    }

    /**
     * Test is neg token arg.
     */
    @Test
    void testIsNegTokenArg() {
        Assertions.assertTrue(SPNegoMessage.isNegTokenArg(SPNegoMessageTest.NEG_TOKEN_ARG_OK));
        Assertions.assertFalse(SPNegoMessage.isNegTokenArg(SPNegoMessageTest.NEG_TOKEN_ARG_TOO_SHORT));
        Assertions.assertFalse(SPNegoMessage.isNegTokenArg(SPNegoMessageTest.BAD_MESSAGE));
    }
}
