/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
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
