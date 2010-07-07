/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.security.auth.Subject;
import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.apache.catalina.realm.GenericPrincipal;

import waffle.apache.catalina.SimpleFilterChain;
import waffle.apache.catalina.SimpleHttpRequest;
import waffle.apache.catalina.SimpleHttpResponse;
import waffle.mock.MockWindowsIdentity;
import waffle.util.Base64;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Secur32Util;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
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
		_filter.setAuth(new WindowsAuthProviderImpl());		
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
		assertEquals(3, wwwAuthenticates.length);
		assertEquals("Negotiate", wwwAuthenticates[0]);
		assertEquals("NTLM", wwwAuthenticates[1]);
		assertTrue(wwwAuthenticates[2].startsWith("Basic realm=\""));
		assertEquals(2, response.getHeaderNames().length);
		assertEquals("keep-alive", response.getHeader("Connection"));
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
			SimpleFilterChain filterChain = new SimpleFilterChain();
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
	        assertTrue(filterChain.getRequest() instanceof NegotiateRequestWrapper);
	        assertTrue(filterChain.getResponse() instanceof SimpleHttpResponse);	        
	        NegotiateRequestWrapper wrappedRequest = (NegotiateRequestWrapper) filterChain.getRequest();
	        assertEquals("NEGOTIATE", wrappedRequest.getAuthType());
	        assertEquals(Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible), 
	        		wrappedRequest.getRemoteUser());
	        assertTrue(wrappedRequest.getUserPrincipal() instanceof WindowsPrincipal);
	        assertTrue(wrappedRequest.isUserInRole("Everyone"));
		} finally {
			if (clientContext != null) {
				clientContext.dispose();
			}
			if (clientCredentials != null) {
				clientCredentials.dispose();
			}			
		}
	}
	
	public void testNegotiatePreviousAuthWithWindowsPrincipal() throws IOException, ServletException {
		MockWindowsIdentity mockWindowsIdentity = new MockWindowsIdentity("user", new ArrayList<String>());
		SimpleHttpRequest request = new SimpleHttpRequest();
		WindowsPrincipal windowsPrincipal = new WindowsPrincipal(mockWindowsIdentity);
		request.setUserPrincipal(windowsPrincipal);
		SimpleFilterChain filterChain = new SimpleFilterChain();
		SimpleHttpResponse response = new SimpleHttpResponse();
		_filter.doFilter(request, response, filterChain);
		assertTrue(filterChain.getRequest() instanceof NegotiateRequestWrapper);		
        NegotiateRequestWrapper wrappedRequest = (NegotiateRequestWrapper) filterChain.getRequest();
        assertTrue(wrappedRequest.getUserPrincipal() instanceof WindowsPrincipal);
        assertEquals(windowsPrincipal, wrappedRequest.getUserPrincipal());
	}

	public void testNegotiatePreviousAuthWithGenericPrincipal() 
		throws IOException, ServletException {
		GenericPrincipal genericPrincipal = new GenericPrincipal(null, "name", "password");
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setUserPrincipal(genericPrincipal);
		SimpleFilterChain filterChain = new SimpleFilterChain();
		SimpleHttpResponse response = new SimpleHttpResponse();
		_filter.doFilter(request, response, filterChain);
		assertTrue(filterChain.getRequest() instanceof SimpleHttpRequest);
        assertTrue(request.getUserPrincipal() instanceof GenericPrincipal);
        assertEquals(genericPrincipal, request.getUserPrincipal());
	}
	
	public void testChallengeNTLMPOST() throws IOException, ServletException {
		MockWindowsIdentity mockWindowsIdentity = new MockWindowsIdentity("user", new ArrayList<String>());
		SimpleHttpRequest request = new SimpleHttpRequest();
		WindowsPrincipal windowsPrincipal = new WindowsPrincipal(mockWindowsIdentity);
		request.setUserPrincipal(windowsPrincipal);
		request.setMethod("POST");
		request.setContentLength(0);
		request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
		SimpleFilterChain filterChain = new SimpleFilterChain();
		SimpleHttpResponse response = new SimpleHttpResponse();
		_filter.doFilter(request, response, filterChain);
		assertEquals(401, response.getStatus());
		String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
		assertEquals(1, wwwAuthenticates.length);
		assertTrue(wwwAuthenticates[0].startsWith("NTLM "));
		assertEquals(2, response.getHeaderNames().length);
		assertEquals("keep-alive", response.getHeader("Connection"));
		assertEquals(401, response.getStatus());
	}

	public void testChallengeNTLMPUT() throws IOException, ServletException {
		MockWindowsIdentity mockWindowsIdentity = new MockWindowsIdentity("user", new ArrayList<String>());
		SimpleHttpRequest request = new SimpleHttpRequest();
		WindowsPrincipal windowsPrincipal = new WindowsPrincipal(mockWindowsIdentity);
		request.setUserPrincipal(windowsPrincipal);
		request.setMethod("PUT");
		request.setContentLength(0);
		request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
		SimpleFilterChain filterChain = new SimpleFilterChain();
		SimpleHttpResponse response = new SimpleHttpResponse();
		_filter.doFilter(request, response, filterChain);
		assertEquals(401, response.getStatus());
		String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
		assertEquals(1, wwwAuthenticates.length);
		assertTrue(wwwAuthenticates[0].startsWith("NTLM "));
		assertEquals(2, response.getHeaderNames().length);
		assertEquals("keep-alive", response.getHeader("Connection"));
		assertEquals(401, response.getStatus());
	}
	
	public void testInitBasicSecurityFilterProvider() throws ServletException {
		SimpleFilterConfig filterConfig = new SimpleFilterConfig();
		filterConfig.setParameter("principalFormat", "sid");
		filterConfig.setParameter("roleFormat", "none");
		filterConfig.setParameter("allowGuestLogin", "true");
		filterConfig.setParameter("securityFilterProviders", "waffle.servlet.spi.BasicSecurityFilterProvider\n");
		filterConfig.setParameter("waffle.servlet.spi.BasicSecurityFilterProvider/realm", "DemoRealm");
		_filter.init(filterConfig);
		assertEquals(_filter.getPrincipalFormat(), PrincipalFormat.sid);
		assertEquals(_filter.getRoleFormat(), PrincipalFormat.none);
		assertTrue(_filter.getAllowGuestLogin());
		assertEquals(1, _filter.getProviders().size());
	}
	
	public void testInitNegotiateSecurityFilterProvider() throws ServletException {
		SimpleFilterConfig filterConfig = new SimpleFilterConfig();
		filterConfig.setParameter("securityFilterProviders", "waffle.servlet.spi.NegotiateSecurityFilterProvider\n");
		filterConfig.setParameter("waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols", "NTLM");		
		_filter.init(filterConfig);
		assertEquals(_filter.getPrincipalFormat(), PrincipalFormat.fqn);
		assertEquals(_filter.getRoleFormat(), PrincipalFormat.fqn);
		assertTrue(_filter.getAllowGuestLogin());
		assertEquals(1, _filter.getProviders().size());
	}

	public void testInitNegotiateSecurityFilterProviderInvalidProtocol() throws ServletException {
		SimpleFilterConfig filterConfig = new SimpleFilterConfig();
		filterConfig.setParameter("securityFilterProviders", "waffle.servlet.spi.NegotiateSecurityFilterProvider\n");
		filterConfig.setParameter("waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols", "INVALID");		
		try {
			_filter.init(filterConfig);
			fail("expected ServletException");
		} catch (ServletException e) {
			assertEquals("java.lang.RuntimeException: Unsupported protocol: INVALID", e.getMessage());
		}
	}
	
	public void testInitInvalidParameter() {
		try {
			SimpleFilterConfig filterConfig = new SimpleFilterConfig();
			filterConfig.setParameter("invalidParameter", "random");
			_filter.init(filterConfig);
			fail("expected ServletException");
		} catch (ServletException e) {
			assertEquals("Invalid parameter: invalidParameter", e.getMessage());
		}
	}
	
	public void testInitInvalidClassInParameter() {
		try {
			SimpleFilterConfig filterConfig = new SimpleFilterConfig();
			filterConfig.setParameter("invalidClass/invalidParameter", "random");
			_filter.init(filterConfig);
			fail("expected ServletException");
		} catch (ServletException e) {
			assertEquals("java.lang.ClassNotFoundException: invalidClass", e.getMessage());
		}
	}
}
