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

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.deploy.LoginConfig;

import waffle.apache.catalina.SimpleContext;
import waffle.apache.catalina.SimpleEngine;
import waffle.apache.catalina.SimpleHttpRequest;
import waffle.apache.catalina.SimpleHttpResponse;
import waffle.apache.catalina.SimplePipeline;
import waffle.apache.catalina.SimpleRealm;
import waffle.mock.MockWindowsAuthProvider;
import waffle.util.Base64;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

/**
 * Waffle Tomcat Mixed Authenticator Tests
 * @author dblock[at]dblock[dot]org
 */
public class MixedAuthenticatorTests extends TestCase {

	MixedAuthenticator _authenticator = null;
	
	@Override
	public void setUp() throws LifecycleException {
		_authenticator = new MixedAuthenticator();
		SimpleContext ctx = new SimpleContext();
		Realm realm = new SimpleRealm();
		ctx.setRealm(realm);
		SimpleEngine engine = new SimpleEngine();
		ctx.setParent(engine);
		SimplePipeline pipeline = new SimplePipeline();
		engine.setPipeline(pipeline);
		ctx.setPipeline(pipeline);
		_authenticator.setContainer(ctx);		
		_authenticator.start();
	}

	@Override
	public void tearDown() throws LifecycleException {
		_authenticator.stop();
		_authenticator = null;
	}
	
	public void testGetInfo() {
		assertTrue(_authenticator.getInfo().length() > 0);		
	}

	public void testChallengeGET() {
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		request.setQueryString("j_negotiate_check");
		SimpleHttpResponse response = new SimpleHttpResponse();
		_authenticator.authenticate(request, response, null);
		String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
		assertEquals(2, wwwAuthenticates.length);
		assertEquals("Negotiate", wwwAuthenticates[0]);
		assertEquals("NTLM", wwwAuthenticates[1]);
		assertEquals("close", response.getHeader("Connection"));
		assertEquals(2, response.getHeaderNames().size());
		assertEquals(401, response.getStatus());
	}
	
	public void testChallengePOST() {
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
			SimpleHttpRequest request = new SimpleHttpRequest();
			request.setQueryString("j_negotiate_check");
			request.setMethod("POST");
			request.setContentLength(0);
			String clientToken = Base64.encode(clientContext.getToken());
			request.addHeader("Authorization", securityPackage + " " + clientToken);
			SimpleHttpResponse response = new SimpleHttpResponse();
			_authenticator.authenticate(request, response, null);
			assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
			assertEquals("keep-alive", response.getHeader("Connection"));
			assertEquals(2, response.getHeaderNames().size());
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
	
	public void testNegotiate() {
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
			clientContext.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize(null, null, WindowsAccountImpl.getCurrentUsername());
			// negotiate
			boolean authenticated = false;
			SimpleHttpRequest request = new SimpleHttpRequest();
			request.setQueryString("j_negotiate_check");
	        while(true)
	        {
	    		String clientToken = Base64.encode(clientContext.getToken());
	    		request.addHeader("Authorization", securityPackage + " " + clientToken);
	    		
	    		SimpleHttpResponse response = new SimpleHttpResponse();
	    		authenticated = _authenticator.authenticate(request, response, null);
	
	    		if (authenticated) {
            assertTrue(response.getHeaderNames().size() >= 0);
	    			break;
	    		}
	    		
	    		assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
	    		assertEquals("keep-alive", response.getHeader("Connection"));
	    		assertEquals(2, response.getHeaderNames().size());
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

	public void testGet() {
		LoginConfig loginConfig = new LoginConfig();
		loginConfig.setErrorPage("error.html");
		loginConfig.setLoginPage("login.html");
		SimpleHttpRequest request = new SimpleHttpRequest();
		SimpleHttpResponse response = new SimpleHttpResponse();
		assertFalse(_authenticator.authenticate(request, response, loginConfig));
		assertEquals(304, response.getStatus());
		assertEquals("login.html", response.getHeader("Location"));
		assertEquals(1, response.getHeaderNames().size());
	}

	public void testPostSecurityCheck() {
		LoginConfig loginConfig = new LoginConfig();
		loginConfig.setErrorPage("error.html");
		loginConfig.setLoginPage("login.html");
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setQueryString("j_security_check");
		request.addParameter("j_username", "username");
		request.addParameter("j_password", "password");
		SimpleHttpResponse response = new SimpleHttpResponse();
		assertFalse(_authenticator.authenticate(request, response, loginConfig));
		assertEquals(304, response.getStatus());
		assertEquals("error.html", response.getHeader("Location"));
		assertEquals(1, response.getHeaderNames().size());
	}

	public void testSecurityCheckQueryString() {
		_authenticator.setAuth(new MockWindowsAuthProvider());
		LoginConfig loginConfig = new LoginConfig();
		loginConfig.setErrorPage("error.html");
		loginConfig.setLoginPage("login.html");
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setQueryString("j_security_check");
		request.addParameter("j_username", WindowsAccountImpl.getCurrentUsername());
		request.addParameter("j_password", "");
		SimpleHttpResponse response = new SimpleHttpResponse();
		assertTrue(_authenticator.authenticate(request, response, loginConfig));
	}
	
	public void testSecurityCheckParameters() {
		_authenticator.setAuth(new MockWindowsAuthProvider());
		LoginConfig loginConfig = new LoginConfig();
		loginConfig.setErrorPage("error.html");
		loginConfig.setLoginPage("login.html");
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.addParameter("j_security_check", "");
		request.addParameter("j_username", WindowsAccountImpl.getCurrentUsername());
		request.addParameter("j_password", "");
		SimpleHttpResponse response = new SimpleHttpResponse();
		assertTrue(_authenticator.authenticate(request, response, loginConfig));
	}
}
