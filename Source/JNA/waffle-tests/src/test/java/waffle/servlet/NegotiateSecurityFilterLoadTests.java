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

import javax.servlet.ServletException;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The Class NegotiateSecurityFilterLoadTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterLoadTests {

    /** The conti perf rule. */
    @Rule
    public ContiPerfRule                       contiPerfRule = new ContiPerfRule();

    /** The tests. */
    private final NegotiateSecurityFilterTests tests         = new NegotiateSecurityFilterTests();

    /**
     * Sets the up.
     *
     * @throws ServletException
     *             the servlet exception
     */
    @Before
    public void setUp() throws ServletException {
        this.tests.setUp();
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {
        this.tests.tearDown();
    }

    /**
     * Test load.
     *
     * @throws Throwable
     *             the throwable
     */
    @Test
    @PerfTest(invocations = 10, threads = 10)
    public void testLoad() throws Throwable {
        this.tests.testNegotiate();
    }
}
