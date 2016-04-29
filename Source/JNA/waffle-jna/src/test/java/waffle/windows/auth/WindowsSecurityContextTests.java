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
package waffle.windows.auth;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

/**
 * The Class WindowsSecurityContextTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsSecurityContextTests {

    /**
     * Test negotiate.
     */
    @Test
    public void testNegotiate() {
        final String securityPackage = "Negotiate";
        // security context
        final IWindowsSecurityContext ctx = WindowsSecurityContextImpl.getCurrent(securityPackage,
                WindowsAccountImpl.getCurrentUsername());
        Assert.assertTrue(ctx.isContinue());
        Assert.assertEquals(securityPackage, ctx.getSecurityPackage());
        Assertions.assertThat(ctx.getToken().length).isGreaterThan(0);
        ctx.dispose();
    }
}
