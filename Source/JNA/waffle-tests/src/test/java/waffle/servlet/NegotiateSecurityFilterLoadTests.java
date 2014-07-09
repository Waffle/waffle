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
package waffle.servlet;

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
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	private NegotiateSecurityFilterTests _tests = new NegotiateSecurityFilterTests();

	@Before
	public void setUp() {
		_tests.setUp();
	}

	@After
	public void tearDown() {
		_tests.tearDown();
	}

	@Test
	@PerfTest(invocations = 10, threads = 10)
	public void testLoad() throws Throwable {
		_tests.testNegotiate();
	}
}
