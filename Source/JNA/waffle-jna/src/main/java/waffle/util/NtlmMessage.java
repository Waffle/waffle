/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.util;

/**
 * Rudimentary NTLM message utility.
 *
 * @author dblock[at]dblock[dot]org
 */
public final class NtlmMessage {

    // NTLM messages start with 0x4e544c4d53535000, NTLMSSP signature
    /** The Constant NTLM_SSP_SIGNATURE. */
    private static final byte[] NTLM_SSP_SIGNATURE = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00 };

    /**
     * Checks if is ntlm message.
     *
     * @param message
     *            the message
     * @return true, if is ntlm message
     */
    public static boolean isNtlmMessage(final byte[] message) {
        if (message == null || message.length < NtlmMessage.NTLM_SSP_SIGNATURE.length) {
            return false;
        }

        for (int i = 0; i < NtlmMessage.NTLM_SSP_SIGNATURE.length; i++) {
            if (NtlmMessage.NTLM_SSP_SIGNATURE[i] != message[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get NTLM message type.
     *
     * @param message
     *            Assuming a valid NTLM message.
     * @return Message type.
     */
    public static int getMessageType(final byte[] message) {
        return message[NtlmMessage.NTLM_SSP_SIGNATURE.length];
    }

    /**
     * Instantiates a new ntlm message.
     */
    private NtlmMessage() {
        // Prevent Instantiation of object
    }
}
