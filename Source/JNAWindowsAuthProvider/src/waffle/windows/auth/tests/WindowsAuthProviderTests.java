package waffle.windows.auth.tests;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import junit.framework.TestCase;

public class WindowsAuthProviderTests extends TestCase {

	public void testGetCurrentComputer() throws Exception {
		IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
		IWindowsComputer computer = prov.getCurrentComputer();
		System.out.println(computer.getComputerName());
		assertTrue(computer.getComputerName().length() > 0);
		System.out.println(computer.getJoinStatus());
		System.out.println(computer.getMemberOf());		
	}
	
}
