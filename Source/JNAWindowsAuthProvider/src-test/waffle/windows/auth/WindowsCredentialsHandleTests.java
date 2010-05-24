/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth;

import junit.framework.TestCase;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsCredentialsHandleTests extends TestCase {
	
	public void testGetCurrent() {
		IWindowsCredentialsHandle handle = WindowsCredentialsHandleImpl.getCurrent(
				"Negotiate");
		assertNotNull(handle);
		handle.initialize();
		handle.dispose();
	}
}
