/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth;

/**
 * A Windows Computer.
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsComputer {

	/**
	 * Computer name.
	 * @return
	 *  String.
	 */
	public String getComputerName();

	/**
	 * Member of (domain).
	 * @return
	 *  String.
	 */
	public String getMemberOf();

	/**
	 * Join status.
	 * @return
	 *  String.
	 */
	public String getJoinStatus();

	/**
	 * Groups.
	 * @return
	 *  Array of group names.
	 */
	public String[] getGroups();
}
