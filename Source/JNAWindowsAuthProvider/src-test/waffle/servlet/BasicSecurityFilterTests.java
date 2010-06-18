/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet;

import java.io.IOException;

import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import junit.framework.TestCase;
import waffle.apache.catalina.SimpleFilterChain;
import waffle.apache.catalina.SimpleHttpRequest;
import waffle.apache.catalina.SimpleHttpResponse;
import waffle.mock.MockWindowsAuthProvider;
import waffle.util.Base64;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * Waffle Tomcat Security Filter Tests
 * @author dblock[at]dblock[dot]org
 */
public class BasicSecurityFilterTests extends TestCase {

	NegotiateSecurityFilter _filter = null;
	MockWindowsAuthProvider _provider = null;
	
	@Override
	public void setUp() {
		_filter = new NegotiateSecurityFilter();
		_filter.setAuth(new MockWindowsAuthProvider());
		try {
			_filter.init(null);
		} catch (ServletException e) {
			fail(e.getMessage());
		}
	}

	@Override
	public void tearDown() {
		_filter.destroy();
		_filter = null;
	}
	
	public void testBasicAuth() throws IOException, ServletException {
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		String userHeaderValue = WindowsAccountImpl.getCurrentUsername() + ":password";
		String basicAuthHeader = "Basic " + Base64.encode(userHeaderValue.getBytes());
		request.addHeader("Authorization", basicAuthHeader);
		SimpleHttpResponse response = new SimpleHttpResponse();
		FilterChain filterChain = new SimpleFilterChain();
		_filter.doFilter(request, response, filterChain);
		Subject subject = (Subject) request.getSession().getAttribute("javax.security.auth.subject");
		boolean authenticated = (subject != null && subject.getPrincipals().size() > 0);
		assertTrue(authenticated);
	}
}
