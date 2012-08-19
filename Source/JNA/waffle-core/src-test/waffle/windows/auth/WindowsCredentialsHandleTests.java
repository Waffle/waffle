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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsCredentialsHandleTests {

	@Test
	public void testGetCurrent() {
		IWindowsCredentialsHandle handle = WindowsCredentialsHandleImpl
				.getCurrent("Negotiate");
		assertNotNull(handle);
		handle.initialize();
		handle.dispose();
	}
}
