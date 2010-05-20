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
	 * 
	 * @return
	 */
	public String getSidString();

	/**
	 * 
	 * @return
	 */
	public byte[] getSid();

	/**
	 * 
	 * @return
	 */
	public String getFqn();

	/**
	 * 
	 * @return
	 */
	public IWindowsAccount[] getGroups();
}
