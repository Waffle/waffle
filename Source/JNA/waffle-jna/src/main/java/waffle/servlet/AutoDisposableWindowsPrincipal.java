/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

/**
 * The Class AutoDisposableWindowsPrincipal.
 */
public class AutoDisposableWindowsPrincipal extends WindowsPrincipal implements HttpSessionBindingListener {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new auto disposable windows principal.
     *
     * @param windowsIdentity
     *            the windows identity
     */
    public AutoDisposableWindowsPrincipal(final IWindowsIdentity windowsIdentity) {
        super(windowsIdentity);
    }

    /**
     * Instantiates a new auto disposable windows principal.
     *
     * @param windowsIdentity
     *            the windows identity
     * @param principalFormat
     *            the principal format
     * @param roleFormat
     *            the role format
     */
    public AutoDisposableWindowsPrincipal(final IWindowsIdentity windowsIdentity, final PrincipalFormat principalFormat,
            final PrincipalFormat roleFormat) {
        super(windowsIdentity, principalFormat, roleFormat);
    }

    @Override
    public void valueBound(final HttpSessionBindingEvent evt) {
        // Do nothing
    }

    @Override
    public void valueUnbound(final HttpSessionBindingEvent evt) {
        if (this.getIdentity() != null) {
            this.getIdentity().dispose();
        }
    }

}
