/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.spring;

import junit.framework.TestCase;

import org.springframework.security.core.authority.GrantedAuthorityImpl;

import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.WindowsAccount;

public class FqnGrantedAuthorityFactoryTests extends TestCase {
	
	private WindowsAccount _group;

	@Override
	public void setUp() {
		_group = new WindowsAccount(new MockWindowsAccount("group"));
	}
	
	public void testPrefixAndUppercase() {
		FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", true);
		assertEquals(new GrantedAuthorityImpl("PREFIX_GROUP"), factory.createGrantedAuthority(_group));
	}
	
	public void testPrefixAndLowercase() {
		FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", false);
		assertEquals(new GrantedAuthorityImpl("prefix_group"), factory.createGrantedAuthority(_group));
	}

	public void testNoPrefixAndUppercase() {
		FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, true);
		assertEquals(new GrantedAuthorityImpl("GROUP"), factory.createGrantedAuthority(_group));
	}
	
	public void testNoPrefixAndLowercase() {
		FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, false);
		assertEquals(new GrantedAuthorityImpl("group"), factory.createGrantedAuthority(_group));
	}
	
}
