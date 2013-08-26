/*******************************************************************************
* Waffle (http://waffle.codeplex.com)
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

/**
 * A Windows imerpsonation context.
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsImpersonationContext {

	/**
	 * Terminate the impersonation of a client application.
	 */
	void RevertToSelf();
}
