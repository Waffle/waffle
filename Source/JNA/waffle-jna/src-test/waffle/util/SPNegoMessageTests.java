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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * @author dblock[at]dblock[dot]org
 */
public class SPNegoMessageTests {

	@Test
	public void testIsSPNegoMessage() {
		assertFalse(SPNegoMessage.isSPNegoMessage(null));
		byte[] msg = { 0x60, 0x76, 0x06, 0x06, 0x2B, 0x06, 0x01, 0x05, 0x05, 0x02  };
		assertTrue(SPNegoMessage.isSPNegoMessage(msg));
		byte[] shortMessage = { 0x60, 0x76, 0x06, 0x06, 0x2B, 0x06, 0x01, 0x05, 0x05 };
		assertFalse(SPNegoMessage.isSPNegoMessage(shortMessage));
		byte[] badMessage = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00 };
		assertFalse(SPNegoMessage.isSPNegoMessage(badMessage));
	}
}
