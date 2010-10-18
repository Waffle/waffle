/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.kerberos;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * @author michal[ddot]bergmann[at]seznam[dott]cz
 */
public class JavaIdentity implements IWindowsIdentity {

	String _fqn;
	String _sidString;
	byte[] _sid;

	JavaIdentity(String fqn) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		this._fqn = fqn;
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(fqn.getBytes("utf-8"));
		this._sid = digest.digest();
		this._sidString = String.format("%1$032X", new BigInteger(1,this._sid));
	}

	@Override
	public String getSidString() {
		return _sidString;
	}

	@Override
	public byte[] getSid() {
		return _sid;
	}

	@Override
	public String getFqn() {
		return _fqn;
	}

	@Override
	public IWindowsAccount[] getGroups() {
		return new IWindowsAccount[0];
	}

	@Override
	public IWindowsImpersonationContext impersonate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isGuest() {
		return false;
	}

}
