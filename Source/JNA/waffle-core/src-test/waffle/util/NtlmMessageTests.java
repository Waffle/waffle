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

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NtlmMessageTests extends TestCase {
	public void testIsNtlmMessage() {
		assertFalse(NtlmMessage.isNtlmMessage(null));
		byte[] ntlmSignature = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00 };
		assertTrue(NtlmMessage.isNtlmMessage(ntlmSignature));
		byte[] shortMessage = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50 };
		assertFalse(NtlmMessage.isNtlmMessage(shortMessage));
		byte[] longMessage = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50, 0x00,
				0x00 };
		assertTrue(NtlmMessage.isNtlmMessage(longMessage));
		byte[] badMessage = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00 };
		assertFalse(NtlmMessage.isNtlmMessage(badMessage));
	}

	public void testGetNtlmMessageType() {
		byte[] ntlmMessageType1 = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50,
				0x00, 0x01, 0x00, 0x00, 0x00, 0x02, 0x02, 0x00, 0x00 };
		assertEquals(1, NtlmMessage.getMessageType(ntlmMessageType1));
		byte[] ntlmMessageType2 = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50,
				0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x02, 0x02, 0x00, 0x00, 0x01, 0x23, 0x45,
				0x67 };
		assertEquals(2, NtlmMessage.getMessageType(ntlmMessageType2));
		byte[] ntlmMessageType3 = { 0x4e, 0x54, 0x4c, 0x4d, 0x53, 0x53, 0x50,
				0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x02, 0x02, 0x00, 0x00, 0x01, 0x23, 0x45,
				0x67 };
		assertEquals(3, NtlmMessage.getMessageType(ntlmMessageType3));
	}
}
