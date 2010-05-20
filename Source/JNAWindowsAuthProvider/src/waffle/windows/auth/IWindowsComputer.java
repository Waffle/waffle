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
	 * 
	 * @return
	 */
	public String getComputerName();

	/**
	 * 
	 * @return
	 */
	public String getMemberOf();

	/**
	 * 
	 * @return
	 */
	public String getJoinStatus();

	/**
	 * 
	 * @return
	 */
	public String[] getGroups();
}
