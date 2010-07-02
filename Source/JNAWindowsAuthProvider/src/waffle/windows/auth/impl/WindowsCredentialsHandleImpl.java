/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth.impl;

import waffle.windows.auth.IWindowsCredentialsHandle;

import com.sun.jna.NativeLong;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.TimeStamp;

/**
 * Pre-existing credentials of a security principal. This is a handle to a previously 
 * authenticated logon data used by a security principal to establish  its own identity, 
 * such as a password, or a Kerberos protocol ticket.
 * @author dblock[at]dblock[dot]org
 */
public class WindowsCredentialsHandleImpl implements IWindowsCredentialsHandle {
	
	private String _principalName = null;
	private NativeLong _credentialsType = null;  
	private String _securityPackage = null;
	private CredHandle _handle = null;
	private TimeStamp _clientLifetime = null;
	
	
	/**
	 * Returns the current credentials handle.
	 * @param securityPackage
	 *  Security package, eg. "Negotiate".
	 * @return
	 *  A windows credentials handle
	 */
	public static IWindowsCredentialsHandle getCurrent(String securityPackage) {
		IWindowsCredentialsHandle handle = new WindowsCredentialsHandleImpl(
				null, Sspi.SECPKG_CRED_OUTBOUND, securityPackage);
		handle.initialize();
		return handle;
	}
	
	/**
	 * A new Windows credentials handle.
	 * @param principalName
	 *  Principal name.
	 * @param credentialsType
	 *  Credentials type.
	 * @param securityPackage
	 *  Security package.
	 */
	public WindowsCredentialsHandleImpl(String principalName, int credentialsType, 
			String securityPackage) {
		_principalName = principalName;
		_credentialsType = new NativeLong(credentialsType);
		_securityPackage = securityPackage;
	}

	/**
	 * Initialize a new credentials handle.
	 */
	public void initialize() {		
		_handle = new CredHandle();
		_clientLifetime = new TimeStamp();
		int rc = Secur32.INSTANCE.AcquireCredentialsHandle(_principalName, 
				_securityPackage, _credentialsType, null, null, null, null, 
				_handle, _clientLifetime);
		if (W32Errors.SEC_E_OK != rc) {
			throw new Win32Exception(rc);
		}
	}

	/**
	 * Dispose of the credentials handle.
	 */
	public void dispose() {
		if (_handle != null && ! _handle.isNull()) {
			int rc = Secur32.INSTANCE.FreeCredentialsHandle(_handle);
			if (W32Errors.SEC_E_OK != rc) {
				throw new Win32Exception(rc);
			}
			_handle = null;
		}
	}

	/**
	 * 
	 */
	public CredHandle getHandle() {
		return _handle;
	}
}
