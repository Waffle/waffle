package waffle.windows.auth.tests;

import junit.framework.TestCase;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

public class WindowsAuthProviderTests extends TestCase {

	public void testGetCurrentComputer() throws Exception {
		IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
		IWindowsComputer computer = prov.getCurrentComputer();
		System.out.println(computer.getComputerName());
		assertTrue(computer.getComputerName().length() > 0);
		System.out.println(computer.getJoinStatus());
		System.out.println(computer.getMemberOf());
		String[] localGroups = computer.getGroups();
		assertNotNull(localGroups);
		assertTrue(localGroups.length > 0);
		for(String localGroup : localGroups) {
			System.out.println(" " + localGroup);
		}		
	}	
}
