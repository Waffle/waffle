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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccountTests {

	@Test
	public void testGetCurrentUsername() {
		String currentUsername = WindowsAccountImpl.getCurrentUsername();
		System.out.println("Current username: " + currentUsername);
		assertTrue(currentUsername.length() > 0);
	}

	@Test
	public void testGetCurrentAccount() {
		String currentUsername = WindowsAccountImpl.getCurrentUsername();
		IWindowsAccount account = new WindowsAccountImpl(currentUsername);
		assertTrue(account.getName().length() > 0);
		System.out.println("Name: " + account.getName());
		assertTrue(account.getDomain().length() > 0);
		System.out.println("Domain: " + account.getDomain());
		assertTrue(account.getFqn().length() > 0);
		System.out.println("Fqn: " + account.getFqn());
		assertTrue(account.getSidString().length() > 0);
		System.out.println("Sid: " + account.getSidString());
		// To avoid errors with machine naming being all upper-case, use test in this manner
		assertTrue(currentUsername.equalsIgnoreCase(account.getFqn()));
		assertTrue(currentUsername.endsWith("\\" + account.getName()));
		// To avoid errors with machine naming being all upper-case, use test in this manner
		assertTrue(currentUsername.toLowerCase().startsWith(account.getDomain().toLowerCase() + "\\"));
	}
}
