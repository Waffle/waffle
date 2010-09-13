/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import waffle.mock.MockWindowsAuthProvider;
import waffle.mock.MockWindowsIdentity;
import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationProviderTests extends TestCase {
	private WindowsAuthenticationProvider _provider = null;
	
	@Override
	public void setUp() {
		String[] configFiles = new String[] { "springTestAuthBeans.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(configFiles);	
		_provider = (WindowsAuthenticationProvider) ctx.getBean("waffleSpringAuthenticationProvider");
	}

	@Override
	public void tearDown() {
		_provider = null;
	}
	
	public void testWindowsAuthenticationProvider() {
		assertTrue(_provider.getAllowGuestLogin());
		assertTrue(_provider.getAuthProvider() instanceof MockWindowsAuthProvider);
		assertEquals(PrincipalFormat.sid, _provider.getPrincipalFormat());
		assertEquals(PrincipalFormat.both, _provider.getRoleFormat());
	}

	public void testSupports() {
		assertFalse(_provider.supports(this.getClass()));
		assertTrue(_provider.supports(UsernamePasswordAuthenticationToken.class));
	}

	public void testAuthenticate() {
		MockWindowsIdentity mockIdentity = new MockWindowsIdentity(WindowsAccountImpl.getCurrentUsername(), new ArrayList<String>());
		WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, "password");
		Authentication authenticated = _provider.authenticate(authentication);
		assertNotNull(authenticated);
		assertTrue(authenticated.isAuthenticated());
		Collection<GrantedAuthority> authorities = authenticated.getAuthorities();
		Iterator<GrantedAuthority> authoritiesIterator = authorities.iterator();
		assertEquals(3, authorities.size());
		assertEquals("ROLE_USER", authoritiesIterator.next().getAuthority());
		assertEquals("ROLE_USERS", authoritiesIterator.next().getAuthority());
		assertEquals("ROLE_EVERYONE", authoritiesIterator.next().getAuthority());
		assertTrue(authenticated.getPrincipal() instanceof WindowsPrincipal);
	}
	
	public void testAuthenticateWithCustomGrantedAuthorityFactory() {
	    _provider.setDefaultGrantedAuthority(null);
	    _provider.setGrantedAuthorityFactory(new FqnGrantedAuthorityFactory(null, false));
	    
	    MockWindowsIdentity mockIdentity = new MockWindowsIdentity(WindowsAccountImpl.getCurrentUsername(), new ArrayList<String>());
	    WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
	    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, "password");
	    
	    Authentication authenticated = _provider.authenticate(authentication);
	    assertNotNull(authenticated);
	    assertTrue(authenticated.isAuthenticated());
	    Collection<GrantedAuthority> authorities = authenticated.getAuthorities();
	    Iterator<GrantedAuthority> authoritiesIterator = authorities.iterator();
	    assertEquals(2, authorities.size());
	    assertEquals("Users", authoritiesIterator.next().getAuthority());
	    assertEquals("Everyone", authoritiesIterator.next().getAuthority());
	    assertTrue(authenticated.getPrincipal() instanceof WindowsPrincipal);
	}
	
	public void testGuestIsDisabled() {
		try {
			MockWindowsIdentity mockIdentity = new MockWindowsIdentity("Guest", new ArrayList<String>());
			_provider.setAllowGuestLogin(false);
			WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, "password");
			_provider.authenticate(authentication);
			fail("expected AuthenticationServiceException");
		} catch (AuthenticationServiceException e) {
			assertTrue(e.getCause() instanceof GuestLoginDisabledAuthenticationException);
		}
	}
}
