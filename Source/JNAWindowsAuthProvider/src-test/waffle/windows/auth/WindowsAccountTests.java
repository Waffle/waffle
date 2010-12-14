/*******************************************************************************
* Waffle (http://waffle.codeplex.com)
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

import junit.framework.TestCase;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccountTests extends TestCase {
	
	public void testGetCurrentUsername() {
		String currentUsername = WindowsAccountImpl.getCurrentUsername();
		System.out.println("Current username: " + currentUsername);
		assertTrue(currentUsername.length() > 0);
	}
	
	public void testGetCurrentAccount() throws Exception {
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
		assertEquals(currentUsername, account.getFqn());
		assertTrue(currentUsername.endsWith("\\" + account.getName()));
		assertTrue(currentUsername.startsWith(account.getDomain() + "\\"));
	}
}
