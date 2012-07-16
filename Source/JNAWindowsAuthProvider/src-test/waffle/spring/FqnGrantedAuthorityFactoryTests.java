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
package waffle.spring;

import junit.framework.TestCase;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.WindowsAccount;

public class FqnGrantedAuthorityFactoryTests extends TestCase {

	private WindowsAccount _group;

	@Override
	public void setUp() {
		_group = new WindowsAccount(new MockWindowsAccount("group"));
	}

	public void testPrefixAndUppercase() {
		FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(
				"prefix_", true);
		assertEquals(new SimpleGrantedAuthority("PREFIX_GROUP"),
				factory.createGrantedAuthority(_group));
	}

	public void testPrefixAndLowercase() {
		FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(
				"prefix_", false);
		assertEquals(new SimpleGrantedAuthority("prefix_group"),
				factory.createGrantedAuthority(_group));
	}

	public void testNoPrefixAndUppercase() {
		FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(
				null, true);
		assertEquals(new SimpleGrantedAuthority("GROUP"),
				factory.createGrantedAuthority(_group));
	}

	public void testNoPrefixAndLowercase() {
		FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(
				null, false);
		assertEquals(new SimpleGrantedAuthority("group"),
				factory.createGrantedAuthority(_group));
	}

}
