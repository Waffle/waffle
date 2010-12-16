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
package waffle.apache;

import junit.framework.TestCase;

import org.apache.catalina.Realm;

import waffle.apache.catalina.SimpleContext;
import waffle.apache.catalina.SimpleHttpRequest;
import waffle.apache.catalina.SimpleHttpResponse;
import waffle.apache.catalina.SimpleRealm;
import waffle.util.Base64;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

/**
 * Waffle Tomcat Authenticator Tests
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateAuthenticatorTests extends TestCase {

	NegotiateAuthenticator _authenticator = null;
	
	@Override
	public void setUp() {
		_authenticator = new NegotiateAuthenticator();
		SimpleContext ctx = new SimpleContext();
		Realm realm = new SimpleRealm();
		ctx.setRealm(realm);
		_authenticator.setContainer(ctx);		
		_authenticator.start();
	}

	@Override
	public void tearDown() {
		_authenticator.stop();
		_authenticator = null;
	}
	
	public void testGetInfo() {
		assertTrue(_authenticator.getInfo().length() > 0);
		assertTrue(_authenticator.getAuth() instanceof WindowsAuthProviderImpl);
	}
	
	public void testAllowGuestLogin() {
		assertTrue(_authenticator.getAllowGuestLogin());
		_authenticator.setAllowGuestLogin(false);
		assertFalse(_authenticator.getAllowGuestLogin());		
	}
	
	public void testPrincipalFormat() {
		assertEquals(PrincipalFormat.fqn, _authenticator.getPrincipalFormat());
		_authenticator.setPrincipalFormat("both");
		assertEquals(PrincipalFormat.both, _authenticator.getPrincipalFormat());		
	}

	public void testRoleFormat() {
		assertEquals(PrincipalFormat.fqn, _authenticator.getRoleFormat());
		_authenticator.setRoleFormat("both");
		assertEquals(PrincipalFormat.both, _authenticator.getRoleFormat());		
	}
		
	public void testChallengeGET() {
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		SimpleHttpResponse response = new SimpleHttpResponse();
		_authenticator.authenticate(request, response, null);
		String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
		assertEquals(2, wwwAuthenticates.length);
		assertEquals("Negotiate", wwwAuthenticates[0]);
		assertEquals("NTLM", wwwAuthenticates[1]);
		assertEquals("close", response.getHeader("Connection"));
		assertEquals(2, response.getHeaderNames().length);
		assertEquals(401, response.getStatus());
	}
	
	public void testChallengePOST() {
		String securityPackage = "Negotiate";
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		try {			
			clientCredentials = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize(null, null, WindowsAccountImpl.getCurrentUsername());
			SimpleHttpRequest request = new SimpleHttpRequest();
			request.setMethod("POST");
			request.setContentLength(0);
			String clientToken = Base64.encode(clientContext.getToken());
			request.addHeader("Authorization", securityPackage + " " + clientToken);
			SimpleHttpResponse response = new SimpleHttpResponse();
			_authenticator.authenticate(request, response, null);
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

	public void testPOSTEmpty() {
		String securityPackage = "Negotiate";
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		try {
			// client credentials handle
			clientCredentials = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize(null, null, WindowsAccountImpl.getCurrentUsername());
			// negotiate
			boolean authenticated = false;
			SimpleHttpRequest request = new SimpleHttpRequest();
			request.setMethod("POST");
			request.setContentLength(0);
	        while(true)
	        {
	    		String clientToken = Base64.encode(clientContext.getToken());
	    		request.addHeader("Authorization", securityPackage + " " + clientToken);
	    		
	    		SimpleHttpResponse response = new SimpleHttpResponse();
	    		authenticated = _authenticator.authenticate(request, response, null);
	
	    		if (authenticated) {
	        		assertTrue(response.getHeaderNames().length >= 0);
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
	            clientContext.initialize(clientContext.getHandle(), continueTokenBuffer, WindowsAccountImpl.getCurrentUsername());
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
	
	public void testNegotiate() {
		String securityPackage = "Negotiate";
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		try {
			// client credentials handle
			clientCredentials = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize(null, null, WindowsAccountImpl.getCurrentUsername());
			// negotiate
			boolean authenticated = false;
			SimpleHttpRequest request = new SimpleHttpRequest();
	        while(true)
	        {
	    		String clientToken = Base64.encode(clientContext.getToken());
	    		request.addHeader("Authorization", securityPackage + " " + clientToken);

	    		SimpleHttpResponse response = new SimpleHttpResponse();
	    		authenticated = _authenticator.authenticate(request, response, null);
	
	    		if (authenticated) {
	    			assertNotNull(request.getUserPrincipal());
	    			assertTrue(request.getUserPrincipal() instanceof GenericWindowsPrincipal);
	    			GenericWindowsPrincipal windowsPrincipal = (GenericWindowsPrincipal) request.getUserPrincipal();
	    			assertTrue(windowsPrincipal.getSidString().startsWith("S-"));
	    			assertTrue(windowsPrincipal.getSid().length > 0);
	    			assertTrue(windowsPrincipal.getGroups().containsKey("Everyone"));
	        		assertTrue(response.getHeaderNames().length <= 1);
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
	            clientContext.initialize(clientContext.getHandle(), continueTokenBuffer, WindowsAccountImpl.getCurrentUsername());
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
