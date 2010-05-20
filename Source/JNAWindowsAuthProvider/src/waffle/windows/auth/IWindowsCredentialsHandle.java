/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth;

import com.sun.jna.platform.win32.Sspi.CredHandle;

/**
 * Windows credentials handle.
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsCredentialsHandle {
	/**
	 * Initialize.
	 */
	void initialize();
	/**
	 * Dispose.
	 */
	void dispose();
	
	/**
	 * Return a security handle.
	 */
	CredHandle getHandle();
}
