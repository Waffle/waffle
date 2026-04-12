/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import javax.servlet.http.HttpSessionBindingEvent;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

/**
 * Tests for {@link AutoDisposableWindowsPrincipal}.
 */
class AutoDisposableWindowsPrincipalTest {

    /** The Constant TEST_FQN. */
    private static final String TEST_FQN = "DOMAIN\\testuser";

    /** The windows identity. */
    @Mocked
    private IWindowsIdentity windowsIdentity;

    /** The session binding event. */
    @Mocked
    private HttpSessionBindingEvent sessionBindingEvent;

    /**
     * Test constructor with identity only.
     */
    @Test
    void testConstructorWithIdentityOnly() {
        Assertions.assertNotNull(new Expectations() {
            {
                AutoDisposableWindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = AutoDisposableWindowsPrincipalTest.TEST_FQN;
                AutoDisposableWindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final AutoDisposableWindowsPrincipal principal = new AutoDisposableWindowsPrincipal(this.windowsIdentity);
        Assertions.assertEquals(AutoDisposableWindowsPrincipalTest.TEST_FQN, principal.getName());
    }

    /**
     * Test constructor with identity and format.
     */
    @Test
    void testConstructorWithIdentityAndFormat() {
        Assertions.assertNotNull(new Expectations() {
            {
                AutoDisposableWindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = AutoDisposableWindowsPrincipalTest.TEST_FQN;
                AutoDisposableWindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final AutoDisposableWindowsPrincipal principal = new AutoDisposableWindowsPrincipal(this.windowsIdentity,
                PrincipalFormat.FQN, PrincipalFormat.FQN);
        Assertions.assertEquals(AutoDisposableWindowsPrincipalTest.TEST_FQN, principal.getName());
    }

    /**
     * Test value bound does nothing.
     */
    @Test
    void testValueBound() {
        Assertions.assertNotNull(new Expectations() {
            {
                AutoDisposableWindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = AutoDisposableWindowsPrincipalTest.TEST_FQN;
                AutoDisposableWindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        final AutoDisposableWindowsPrincipal principal = new AutoDisposableWindowsPrincipal(this.windowsIdentity);
        Assertions.assertDoesNotThrow(() -> principal.valueBound(this.sessionBindingEvent));
    }

    /**
     * Test value unbound disposes identity.
     */
    @Test
    void testValueUnboundDisposesIdentity() {
        Assertions.assertNotNull(new Expectations() {
            {
                AutoDisposableWindowsPrincipalTest.this.windowsIdentity.getFqn();
                this.result = AutoDisposableWindowsPrincipalTest.TEST_FQN;
                AutoDisposableWindowsPrincipalTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
                AutoDisposableWindowsPrincipalTest.this.windowsIdentity.dispose();
                this.times = 1;
            }
        });
        final AutoDisposableWindowsPrincipal principal = new AutoDisposableWindowsPrincipal(this.windowsIdentity);
        principal.valueUnbound(this.sessionBindingEvent);
    }
}
