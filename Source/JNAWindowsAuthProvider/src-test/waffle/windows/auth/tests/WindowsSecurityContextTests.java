package waffle.windows.auth.tests;

import junit.framework.TestCase;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

public class WindowsSecurityContextTests extends TestCase {
	
	public void testNegotiate() {
		String securityPackage = "Negotiate";
		// security context
		IWindowsSecurityContext ctx = WindowsSecurityContextImpl.getCurrent(securityPackage);
		ctx.initialize();
		assertTrue(ctx.getContinue());
		assertEquals(securityPackage, ctx.getSecurityPackage());
		assertTrue(ctx.getToken().length > 0);
		ctx.dispose();
	}
}
