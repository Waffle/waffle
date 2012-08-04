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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import waffle.http.SimpleFilterChain;
import waffle.http.SimpleHttpRequest;
import waffle.http.SimpleHttpResponse;
import waffle.servlet.spi.BasicSecurityFilterProvider;
import waffle.servlet.spi.NegotiateSecurityFilterProvider;
import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.util.Base64;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterTests extends TestCase {

	private NegotiateSecurityFilter _filter = null;

	@Override
	public void setUp() {
		String[] configFiles = new String[] { "springTestFilterBeans.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(configFiles);
		SecurityContextHolder.getContext().setAuthentication(null);
		_filter = (NegotiateSecurityFilter) ctx
				.getBean("waffleNegotiateSecurityFilter");
	}

	@Override
	public void tearDown() {
		_filter = null;
	}

	public void testFilter() {
		assertFalse(_filter.getAllowGuestLogin());
		assertEquals(PrincipalFormat.fqn, _filter.getPrincipalFormat());
		assertEquals(PrincipalFormat.both, _filter.getRoleFormat());
		assertNull(_filter.getFilterConfig());
		assertTrue(_filter.getProvider() != null);
	}

	public void testProvider() throws ClassNotFoundException {
		SecurityFilterProviderCollection provider = _filter.getProvider();
		assertEquals(2, provider.size());
		assertTrue(provider
				.getByClassName("waffle.servlet.spi.BasicSecurityFilterProvider") instanceof BasicSecurityFilterProvider);
		assertTrue(provider
				.getByClassName("waffle.servlet.spi.NegotiateSecurityFilterProvider") instanceof NegotiateSecurityFilterProvider);
	}

	public void testNoChallengeGET() throws IOException, ServletException {
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		SimpleHttpResponse response = new SimpleHttpResponse();
		SimpleFilterChain chain = new SimpleFilterChain();
		_filter.doFilter(request, response, chain);
		// unlike servlet filters, it's a passthrough
		assertEquals(500, response.getStatus());
	}

	public void testNegotiate() throws IOException, ServletException {
		String securityPackage = "Negotiate";
		SimpleFilterChain filterChain = new SimpleFilterChain();
		SimpleHttpRequest request = new SimpleHttpRequest();

		String clientToken = Base64.encode(WindowsAccountImpl
				.getCurrentUsername().getBytes());
		request.addHeader("Authorization", securityPackage + " " + clientToken);

		SimpleHttpResponse response = new SimpleHttpResponse();
		_filter.doFilter(request, response, filterChain);

		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		assertNotNull(auth);
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		assertNotNull(authorities);
		assertEquals(3, authorities.size());
		Iterator<? extends GrantedAuthority> authoritiesIterator = authorities.iterator();
		assertEquals("ROLE_USER", authoritiesIterator.next().getAuthority());
		assertEquals("ROLE_USERS", authoritiesIterator.next().getAuthority());
		assertEquals("ROLE_EVERYONE", authoritiesIterator.next().getAuthority());
		assertEquals(0, response.getHeaderNames().length);
	}

	public void testUnsupportedSecurityPackagePassthrough() throws IOException,
			ServletException {
		SimpleFilterChain filterChain = new SimpleFilterChain();
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.addHeader("Authorization", "Unsupported challenge");
		SimpleHttpResponse response = new SimpleHttpResponse();
		_filter.doFilter(request, response, filterChain);
		// the filter should ignore authorization for an unsupported security package, ie. not return a 401
		assertEquals(500, response.getStatus());
	}

	public void testGuestIsDisabled() throws IOException, ServletException {
		String securityPackage = "Negotiate";
		SimpleFilterChain filterChain = new SimpleFilterChain();
		SimpleHttpRequest request = new SimpleHttpRequest();

		String clientToken = Base64.encode("Guest".getBytes());
		request.addHeader("Authorization", securityPackage + " " + clientToken);

		SimpleHttpResponse response = new SimpleHttpResponse();
		_filter.doFilter(request, response, filterChain);

		assertEquals(401, response.getStatus());
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	public void testAfterPropertiesSet() {
		_filter.setProvider(null);
		try {
			_filter.afterPropertiesSet();
			fail("expected ServletException");
		} catch (Exception e) {
			assertTrue(e instanceof ServletException);
		}
	}
}
