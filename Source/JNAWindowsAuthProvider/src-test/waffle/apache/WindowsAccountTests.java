/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.apache;

import junit.framework.TestCase;
import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.WindowsAccount;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccountTests extends TestCase {
	public void testProperties() {
		MockWindowsAccount mockWindowsAccount = new MockWindowsAccount("localhost\\Administrator");
		WindowsAccount account = new WindowsAccount(mockWindowsAccount);
		assertEquals("localhost", account.getDomain());
		assertEquals("localhost\\Administrator", account.getFqn());
		assertEquals("Administrator", account.getName());
		assertTrue(account.getSidString().startsWith("S-"));
	}
}
