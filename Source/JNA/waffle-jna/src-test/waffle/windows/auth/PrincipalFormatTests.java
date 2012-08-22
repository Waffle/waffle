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
package waffle.windows.auth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dblock[at]dblock[dot]org
 */
public class PrincipalFormatTests {

	@Test
	public void testKnown() {
		assertEquals(PrincipalFormat.fqn, PrincipalFormat.valueOf("fqn"));
		assertEquals(PrincipalFormat.sid, PrincipalFormat.valueOf("sid"));
		assertEquals(PrincipalFormat.both, PrincipalFormat.valueOf("both"));
		assertEquals(PrincipalFormat.none, PrincipalFormat.valueOf("none"));
		assertEquals(4, PrincipalFormat.values().length);
	}

	@Test(expected = RuntimeException.class)
	public void testUnknown() {
		PrincipalFormat.valueOf("garbage");
	}
}
