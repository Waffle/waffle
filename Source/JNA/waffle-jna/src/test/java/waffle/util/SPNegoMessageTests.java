/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * The Class SPNegoMessageTests.
 *
 * @author ari.suutari[at]syncrontech[dot]com
 */
public class SPNegoMessageTests {

    // Different SPNEGO messages. For details and specification,
    // see http://msdn.microsoft.com/en-us/library/ms995330.aspx

    /** The Constant negTokenInitOk. */
    private static final byte[] negTokenInitOk       = { 0x60, 0x76, 0x06, 0x06, 0x2B, 0x06, 0x01, 0x05, 0x05, 0x02 };

    /** The Constant negTokenInitTooShort. */
    private static final byte[] negTokenInitTooShort = { 0x60, 0x76, 0x06, 0x06, 0x2B, 0x06, 0x01, 0x05, 0x05 };

    /** The Constant negTokenArgOk. */
    private static final byte[] negTokenArgOk        = { (byte) 0xA1, 0x33, 0x30, 0x31, 0x0, 0x03, 0x0A, 0x01, 0x01,
            0x0, 0x2A, 0x04, 0x28, 0x4E, 0x54, 0x4C, 0x4D, 0x53, 0x53, 0x50, 0x00, 0x01, 0x00, 0x00, 0x00, 0x0, 0x0,
            0x08, 0x0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x06, 0x01, 0x0, 0x1D, 0x00, 0x00, 0x00, 0x0F };

    /** The Constant negTokenArgTooShort. */
    private static final byte[] negTokenArgTooShort  = { (byte) 0xA1, 0x33, 0x30, 0x31, 0x0, 0x03 };

    /** The Constant badMessage. */
    private static final byte[] badMessage           = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    /**
     * Test is neg token init.
     */
    @Test
    public void testIsNegTokenInit() {
        Assert.assertTrue(SPNegoMessage.isNegTokenInit(SPNegoMessageTests.negTokenInitOk));
        Assert.assertFalse(SPNegoMessage.isNegTokenInit(SPNegoMessageTests.negTokenInitTooShort));
        Assert.assertFalse(SPNegoMessage.isNegTokenInit(SPNegoMessageTests.badMessage));
    }

    /**
     * Test is neg token arg.
     */
    @Test
    public void testIsNegTokenArg() {
        Assert.assertTrue(SPNegoMessage.isNegTokenArg(SPNegoMessageTests.negTokenArgOk));
        Assert.assertFalse(SPNegoMessage.isNegTokenArg(SPNegoMessageTests.negTokenArgTooShort));
        Assert.assertFalse(SPNegoMessage.isNegTokenArg(SPNegoMessageTests.badMessage));
    }
}
