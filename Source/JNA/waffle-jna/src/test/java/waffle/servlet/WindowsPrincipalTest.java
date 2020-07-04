/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.servlet;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    IWindowsIdentity windowsIdentity;

    /**
     * Test to string.
     */
    @Test
    public void testToString() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        Assertions.assertEquals(WindowsPrincipalTest.TEST_FQN, principal.getName());
        Assertions.assertEquals(WindowsPrincipalTest.TEST_FQN, principal.toString());
    }

    /**
     * Test equals and hash code.
     */
    @Test
    public void testEqualsAndHashCode() {
        Assertions.assertNotNull(new Expectations() {
            {
                WindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = WindowsPrincipalTest.TEST_FQN;
                WindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final WindowsPrincipal principal = new WindowsPrincipal(this.windowsIdentity);
        final WindowsPrincipal principal2 = new WindowsPrincipal(this.windowsIdentity);
        Assertions.assertTrue(principal.equals(principal2) && principal2.equals(principal));
        Assertions.assertEquals(principal.hashCode(), principal2.hashCode());
        Assertions.assertEquals(principal.hashCode(), WindowsPrincipalTest.TEST_FQN.hashCode());
    }

}
