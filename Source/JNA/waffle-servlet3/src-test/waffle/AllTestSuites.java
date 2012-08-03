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
package waffle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import waffle.servlet.AllServletTests;
import waffle.util.AllUtilTests;

@RunWith(Suite.class)
@SuiteClasses({ AllServletTests.class, AllUtilTests.class })
public class AllTestSuites {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("Master setup");

	}

	@AfterClass
	public static void tearDownClass() {
		System.out.println("Master tearDown");
	}

}
