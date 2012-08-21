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

import waffle.mock.http.SimpleHttpRequest;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NtlmServletRequestTests {

	@Test
	public void testGetConnectionId() {
		SimpleHttpRequest.resetRemotePort();
		SimpleHttpRequest request1 = new SimpleHttpRequest();
		assertEquals(":1", NtlmServletRequest.getConnectionId(request1));
		SimpleHttpRequest request2 = new SimpleHttpRequest();
		assertEquals(":2", NtlmServletRequest.getConnectionId(request2));
		request2.setRemoteAddr("192.168.1.1");
		assertEquals("192.168.1.1:2",
				NtlmServletRequest.getConnectionId(request2));
		request2.setRemoteHost("codeplex.com");
		assertEquals("codeplex.com:2",
				NtlmServletRequest.getConnectionId(request2));
	}
}
