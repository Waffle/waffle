/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.windows.auth.impl;

import waffle.windows.auth.IWindowsCredentialsHandle;

import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;

/**
 * Pre-existing credentials of a security principal. This is a handle to a previously authenticated logon data used by a
 * security principal to establish its own identity, such as a password, or a Kerberos protocol ticket.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class WindowsCredentialsHandleImpl implements IWindowsCredentialsHandle {

    private String     principalName;
    private int        credentialsType;
    private String     securityPackage;
    private CredHandle handle;
    private TimeStamp  clientLifetime;

    /**
     * Returns the current credentials handle.
     * 
     * @param securityPackage
     *            Security package, eg. "Negotiate".
     * @return A windows credentials handle
     */
    public static IWindowsCredentialsHandle getCurrent(String securityPackage) {
        IWindowsCredentialsHandle handle = new WindowsCredentialsHandleImpl(null, Sspi.SECPKG_CRED_OUTBOUND,
                securityPackage);
        handle.initialize();
        return handle;
    }

    /**
     * A new Windows credentials handle.
     * 
     * @param principalName
     *            Principal name.
     * @param credentialsType
     *            Credentials type.
     * @param securityPackage
     *            Security package.
     */
    public WindowsCredentialsHandleImpl(String principalName, int credentialsType, String securityPackage) {
        this.principalName = principalName;
        this.credentialsType = credentialsType;
        this.securityPackage = securityPackage;
    }

    /**
     * Initialize a new credentials handle.
     */
    @Override
    public void initialize() {
        this.handle = new CredHandle();
        this.clientLifetime = new TimeStamp();
        int rc = Secur32.INSTANCE.AcquireCredentialsHandle(this.principalName, this.securityPackage,
                this.credentialsType, null, null, null, null, this.handle, this.clientLifetime);
        if (WinError.SEC_E_OK != rc) {
            throw new Win32Exception(rc);
        }
    }

    /**
     * Dispose of the credentials handle.
     */
    @Override
    public void dispose() {
        if (this.handle != null && !this.handle.isNull()) {
            int rc = Secur32.INSTANCE.FreeCredentialsHandle(this.handle);
            if (WinError.SEC_E_OK != rc) {
                throw new Win32Exception(rc);
            }
        }
    }

    /**
     * Get CredHandle.
     */
    @Override
    public CredHandle getHandle() {
        return this.handle;
    }
}
