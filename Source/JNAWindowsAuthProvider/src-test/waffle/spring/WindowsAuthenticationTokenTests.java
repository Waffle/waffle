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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import waffle.mock.MockWindowsIdentity;
import waffle.servlet.WindowsPrincipal;
import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationTokenTests extends TestCase {	
	
	private WindowsPrincipal _principal = null;
	private WindowsAuthenticationToken _token = null;
	
	@Override
	public void setUp() {
		List<String> mockGroups = new ArrayList<String>();
		mockGroups.add("group1");
		mockGroups.add("group2");
		MockWindowsIdentity mockIdentity = new MockWindowsIdentity("localhost\\user1", mockGroups);
		_principal = new WindowsPrincipal(mockIdentity);
		_token = new WindowsAuthenticationToken(_principal);		
	}
	
	public void testWindowsAuthenticationToken() {
		assertNull(_token.getCredentials());
		assertNull(_token.getDetails());
		assertTrue(_token.isAuthenticated());
		assertEquals("localhost\\user1", _token.getName());
		Collection<GrantedAuthority> authorities = _token.getAuthorities();
		Iterator<GrantedAuthority> authoritiesIterator = authorities.iterator();
		assertEquals(3, authorities.size());
		assertEquals("ROLE_USER", authoritiesIterator.next().getAuthority());
		assertEquals("ROLE_GROUP1", authoritiesIterator.next().getAuthority());
		assertEquals("ROLE_GROUP2", authoritiesIterator.next().getAuthority());
		assertEquals(_principal, _token.getPrincipal());
	}
	
	public void testCustomGrantedAuthorityFactory() {
		
		WindowsAuthenticationToken token = new WindowsAuthenticationToken(
			_principal,
			new FqnGrantedAuthorityFactory(null, false),
			null);
		
		assertNull(token.getCredentials());
		assertNull(token.getDetails());
		assertTrue(token.isAuthenticated());
		assertEquals("localhost\\user1", token.getName());
		Collection<GrantedAuthority> authorities = token.getAuthorities();
		Iterator<GrantedAuthority> authoritiesIterator = authorities.iterator();
		assertEquals(2, authorities.size());
		assertEquals("group1", authoritiesIterator.next().getAuthority());
		assertEquals("group2", authoritiesIterator.next().getAuthority());
		assertEquals(_principal, token.getPrincipal());
	}
	
	public void testAuthenticated() {
		assertTrue(_token.isAuthenticated());
		try {
			_token.setAuthenticated(true);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}
}
