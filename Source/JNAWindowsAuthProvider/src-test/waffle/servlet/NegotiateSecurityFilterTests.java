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
import waffle.util.Base64;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

/**
 * Waffle Tomcat Security Filter Tests
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterTests extends TestCase {

	NegotiateSecurityFilter _filter = null;
	
	@Override
	public void setUp() {
		_filter = new NegotiateSecurityFilter();
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
	
	public void testChallengeGET() throws IOException, ServletException {
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		SimpleHttpResponse response = new SimpleHttpResponse();
		_filter.doFilter(request, response, null);
		String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
		assertEquals(2, wwwAuthenticates.length);
		assertEquals("Negotiate", wwwAuthenticates[0]);
		assertEquals("NTLM", wwwAuthenticates[1]);
		assertEquals("close", response.getHeader("Connection"));
		assertEquals(2, response.getHeaderNames().length);
		assertEquals(401, response.getStatus());
	}
	
	public void testChallengePOST() throws IOException, ServletException {
		String securityPackage = "Negotiate";
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		try {
			// client credentials handle
			clientCredentials = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(Advapi32Util.getUserName());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize();
			SimpleHttpRequest request = new SimpleHttpRequest();
			request.setMethod("POST");
			request.setContentLength(0);
			String clientToken = Base64.encode(clientContext.getToken());
			request.addHeader("Authorization", securityPackage + " " + clientToken);
			SimpleHttpResponse response = new SimpleHttpResponse();
			_filter.doFilter(request, response, null);
			assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
			assertEquals("keep-alive", response.getHeader("Connection"));
			assertEquals(2, response.getHeaderNames().length);
			assertEquals(401, response.getStatus());
		} finally {
			if (clientContext != null) {
				clientContext.dispose();
			}
			if (clientCredentials != null) {
				clientCredentials.dispose();
			}			
		}
	}

	
	public void testNegotiate() throws IOException, ServletException {
		String securityPackage = "Negotiate";
		// client credentials handle
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		try {
			// client credentials handle
			clientCredentials = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(Advapi32Util.getUserName());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize();
			// filter chain
			FilterChain filterChain = new SimpleFilterChain();
			// negotiate
			boolean authenticated = false;
			SimpleHttpRequest request = new SimpleHttpRequest();
	        while(true)
	        {
	    		String clientToken = Base64.encode(clientContext.getToken());
	    		request.addHeader("Authorization", securityPackage + " " + clientToken);
	    		
	    		SimpleHttpResponse response = new SimpleHttpResponse();
	    		_filter.doFilter(request, response, filterChain);
	    		
	    		Subject subject = (Subject) request.getSession().getAttribute("javax.security.auth.subject");
	    		authenticated = (subject != null && subject.getPrincipals().size() > 0);
	
	    		if (authenticated) {
	        		assertEquals(0, response.getHeaderNames().length);
	    			break;
	    		}
	    		
	    		assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
	    		assertEquals("keep-alive", response.getHeader("Connection"));
	    		assertEquals(2, response.getHeaderNames().length);
	    		assertEquals(401, response.getStatus());
	    		String continueToken = response.getHeader("WWW-Authenticate").substring(securityPackage.length() + 1);
	    		byte[] continueTokenBytes = Base64.decode(continueToken);
	    		assertTrue(continueTokenBytes.length > 0);
	            SecBufferDesc continueTokenBuffer = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, continueTokenBytes);
	            clientContext.initialize(clientContext.getHandle(), continueTokenBuffer);
	        }        
	        assertTrue(authenticated);
		} finally {
			if (clientContext != null) {
				clientContext.dispose();
			}
			if (clientCredentials != null) {
				clientCredentials.dispose();
			}			
		}
	}
}
