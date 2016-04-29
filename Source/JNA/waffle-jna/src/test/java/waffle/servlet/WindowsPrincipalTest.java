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

import org.junit.Assert;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;

/**
 * The Class WindowsPrincipalTest.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsPrincipalTest {

    /** The Constant TEST_FQN. */
    private static final String TEST_FQN = "ACME\\john.smith";

    /** The windows identity. */
    @Mocked
    IWindowsIdentity            windowsIdentity;

    /**
     * Test to string.
     */
    @Test
    public void testToString() {
        Assert.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        Assert.assertEquals(WindowsPrincipalTest.TEST_FQN, principal.getName());
        Assert.assertEquals(WindowsPrincipalTest.TEST_FQN, principal.toString());
    }

    /**
     * Test equals and hash code.
     */
    @Test
    public void testEqualsAndHashCode() {
        Assert.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        WindowsPrincipal principal2 = new WindowsPrincipal(this.windowsIdentity);
        Assert.assertTrue(principal.equals(principal2) && principal2.equals(principal));
        Assert.assertEquals(principal.hashCode(), principal2.hashCode());
        Assert.assertEquals(principal.hashCode(), WindowsPrincipalTest.TEST_FQN.hashCode());
    }

}
