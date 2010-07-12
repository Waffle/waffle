/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsIdentityImpersonationContextImpl implements IWindowsImpersonationContext {

	/**
	 * Impersonate a logged on user.
	 * @param windowsIdentity
	 *  Windows identity obtained via LogonUser.
	 */
	public WindowsIdentityImpersonationContextImpl(HANDLE windowsIdentity) {
		if (! Advapi32.INSTANCE.ImpersonateLoggedOnUser(windowsIdentity)) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}
	}
	
	public void RevertToSelf() {
		Advapi32.INSTANCE.RevertToSelf();
	}	
}
