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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


import waffle.mock.http.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.mockito.*;
import static org.mockito.Mockito.*;

import waffle.mock.MockWindowsAuthProvider;
import waffle.util.Base64;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * Waffle Tomcat Security Filter Tests
 * 
 * @author dblock[at]dblock[dot]org
 */
public class BasicSecurityFilterTests {

	NegotiateSecurityFilter _filter = null;
	MockWindowsAuthProvider _provider = null;

	@Before
	public void setUp() {
		_filter = new NegotiateSecurityFilter();
		_filter.setAuth(new MockWindowsAuthProvider());
		try {
			_filter.init(null);
		} catch (ServletException e) {
			fail(e.getMessage());
		}
	}

	@After
	public void tearDown() {
		_filter.destroy();
	}

	@Test
	public void testBasicAuth() throws IOException, ServletException {
	  SimpleHttpRequest request = new SimpleHttpRequest();
	  request.setMethod("GET");
	  
		String userHeaderValue = WindowsAccountImpl.getCurrentUsername()
				+ ":password";
		String basicAuthHeader = "Basic "
				+ Base64.encode(userHeaderValue.getBytes());
		request.addHeader("Authorization", basicAuthHeader);
		
		SimpleHttpResponse response = new SimpleHttpResponse();
		FilterChain filterChain = new SimpleFilterChain();
		_filter.doFilter(request, response, filterChain);
		Subject subject = (Subject) request.getSession().getAttribute(
				"javax.security.auth.subject");
		boolean authenticated = (subject != null && subject.getPrincipals()
				.size() > 0);
		assertTrue(authenticated);
	}
}
