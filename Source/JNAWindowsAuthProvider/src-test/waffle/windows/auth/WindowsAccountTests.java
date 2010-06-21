/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
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
