/*
 * MIT License
 *
 * Copyright (c) 2010-2022 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;

import waffle.windows.auth.IWindowsCredentialsHandle;

/**
 * Pre-existing credentials of a security principal. This is a handle to a previously authenticated logon data used by a
 * security principal to establish its own identity, such as a password, or a Kerberos protocol ticket.
 */
public class WindowsCredentialsHandleImpl implements IWindowsCredentialsHandle {

    /** The principal name. */
    private final String principalName;

    /** The credentials type. */
    private final int credentialsType;

    /** The security package. */
    private final String securityPackage;

    /** The handle. */
    private CredHandle handle;

    /**
     * A new Windows credentials handle.
     *
     * @param newPrincipalName
     *            Principal name.
     * @param newCredentialsType
     *            Credentials type.
     * @param newSecurityPackage
     *            Security package.
     */
    public WindowsCredentialsHandleImpl(final String newPrincipalName, final int newCredentialsType,
            final String newSecurityPackage) {
        this.principalName = newPrincipalName;
        this.credentialsType = newCredentialsType;
        this.securityPackage = newSecurityPackage;
    }

    /**
     * Returns the current credentials handle.
     *
     * @param securityPackage
     *            Security package, eg. "Negotiate".
     *
     * @return A windows credentials handle
     */
    public static IWindowsCredentialsHandle getCurrent(final String securityPackage) {
        final IWindowsCredentialsHandle handle = new WindowsCredentialsHandleImpl(null, Sspi.SECPKG_CRED_OUTBOUND,
                securityPackage);
        handle.initialize();
        return handle;
    }

    /**
     * Initialize a new credentials handle.
     */
    @Override
    public void initialize() {
        this.handle = new CredHandle();
        final TimeStamp clientLifetime = new TimeStamp();
        final int rc = Secur32.INSTANCE.AcquireCredentialsHandle(this.principalName, this.securityPackage,
                this.credentialsType, null, null, null, null, this.handle, clientLifetime);
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
            final int rc = Secur32.INSTANCE.FreeCredentialsHandle(this.handle);
            if (WinError.SEC_E_OK != rc) {
                throw new Win32Exception(rc);
            }
        }
    }

    /**
     * Get CredHandle.
     *
     * @return the handle
     */
    @Override
    public CredHandle getHandle() {
        return this.handle;
    }
}
