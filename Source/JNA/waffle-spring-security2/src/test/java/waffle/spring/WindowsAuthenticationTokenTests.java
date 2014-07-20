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
package waffle.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;

import waffle.mock.MockWindowsIdentity;
import waffle.servlet.WindowsPrincipal;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationTokenTests {

	private WindowsPrincipal			_principal	= null;
	private WindowsAuthenticationToken	_token		= null;

	@Before
	public void setUp() {
		List<String> mockGroups = new ArrayList<String>();
		mockGroups.add("group1");
		mockGroups.add("group2");
		MockWindowsIdentity mockIdentity = new MockWindowsIdentity("localhost\\user1", mockGroups);
		_principal = new WindowsPrincipal(mockIdentity);
		_token = new WindowsAuthenticationToken(_principal);
	}

	@Test
	public void testWindowsAuthenticationToken() {
		assertNull(_token.getCredentials());
		assertNull(_token.getDetails());
		assertTrue(_token.isAuthenticated());
		assertEquals("localhost\\user1", _token.getName());
		GrantedAuthority[] authorities = _token.getAuthorities();
		assertEquals(3, authorities.length);
		assertEquals("ROLE_USER", authorities[0].getAuthority());
		assertEquals("ROLE_GROUP1", authorities[1].getAuthority());
		assertEquals("ROLE_GROUP2", authorities[2].getAuthority());
		assertEquals(_principal, _token.getPrincipal());
	}

	@Test
	public void testCustomGrantedAuthorityFactory() {

		WindowsAuthenticationToken token = new WindowsAuthenticationToken(_principal, new FqnGrantedAuthorityFactory(
				null, false), null);

		assertNull(token.getCredentials());
		assertNull(token.getDetails());
		assertTrue(token.isAuthenticated());
		assertEquals("localhost\\user1", token.getName());
		GrantedAuthority[] authorities = token.getAuthorities();
		assertEquals(2, authorities.length);
		assertEquals("group1", authorities[0].getAuthority());
		assertEquals("group2", authorities[1].getAuthority());
		assertEquals(_principal, token.getPrincipal());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAuthenticated() {
		assertTrue(_token.isAuthenticated());
		_token.setAuthenticated(true);
	}
}
