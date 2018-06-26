/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.windows.auth;

import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

/**
 * A Windows security context.
 *
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsSecurityContext {

    /**
     * Security package name.
     *
     * @return String.
     */
    String getSecurityPackage();

    /**
     * Principal name.
     *
     * @return String.
     */
    String getPrincipalName();

    /**
     * Token.
     *
     * @return Array of bytes.
     */
    byte[] getToken();

    /**
     * True if protocol requires continuation.
     *
     * @return True or false.
     */
    boolean isContinue();

    /**
     * Windows Identity.
     *
     * @return Windows Identity.
     */
    IWindowsIdentity getIdentity();

    /**
     * Context handle.
     *
     * @return Handle.
     */
    Sspi.CtxtHandle getHandle();

    /**
     * Initialize the security context, continuing from a previous one.
     *
     * @param continueCtx
     *            Continue context.
     * @param continueToken
     *            Continue token.
     * @param targetName
     *            The target of the context. The string contents are security-package specific.
     */
    void initialize(final CtxtHandle continueCtx, final SecBufferDesc continueToken, final String targetName);

    /**
     * Impersonate this security context.
     *
     * @return A Windows Impersonation Context.
     */
    IWindowsImpersonationContext impersonate();

    /**
     * Disposes of the context.
     */
    void dispose();
}
