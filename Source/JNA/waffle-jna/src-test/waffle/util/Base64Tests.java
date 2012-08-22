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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Base64Tests {

	@Test
	public void testBase64EncodeDecode() {
		for (int i = 0; i < 100; i++) {
			byte[] data = new byte[i];
			for (byte j = 0; j < i; j++) {
				data[j] = j;
			}

			String base64data = Base64.encode(data);
			byte[] decodedData = Base64.decode(base64data);

			assertEquals(decodedData.length, data.length);

			for (byte j = 0; j < i; j++) {
				assertEquals(decodedData[j], data[j]);
			}
		}
	}

	@Test
	public void testBase64DecodeEncode() {
		String base64String = "YH8GBisGAQUFAqB1MHOgMDAuBgorBgEEAYI3AgIKBgkqhkiC9xIBAgIGCSqGSIb3EgECAgYKKwYBBAGCNwICHqI/BD1OVExNU1NQAAEAAACXsgjiCQAJADQAAAAMAAwAKAAAAAYBsB0AAAAPREJMT0NLLUdSRUVOV09SS0dST1VQ";
		byte[] data = Base64.decode(base64String);
		String encodedBase64String = Base64.encode(data);
		assertEquals(encodedBase64String, base64String);
	}

	@Test
	public void testBase64DecodeEncode2() {
		String base64String = "oXcwdaADCgEBoloEWE5UTE1TU1AAAwAAAAAAAABYAAAAAAAAAFgAAAAAAAAAWAAAAAAAAABYAAAAAAAAAFgAAAAAAAAAWAAAABXCiOIGAbAdAAAAD0K5dZi6NldppxCrei9fHUGjEgQQAQAAAPUXp1AtIpqEAAAAAA==";
		byte[] data = Base64.decode(base64String);
		String encodedBase64String = Base64.encode(data);
		assertEquals(encodedBase64String, base64String);
	}
}
