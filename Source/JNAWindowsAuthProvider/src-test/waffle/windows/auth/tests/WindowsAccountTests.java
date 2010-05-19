package waffle.windows.auth.tests;

import junit.framework.TestCase;
import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.impl.WindowsAccountImpl;

public class WindowsAccountTests extends TestCase {
	
	public void testGetCurrentUsername() {
		String currentUsername = WindowsAccountImpl.getCurrentUsername();
		System.out.println("Current username: " + currentUsername);
		assertTrue(currentUsername.length() > 0);
	}
	
	public void testGetCurrentAccount() throws Exception {
		IWindowsAccount account = new WindowsAccountImpl(WindowsAccountImpl.getCurrentUsername());
		assertTrue(account.getFqn().length() > 0);
		System.out.println("Fqn: " + account.getFqn());
		assertTrue(account.getSidString().length() > 0);
		System.out.println("Sid: " + account.getSidString());
	}
}
