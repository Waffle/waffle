/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
