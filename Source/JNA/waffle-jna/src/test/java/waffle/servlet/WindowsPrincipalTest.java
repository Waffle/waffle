/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;

/**
 * @author Tony BenBrahim &lt;tony.benbrahim@airliquide.com&gt;
 * 
 */
public class WindowsPrincipalTest {

    private static final String TEST_FQN = "ACME\\john.smith";

    @Test
    public void testToString() {
        final IWindowsIdentity windowsIdentity = mock(IWindowsIdentity.class);
        when(windowsIdentity.getFqn()).thenReturn(TEST_FQN);
        when(windowsIdentity.getGroups()).thenReturn(new IWindowsAccount[0]);
        final WindowsPrincipal principal = new WindowsPrincipal(windowsIdentity);
        assertEquals(TEST_FQN, principal.getName());
        assertEquals(TEST_FQN, principal.toString());
    }

}
