/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

/**
 * A Windows security context.
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
