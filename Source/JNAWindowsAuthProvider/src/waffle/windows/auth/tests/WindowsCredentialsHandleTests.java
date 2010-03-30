package waffle.windows.auth.tests;

import junit.framework.TestCase;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;

public class WindowsCredentialsHandleTests extends TestCase {
	
	public void testGetCurrent() {
		IWindowsCredentialsHandle handle = WindowsCredentialsHandleImpl.getCurrent(
				"Negotiate");
		assertNotNull(handle);
		handle.initialize();
		handle.dispose();
	}
}
