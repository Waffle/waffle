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
 * A Windows Identity.
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsIdentity {

	/**
	 * Sid.
	 * @return
	 *  String.
	 */
	public String getSidString();

	/**
	 * Sid.
	 * @return
	 *  Array of bytes.
	 */
	public byte[] getSid();

	/**
	 * Fully qualified name.
	 * @return
	 *  String.
	 */
	public String getFqn();

	/**
	 * Group memberships.
	 * @return
	 *  Array of accounts.
	 */
	public IWindowsAccount[] getGroups();
	
	/**
	 * Impersonate a logged on user.
	 * @return
	 *  An impersonation context.
	 */
	public IWindowsImpersonationContext impersonate();
	
	/**
	 * Dispose of the Windows identity.
	 */
	public void dispose();
	
	/**
	 * Returns true if the identity represents a Guest account.
	 * @return
	 *  True if the identity represents a Guest account, false otherwise.
	 */
	public boolean isGuest();
}
