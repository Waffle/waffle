/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth;

import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

/**
 * A Windows security context.
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsSecurityContext {

	/**
	 * Security package name.
	 * @return
	 *  String.
	 */	
	public String getSecurityPackage();

	/**
	 * Principal name.
	 * @return
	 *  String.
	 */
	public String getPrincipalName();
	
	/**
	 * Token.
	 * @return
	 *  Array of bytes.
	 */
	public byte[] getToken();

	/**
	 * True if protocol requires continuation.
	 * @return
	 *  True or false.
	 */
	public boolean getContinue();

	/**
	 * Windows Identity.
	 * @return
	 *  Windows Identity.
	 */
	public IWindowsIdentity getIdentity();

	/**
	 * Context handle.
	 * @return
	 *  Handle.
	 */
	public Sspi.CtxtHandle getHandle();

	/**
	 * Initialize the security context.
	 */
	public void initialize();
	
	/**
	 * Initialize the security context, continuing from a previous one.
	 * @param continueCtx
	 *  Continue context.
	 * @param continueToken
	 *  Continue token.
	 */
	public void initialize(CtxtHandle continueCtx, SecBufferDesc continueToken);
	
	/**
	 * Impersonate this security context.
	 * @return
	 *  A Windows Impersonation Context.
	 */
	public IWindowsImpersonationContext impersonate();
	
	/**
	 * Disposes of the context.
	 */
	public void dispose();
}
