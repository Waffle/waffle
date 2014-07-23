/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
package waffle.servlet;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

public class AutoDisposableWindowsPrincipal extends WindowsPrincipal implements HttpSessionBindingListener {

    private static final long serialVersionUID = 1L;

    public AutoDisposableWindowsPrincipal(IWindowsIdentity windowsIdentity) {
        super(windowsIdentity);
    }

    public AutoDisposableWindowsPrincipal(IWindowsIdentity windowsIdentity, PrincipalFormat principalFormat,
            PrincipalFormat roleFormat) {
        super(windowsIdentity, principalFormat, roleFormat);
    }

    @Override
    public void valueBound(HttpSessionBindingEvent evt) {
        // Do nothing
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent evt) {
        if (getIdentity() != null) {
            getIdentity().dispose();
        }
    }

}
