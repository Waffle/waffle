/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth;

/**
 * Windows account.
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsAccount {

	/**
	 * Security identifier.
	 * @return
	 *  String in the S- format.
	 */
	public String getSidString();


	/**
	 * Fully qualified username.
	 * @return
	 *  String.
	 */
	public String getFqn();
	
	/**
	 * User name.
	 * @return
	 *  String.
	 */
	public String getName();
	
	/**
	 * Domain name.
	 * @return
	 *  String.
	 */
	public String getDomain();
}
