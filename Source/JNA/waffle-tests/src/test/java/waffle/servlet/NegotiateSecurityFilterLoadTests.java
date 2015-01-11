/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
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

import javax.servlet.ServletException;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterLoadTests {

    @Rule
    public ContiPerfRule                 contiPerfRule = new ContiPerfRule();

    private NegotiateSecurityFilterTests tests         = new NegotiateSecurityFilterTests();

    @Before
    public void setUp() throws ServletException {
        this.tests.setUp();
    }

    @After
    public void tearDown() {
        this.tests.tearDown();
    }

    @Test
    @PerfTest(invocations = 10, threads = 10)
    public void testLoad() throws Throwable {
        this.tests.testNegotiate();
    }
}
