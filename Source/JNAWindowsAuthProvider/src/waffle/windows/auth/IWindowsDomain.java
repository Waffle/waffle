/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth;

/**
 * A Windows Domain.
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsDomain {

	/**
	 * 
	 * @return
	 */
	public String getFqn();

	/**
	 * 
	 * @return
	 */
	public String getTrustDirectionString();

	/**
	 * 
	 * @return
	 */
	public String getTrustTypeString();
}
