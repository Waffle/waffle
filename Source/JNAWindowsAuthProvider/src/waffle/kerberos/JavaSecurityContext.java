/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.kerberos;

import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * @author michal[ddot]bergmann[at]seznam[dott]cz
 */
public class JavaSecurityContext implements IWindowsSecurityContext {

	public JavaSecurityContext(byte[] token, String securityPackage,
			IWindowsIdentity identity) {
		this._conti = false;
		this._token = token;
		this._identity = identity;
		this._securityPackage = securityPackage;
	}

	public JavaSecurityContext(byte[] token, String securityPackage) {
		this._conti = true;
		this._token = token;
		this._securityPackage = securityPackage;
	}

	private boolean _conti;
	private byte[] _token;
	private IWindowsIdentity _identity;
	private String _securityPackage;

	@Override
	public String getSecurityPackage() {
		return _securityPackage;
	}

	@Override
	public String getPrincipalName() {
		return null;
	}

	@Override
	public byte[] getToken() {
		return _token;
	}

	@Override
	public boolean getContinue() {
		return _conti;
	}

	@Override
	public IWindowsIdentity getIdentity() {
		return _identity;
	}

	@Override
	public CtxtHandle getHandle() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initialize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initialize(CtxtHandle continueCtx, SecBufferDesc continueToken) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IWindowsImpersonationContext impersonate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose() {
	}

}
