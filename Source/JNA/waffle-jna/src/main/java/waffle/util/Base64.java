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

import com.google.common.io.BaseEncoding;

/**
 * @deprecated use Guava BaseEncoding.base64() instead. Internally this is now just a wrapper around guava and will be
 *             removed in waffle 1.8.
 */
@Deprecated
public final class Base64 {

    /**
     * Base-64 encodes the supplied block of data. Line wrapping is not applied on output.
     * 
     * @param bytes
     *            The block of data that is to be Base-64 encoded.
     * @return A <code>String</code> containing the encoded data.
     * @deprecated use Guava BaseEncoding.base64() instead. Internally this is now just a wrapper around guava and will
     *             be removed in waffle 1.8.
     */
    @Deprecated
    public static String encode(final byte[] bytes) {
        return BaseEncoding.base64().encode(bytes);
    }

    /**
     * Decodes the supplied Base-64 encoded string.
     * 
     * @param string
     *            The Base-64 encoded string that is to be decoded.
     * @return A <code>byte[]</code> containing the decoded data block.
     * @deprecated use Guava BaseEncoding.base64() instead. Internally this is now just a wrapper around guava and will
     *             be removed in waffle 1.8.
     */
    @Deprecated
    public static byte[] decode(final String string) {
        return BaseEncoding.base64().decode(string);
    }

    private Base64() {
        // Prevent Instantiation of object
    }
}
