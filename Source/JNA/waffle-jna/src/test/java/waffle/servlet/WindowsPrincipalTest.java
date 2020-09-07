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
    void testToString() {
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
    void testEqualsAndHashCode() {
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
