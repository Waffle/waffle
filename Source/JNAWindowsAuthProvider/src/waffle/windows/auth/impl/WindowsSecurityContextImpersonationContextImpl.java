/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;

import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsSecurityContextImpersonationContextImpl implements IWindowsImpersonationContext {

	private CtxtHandle _ctx = null;
	
	public WindowsSecurityContextImpersonationContextImpl(CtxtHandle ctx) {
		int rc = Secur32.INSTANCE.ImpersonateSecurityContext(ctx); 
		if (rc != W32Errors.SEC_E_OK) {
			throw new Win32Exception(rc);
		}
		
		_ctx = ctx;
	}
	
	public void RevertToSelf() {
		int rc = Secur32.INSTANCE.RevertSecurityContext(_ctx); 
		if (rc != W32Errors.SEC_E_OK) {
			throw new Win32Exception(rc);
		}
	}
}
