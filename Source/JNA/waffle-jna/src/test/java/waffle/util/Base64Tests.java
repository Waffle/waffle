/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.io.BaseEncoding;

/**
 * @author dblock[at]dblock[dot]org
 * @deprecated Due to prior change this is no longer testing waffle base64 but guava. Remove this once base64 of waffle
 *             is removed in 1.8.
 */
@Deprecated
public class Base64Tests {

    @Test
    public void testBase64EncodeDecode() {
        for (int i = 0; i < 100; i++) {
            final byte[] data = new byte[i];
            for (byte j = 0; j < i; j++) {
                data[j] = j;
            }

            final String base64data = BaseEncoding.base64().encode(data);
            final byte[] decodedData = BaseEncoding.base64().decode(base64data);

            assertEquals(decodedData.length, data.length);

            for (byte j = 0; j < i; j++) {
                assertEquals(decodedData[j], data[j]);
            }
        }
    }

    @Test
    public void testBase64DecodeEncode() {
        final String base64String = "YH8GBisGAQUFAqB1MHOgMDAuBgorBgEEAYI3AgIKBgkqhkiC9xIBAgIGCSqGSIb3EgECAgYKKwYBBAGCNwICHqI/BD1OVExNU1NQAAEAAACXsgjiCQAJADQAAAAMAAwAKAAAAAYBsB0AAAAPREJMT0NLLUdSRUVOV09SS0dST1VQ";
        final byte[] data = BaseEncoding.base64().decode(base64String);
        final String encodedBase64String = BaseEncoding.base64().encode(data);
        assertEquals(encodedBase64String, base64String);
    }

    @Test
    public void testBase64DecodeEncode2() {
        final String base64String = "oXcwdaADCgEBoloEWE5UTE1TU1AAAwAAAAAAAABYAAAAAAAAAFgAAAAAAAAAWAAAAAAAAABYAAAAAAAAAFgAAAAAAAAAWAAAABXCiOIGAbAdAAAAD0K5dZi6NldppxCrei9fHUGjEgQQAQAAAPUXp1AtIpqEAAAAAA==";
        final byte[] data = BaseEncoding.base64().decode(base64String);
        final String encodedBase64String = BaseEncoding.base64().encode(data);
        assertEquals(encodedBase64String, base64String);
    }

}
