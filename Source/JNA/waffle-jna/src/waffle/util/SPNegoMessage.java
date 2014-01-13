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
 * @author ari.suutari[at]syncrontech[dot]com
 */
public abstract class SPNegoMessage {

	// SPNEGO OID, for details see http://msdn.microsoft.com/en-us/library/ms995330.aspx
	private static final byte[]	spnegoOid	= {0x06, 0x06, 0x2b, 0x06, 0x01, 0x05, 0x05, 0x02};

	public static boolean isSPNegoMessage(byte[] message)
	{
		if (message == null || message.length < 2) {
			return false;
		}

		int lenBytes;

		// Check if this is NegTokenArg packet, it's id is 0xa1
		if ((message[0] & 0xff) == 0xa1) {

			int len;

			// Get lenght of message for additional check.
			if ((message[1] & 0x80) == 0)
				len = message[1];
			else {

				lenBytes = message[1] & 0x7f;
				len = 0;
				int i = 2;
				while (lenBytes > 0) {

					len = len << 8;
					len |= (message[i] & 0xff);
					--lenBytes;
				}
			}

			return (len + 2 == message.length);
		}

		// First byte should always be 0x60 (Application Constructed Object)
		if (message[0] != 0x60) {
			return false;
		}

		// Next byte(s) contain token length, figure out
		// how many bytes are used for length data
		lenBytes = 1;
		if ((message[1] & 0x80) != 0) {
			lenBytes = 1 + (message[1] & 0x7f);
		}

		if (message.length < spnegoOid.length + 1 + lenBytes) {
			return false;
		}

		// Now check for SPNEGO OID, which should start just after length data.
		for (int i = 0; i < spnegoOid.length; i++) {
			if (spnegoOid[i] != message[i + 1 + lenBytes]) {
				return false;
			}
		}

		return true;
	}
}
