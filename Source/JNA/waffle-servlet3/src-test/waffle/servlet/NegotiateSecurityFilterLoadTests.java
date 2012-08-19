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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterLoadTests {

	private NegotiateSecurityFilterTests _tests = new NegotiateSecurityFilterTests();

	@Before
	public void setUp() {
		_tests.setUp();
	}

	@After
	public void tearDown() {
		_tests.tearDown();
	}

	private class NegotiateSecurityFilterLoadTest extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			_tests.testNegotiate();
		}
	}

	@Test
	public void testLoad() throws Throwable {
		int load = 10;
		TestRunnable[] runs = new TestRunnable[load];
		for (int i = 0; i < load; i++) {
			runs[i] = new NegotiateSecurityFilterLoadTest();
		}
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(runs);
		mttr.runTestRunnables();
	}
}
