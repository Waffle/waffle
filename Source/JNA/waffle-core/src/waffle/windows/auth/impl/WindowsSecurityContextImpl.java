/*******************************************************************************
* Waffle (http://waffle.codeplex.com)
* 
* Copyright (c) 2010 Application Security, Inc.
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Application Security, Inc.
*******************************************************************************/
package waffle.windows.auth.impl;

import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;
import waffle.windows.auth.IWindowsSecurityContext;

import com.sun.jna.NativeLong;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.NativeLongByReference;

/**
 * Windows Security Context.
 * @author dblock[at]dblock[dot]org
 */
public class WindowsSecurityContextImpl implements IWindowsSecurityContext {

	private String _principalName;
	private String _securityPackage;
	private SecBufferDesc _token = null;
	private CtxtHandle _ctx = null;
	private NativeLongByReference _attr;
	private CredHandle _credentials;
	private boolean _continue;
	
	@Override
	public IWindowsIdentity getIdentity() {
    	HANDLEByReference phContextToken = new HANDLEByReference();
    	int rc = Secur32.INSTANCE.QuerySecurityContextToken(_ctx, phContextToken);
    	if (W32Errors.SEC_E_OK != rc) {
    		throw new Win32Exception(rc);
    	}
    	return new WindowsIdentityImpl(phContextToken.getValue());
	}

	@Override
	public String getSecurityPackage() {
		return _securityPackage;
	}

	@Override
	public byte[] getToken() {
		return _token == null ? null : _token.getBytes();
	}
	
	/**
	 * Get the current Windows security context for a given SSPI package.
	 * @param securityPackage
	 *  SSPI package.
	 * @return
	 *  Windows security context.
	 */
	public static IWindowsSecurityContext getCurrent(String securityPackage, String targetName) {
		IWindowsCredentialsHandle credentialsHandle = WindowsCredentialsHandleImpl.getCurrent(
				securityPackage);
		credentialsHandle.initialize();
		try {
			WindowsSecurityContextImpl ctx = new WindowsSecurityContextImpl();
			ctx.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
			ctx.setCredentialsHandle(credentialsHandle.getHandle()); 
			ctx.setSecurityPackage(securityPackage);
			ctx.initialize(null, null, targetName);
			return ctx;
		} finally {
			credentialsHandle.dispose();
		}
	}
	
	public WindowsSecurityContextImpl() {
		
	}
	
	@Override
	public void initialize(CtxtHandle continueCtx, SecBufferDesc continueToken, String targetName) {
		_attr = new NativeLongByReference();
		_token = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
    	_ctx = new CtxtHandle();
    	int rc = Secur32.INSTANCE.InitializeSecurityContext(_credentials, continueCtx, 
    			targetName, new NativeLong(Sspi.ISC_REQ_CONNECTION), new NativeLong(0), 
    			new NativeLong(Sspi.SECURITY_NATIVE_DREP), continueToken, new NativeLong(0), _ctx, _token, 
    			_attr, null);
    	switch(rc) {
    	case W32Errors.SEC_I_CONTINUE_NEEDED:
    		_continue = true;
    		break;
    	case W32Errors.SEC_E_OK:
    		_continue = false;
    		break;
     	default:
    		throw new Win32Exception(rc);
    	}
	}
	
	@Override
	public void dispose() {
		dispose(_ctx);
	}
	
	/**
	 * Dispose a security context.
	 * @param ctx
	 *  Security context.
	 * @return
	 *  True if a context was disposed.
	 */
	public static boolean dispose(CtxtHandle ctx) {
		if (ctx != null && ! ctx.isNull()) {
	    	int rc = Secur32.INSTANCE.DeleteSecurityContext(ctx);			
			if (W32Errors.SEC_E_OK != rc) {
				throw new Win32Exception(rc);
			}
			return true;
		}
		return false;
	}

	@Override
	public String getPrincipalName() {
		return _principalName;
	}
	
	public void setPrincipalName(String principalName) {
		_principalName = principalName;		
	}
		
	@Override
	public CtxtHandle getHandle() {
		return _ctx;
	}

	public void setCredentialsHandle(CredHandle handle) {
		_credentials = handle;
	}

	public void setToken(byte[] bytes) {
		_token = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, bytes);
	}
	
	public void setSecurityPackage(String securityPackage) {
		_securityPackage = securityPackage;
	}

	public void setSecurityContext(CtxtHandle phNewServerContext) {
		_ctx = phNewServerContext;
	}

	@Override
	public boolean getContinue() {
		return _continue;
	}

	public void setContinue(boolean b) {
		_continue = b;
	}

	@Override
	public IWindowsImpersonationContext impersonate() {
		return new WindowsSecurityContextImpersonationContextImpl(_ctx);
	}
}
