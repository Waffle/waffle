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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author ari.suutari[at]syncrontech[dot]com
 */
public class SPNegoMessageTests {

    // Different SPNEGO messages. For details and specification,
    // see http://msdn.microsoft.com/en-us/library/ms995330.aspx

    private static final byte[] negTokenInitOk       = { 0x60, 0x76, 0x06, 0x06, 0x2B, 0x06, 0x01, 0x05, 0x05, 0x02 };
    private static final byte[] negTokenInitTooShort = { 0x60, 0x76, 0x06, 0x06, 0x2B, 0x06, 0x01, 0x05, 0x05 };

    private static final byte[] negTokenArgOk        = { (byte) 0xA1, 0x33, 0x30, 0x31, 0x0, 0x03, 0x0A, 0x01, 0x01,
            0x0, 0x2A, 0x04, 0x28, 0x4E, 0x54, 0x4C, 0x4D, 0x53, 0x53, 0x50, 0x00, 0x01, 0x00, 0x00, 0x00, 0x0, 0x0,
            0x08, 0x0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x06, 0x01, 0x0, 0x1D, 0x00, 0x00, 0x00, 0x0F };

    private static final byte[] negTokenArgTooShort  = { (byte) 0xA1, 0x33, 0x30, 0x31, 0x0, 0x03 };

    private static final byte[] badMessage           = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    @Test
    public void testIsSPNegoMessage() {
        assertFalse(SPNegoMessage.isSPNegoMessage(null));
        assertTrue(SPNegoMessage.isSPNegoMessage(negTokenInitOk));
        assertFalse(SPNegoMessage.isSPNegoMessage(negTokenInitTooShort));
        assertTrue(SPNegoMessage.isSPNegoMessage(negTokenArgOk));
        assertFalse(SPNegoMessage.isSPNegoMessage(negTokenArgTooShort));
        assertFalse(SPNegoMessage.isSPNegoMessage(badMessage));
    }

    @Test
    public void testIsNegTokenInit() {
        assertTrue(SPNegoMessage.isNegTokenInit(negTokenInitOk));
        assertFalse(SPNegoMessage.isNegTokenInit(negTokenInitTooShort));
        assertFalse(SPNegoMessage.isNegTokenInit(badMessage));
    }

    @Test
    public void testIsNegTokenArg() {
        assertTrue(SPNegoMessage.isNegTokenArg(negTokenArgOk));
        assertFalse(SPNegoMessage.isNegTokenArg(negTokenArgTooShort));
        assertFalse(SPNegoMessage.isNegTokenArg(badMessage));
    }
}
