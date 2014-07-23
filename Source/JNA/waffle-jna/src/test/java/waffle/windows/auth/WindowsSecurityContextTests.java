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
package waffle.windows.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsSecurityContextTests {

    @Test
    public void testNegotiate() {
        String securityPackage = "Negotiate";
        // security context
        IWindowsSecurityContext ctx = WindowsSecurityContextImpl.getCurrent(securityPackage,
                WindowsAccountImpl.getCurrentUsername());
        assertTrue(ctx.isContinue());
        assertEquals(securityPackage, ctx.getSecurityPackage());
        assertTrue(ctx.getToken().length > 0);
        ctx.dispose();
    }
}
