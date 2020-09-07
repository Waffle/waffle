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
