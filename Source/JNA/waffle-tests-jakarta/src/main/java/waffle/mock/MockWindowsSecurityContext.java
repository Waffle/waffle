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
package waffle.mock;

import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

import java.util.ArrayList;
import java.util.List;

import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * The Class MockWindowsSecurityContext.
 *
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsSecurityContext implements IWindowsSecurityContext {

    /** The identity. */
    private final IWindowsIdentity identity;

    /**
     * Instantiates a new mock windows security context.
     *
     * @param username
     *            the username
     */
    public MockWindowsSecurityContext(final String username) {
        final List<String> groups = new ArrayList<>();
        groups.add("Users");
        groups.add("Everyone");
        this.identity = new MockWindowsIdentity(username, groups);
    }

    @Override
    public void dispose() {
        // Do Nothing
    }

    @Override
    public boolean isContinue() {
        return false;
    }

    @Override
    public CtxtHandle getHandle() {
        return new CtxtHandle();
    }

    @Override
    public IWindowsIdentity getIdentity() {
        return this.identity;
    }

    @Override
    public String getPrincipalName() {
        return this.identity.getFqn();
    }

    @Override
    public String getSecurityPackage() {
        return "Mock";
    }

    @Override
    public byte[] getToken() {
        return new byte[0];
    }

    @Override
    public IWindowsImpersonationContext impersonate() {
        return new MockWindowsImpersonationContext();
    }

    /**
     * Initialize.
     */
    public void initialize() {
        // Do Nothing
    }

    @Override
    public void initialize(final CtxtHandle continueCtx, final SecBufferDesc continueToken,
            final String targetPrincipalName) {
        // Do Nothing
    }

}
