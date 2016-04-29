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

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * The Class WindowsAuthProviderLoadTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthProviderLoadTests {

    /** The conti perf rule. */
    @Rule
    public ContiPerfRule                   contiPerfRule = new ContiPerfRule();

    /** The tests. */
    private final WindowsAuthProviderTests tests         = new WindowsAuthProviderTests();

    /**
     * Test load.
     *
     * @throws Throwable
     *             the throwable
     */
    @Test
    @PerfTest(invocations = 10, threads = 10)
    public void testLoad() throws Throwable {
        this.tests.testAcceptSecurityToken();
    }
}
