/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.util;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Base64Tests extends TestCase {

	public void testBase64EncodeDecode() {
		for(int i = 0; i < 100; i++) {
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
	
	public void testBase64DecodeEncode() {
		String base64String = "YH8GBisGAQUFAqB1MHOgMDAuBgorBgEEAYI3AgIKBgkqhkiC9xIBAgIGCSqGSIb3EgECAgYKKwYBBAGCNwICHqI/BD1OVExNU1NQAAEAAACXsgjiCQAJADQAAAAMAAwAKAAAAAYBsB0AAAAPREJMT0NLLUdSRUVOV09SS0dST1VQ"; 
		byte[] data = Base64.decode(base64String);
		String encodedBase64String = Base64.encode(data);
		assertEquals(encodedBase64String, base64String);
	}
	
	public void testBase64DecodeEncode2() {
		String base64String = "oXcwdaADCgEBoloEWE5UTE1TU1AAAwAAAAAAAABYAAAAAAAAAFgAAAAAAAAAWAAAAAAAAABYAAAAAAAAAFgAAAAAAAAAWAAAABXCiOIGAbAdAAAAD0K5dZi6NldppxCrei9fHUGjEgQQAQAAAPUXp1AtIpqEAAAAAA=="; 
		byte[] data = Base64.decode(base64String);
		String encodedBase64String = Base64.encode(data);
		assertEquals(encodedBase64String, base64String);
	}
}
