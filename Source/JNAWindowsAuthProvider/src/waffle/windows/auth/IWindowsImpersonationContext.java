/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
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
