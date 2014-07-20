/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
package waffle.util;

/**
 * Rudimentary NTLM message utility.
 * 
 * @author dblock[at]dblock[dot]org
 */
public final class NtlmMessage {

	// NTLM messages start with 0x4e544c4d53535000, NTLMSSP signature
	private static final byte[]	ntlmsspSignature	= { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00 };

	public static boolean isNtlmMessage(byte[] message) {
		if (message == null || message.length < ntlmsspSignature.length) {
			return false;
		}

		for (int i = 0; i < ntlmsspSignature.length; i++) {
			if (ntlmsspSignature[i] != message[i]) {
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
	public static int getMessageType(byte[] message) {
		return message[ntlmsspSignature.length];
	}

	private NtlmMessage() {
		// Prevent Instantiation of object
	}
}
