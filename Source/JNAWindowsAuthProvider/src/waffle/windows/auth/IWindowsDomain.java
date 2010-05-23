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
	 * Fully qualified domain name.
	 * @return
	 *  String.
	 */
	public String getFqn();

	/**
	 * Trust direction.
	 * @return
	 *  String.
	 */
	public String getTrustDirectionString();

	/**
	 * Trust type.
	 * @return
	 *  String.
	 */
	public String getTrustTypeString();
}
