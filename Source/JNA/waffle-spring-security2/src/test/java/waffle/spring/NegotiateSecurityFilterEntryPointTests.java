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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterEntryPointTests {

	private NegotiateSecurityFilterEntryPoint _entryPoint = null;

	@Before
	public void setUp() {
		String[] configFiles = new String[] { "springTestFilterBeans.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(configFiles);
		_entryPoint = (NegotiateSecurityFilterEntryPoint) ctx
				.getBean("negotiateSecurityFilterEntryPoint");
	}

	@Test
	public void testChallengeGET() throws IOException, ServletException {
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		SimpleHttpResponse response = new SimpleHttpResponse();
		_entryPoint.commence(request, response, null);
		String[] wwwAuthenticates = response
				.getHeaderValues("WWW-Authenticate");
		assertEquals(3, wwwAuthenticates.length);
		assertEquals("NTLM", wwwAuthenticates[0]);
		assertEquals("Negotiate", wwwAuthenticates[1]);
		assertTrue(wwwAuthenticates[2].equals("Basic realm=\"TestRealm\""));
		assertEquals(2, response.getHeaderNamesSize());
		assertEquals("keep-alive", response.getHeader("Connection"));
		assertEquals(401, response.getStatus());
	}

	@Test (expected = ServletException.class)
	public void testGetSetProvider() throws IOException, ServletException {
		assertNotNull(_entryPoint.getProvider());
		_entryPoint.setProvider(null);
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		SimpleHttpResponse response = new SimpleHttpResponse();
		_entryPoint.commence(request, response, null);
		fail("expected ServletException");
	}
}
