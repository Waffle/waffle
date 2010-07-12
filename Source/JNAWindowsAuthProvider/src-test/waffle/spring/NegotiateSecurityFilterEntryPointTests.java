/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.spring;

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import waffle.apache.catalina.SimpleHttpRequest;
import waffle.apache.catalina.SimpleHttpResponse;

/**
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterEntryPointTests extends TestCase {

	private NegotiateSecurityFilterEntryPoint _entryPoint = null;
	
	@Override
	public void setUp() {
		String[] configFiles = new String[] { "springTestFilterBeans.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(configFiles);	
		_entryPoint = (NegotiateSecurityFilterEntryPoint) ctx.getBean("negotiateSecurityFilterEntryPoint");
	}

	@Override
	public void tearDown() {
		_entryPoint = null;
	}
	
	public void testChallengeGET() throws IOException, ServletException {
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		SimpleHttpResponse response = new SimpleHttpResponse();
		_entryPoint.commence(request, response, null);
		String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
		assertEquals(3, wwwAuthenticates.length);
		assertEquals("NTLM", wwwAuthenticates[0]);
		assertEquals("Negotiate", wwwAuthenticates[1]);
		assertTrue(wwwAuthenticates[2].equals("Basic realm=\"TestRealm\""));
		assertEquals(2, response.getHeaderNames().length);
		assertEquals("keep-alive", response.getHeader("Connection"));
		assertEquals(401, response.getStatus());
	}	
	
	public void testGetSetProvider() {
		try {
			assertNotNull(_entryPoint.getProvider());
			_entryPoint.setProvider(null);
			SimpleHttpRequest request = new SimpleHttpRequest();
			request.setMethod("GET");
			SimpleHttpResponse response = new SimpleHttpResponse();
			_entryPoint.commence(request, response, null);
			fail("expected ServletException");
		} catch (Exception e) {
			assertTrue(e instanceof ServletException);
		}
			
	}
}
