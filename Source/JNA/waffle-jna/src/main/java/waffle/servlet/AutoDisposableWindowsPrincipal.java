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
    public AutoDisposableWindowsPrincipal(final IWindowsIdentity windowsIdentity,
            final PrincipalFormat principalFormat, final PrincipalFormat roleFormat) {
        super(windowsIdentity, principalFormat, roleFormat);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
     */
    @Override
    public void valueBound(final HttpSessionBindingEvent evt) {
        // Do nothing
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
     */
    @Override
    public void valueUnbound(final HttpSessionBindingEvent evt) {
        if (this.getIdentity() != null) {
            this.getIdentity().dispose();
        }
    }

}
