/*******************************************************************************
 * Waffle (http://waffle.codeplex.com)
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
package waffle.windows.auth;

import junit.framework.TestCase;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthProviderLoadTests extends TestCase {

	private class WindowsAuthProviderLoadTest extends TestRunnable {
		WindowsAuthProviderTests _tests = new WindowsAuthProviderTests();

		@Override
		public void runTest() throws Throwable {
			_tests.testAcceptSecurityToken();
		}
	}

	public void testLoad() throws Throwable {
		int load = 10;
		TestRunnable[] runs = new TestRunnable[load];
		for (int i = 0; i < load; i++) {
			runs[i] = new WindowsAuthProviderLoadTest();
		}
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(runs);
		mttr.runTestRunnables();
	}
}
