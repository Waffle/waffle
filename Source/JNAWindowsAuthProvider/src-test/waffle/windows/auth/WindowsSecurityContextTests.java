/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth;

import junit.framework.TestCase;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
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
