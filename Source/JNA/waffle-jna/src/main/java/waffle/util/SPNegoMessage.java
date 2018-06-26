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
 * @author ari.suutari[at]syncrontech[dot]com
 */
public final class SPNegoMessage {

    // Check for NegTokenInit. It has always a special oid ("spnegoOid"),
    // which makes it rather easy to detect.
    /** The Constant SPENGO_OID. */
    private static final byte[] SPENGO_OID = { 0x06, 0x06, 0x2b, 0x06, 0x01, 0x05, 0x05, 0x02 };

    // Check if this message is SPNEGO authentication token. There
    // are two token types, NegTokenInit and NegTokenArg.
    // For details and specification, see
    // https://msdn.microsoft.com/en-us/library/ms995330.aspx

    /**
     * Checks if is neg token init.
     *
     * @param message
     *            the message
     * @return true, if is neg token init
     */
    public static boolean isNegTokenInit(final byte[] message) {

        // Message should always contains at least some kind of
        // id byte and length. If it is too short, it
        // cannot be a SPNEGO message.
        if (message == null || message.length < 2) {
            return false;
        }

        // First byte should always be 0x60 (Application Constructed Object)
        if (message[0] != 0x60) {
            return false;
        }

        // Next byte(s) contain token length, figure out
        // how many bytes are used for length data
        int lenBytes = 1;
        if ((message[1] & 0x80) != 0) {
            lenBytes = 1 + (message[1] & 0x7f);
        }

        if (message.length < SPNegoMessage.SPENGO_OID.length + 1 + lenBytes) {
            return false;
        }

        // Now check for SPNEGO OID, which should start just after length data.
        for (int i = 0; i < SPNegoMessage.SPENGO_OID.length; i++) {
            if (SPNegoMessage.SPENGO_OID[i] != message[i + 1 + lenBytes]) {
                return false;
            }
        }

        return true;
    }

    // Check for NegTokenArg. It doesn't have oid similar to NegTokenInit.
    // Instead id has one-byte id (0xa1). Obviously this is not
    // a great way to detect the message, so we check encoded
    // message length against number of received message bytes.
    /**
     * Checks if is neg token arg.
     *
     * @param message
     *            the message
     * @return true, if is neg token arg
     */
    public static boolean isNegTokenArg(final byte[] message) {

        // Message should always contains at least some kind of
        // id byte and length. If it is too short, it
        // cannot be a SPNEGO message.
        if (message == null || message.length < 2) {
            return false;
        }

        // Check if this is NegTokenArg packet, it's id is 0xa1
        if ((message[0] & 0xff) != 0xa1) {
            return false;
        }

        int lenBytes;
        int len;

        // Get length of message for additional check.
        if ((message[1] & 0x80) == 0) {
            len = message[1];
        } else {
            lenBytes = message[1] & 0x7f;
            len = 0;
            final int i = 2;
            while (lenBytes > 0) {
                len = len << 8;
                len |= message[i] & 0xff;
                --lenBytes;
            }
        }

        return len + 2 == message.length;
    }

    /**
     * Instantiates a new SP nego message.
     */
    private SPNegoMessage() {
        // Prevent Instantiation of object
    }
}
