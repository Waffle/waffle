/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
package waffle.windows.auth;

import com.sun.jna.platform.win32.Sspi.CredHandle;

/**
 * Windows credentials handle.
 * 
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
