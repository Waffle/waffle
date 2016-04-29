/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.mock;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

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

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsSecurityContext#dispose()
     */
    @Override
    public void dispose() {
        // Do Nothing
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsSecurityContext#isContinue()
     */
    @Override
    public boolean isContinue() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsSecurityContext#getHandle()
     */
    @Override
    public CtxtHandle getHandle() {
        return new CtxtHandle();
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsSecurityContext#getIdentity()
     */
    @Override
    public IWindowsIdentity getIdentity() {
        return this.identity;
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsSecurityContext#getPrincipalName()
     */
    @Override
    public String getPrincipalName() {
        return this.identity.getFqn();
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsSecurityContext#getSecurityPackage()
     */
    @Override
    public String getSecurityPackage() {
        return "Mock";
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsSecurityContext#getToken()
     */
    @Override
    public byte[] getToken() {
        return new byte[0];
    }

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsSecurityContext#impersonate()
     */
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

    /*
     * (non-Javadoc)
     * @see waffle.windows.auth.IWindowsSecurityContext#initialize(com.sun.jna.platform.win32.Sspi.CtxtHandle,
     * com.sun.jna.platform.win32.Sspi.SecBufferDesc, java.lang.String)
     */
    @Override
    public void initialize(final CtxtHandle continueCtx, final SecBufferDesc continueToken,
            final String targetPrincipalName) {
        // Do Nothing
    }
}
