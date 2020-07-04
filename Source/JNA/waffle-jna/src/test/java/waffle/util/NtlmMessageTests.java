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
    public void testIsNtlmMessage() {
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
    public void testGetNtlmMessageType() {
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
