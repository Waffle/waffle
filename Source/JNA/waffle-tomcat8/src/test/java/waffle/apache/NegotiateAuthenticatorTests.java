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
package waffle.apache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import waffle.apache.catalina.SimpleContext;
import waffle.apache.catalina.SimpleEngine;
import waffle.apache.catalina.SimpleHttpRequest;
import waffle.apache.catalina.SimpleHttpResponse;
import waffle.apache.catalina.SimplePipeline;
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
 * 
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateAuthenticatorTests {

	private NegotiateAuthenticator _authenticator;

	@Before
	public void setUp() throws LifecycleException {
		_authenticator = new NegotiateAuthenticator();
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

	@After
	public void tearDown() throws LifecycleException {
		_authenticator.stop();
	}

	@Test
	public void testGetInfo() {
		assertTrue(_authenticator.getInfo().length() > 0);
		assertTrue(_authenticator.getAuth() instanceof WindowsAuthProviderImpl);
	}

	@Test
	public void testAllowGuestLogin() {
		assertTrue(_authenticator.isAllowGuestLogin());
		_authenticator.setAllowGuestLogin(false);
		assertFalse(_authenticator.isAllowGuestLogin());
	}

	@Test
	public void testPrincipalFormat() {
		assertEquals(PrincipalFormat.fqn, _authenticator.getPrincipalFormat());
		_authenticator.setPrincipalFormat("both");
		assertEquals(PrincipalFormat.both, _authenticator.getPrincipalFormat());
	}

	@Test
	public void testRoleFormat() {
		assertEquals(PrincipalFormat.fqn, _authenticator.getRoleFormat());
		_authenticator.setRoleFormat("both");
		assertEquals(PrincipalFormat.both, _authenticator.getRoleFormat());
	}

	@Test
	public void testChallengeGET() {
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		SimpleHttpResponse response = new SimpleHttpResponse();
		_authenticator.authenticate(request, response);
		String[] wwwAuthenticates = response
				.getHeaderValues("WWW-Authenticate");
		assertEquals(2, wwwAuthenticates.length);
		assertEquals("Negotiate", wwwAuthenticates[0]);
		assertEquals("NTLM", wwwAuthenticates[1]);
		assertEquals("close", response.getHeader("Connection"));
		assertEquals(2, response.getHeaderNames().size());
		assertEquals(401, response.getStatus());
	}

	@Test
	public void testChallengePOST() {
		String securityPackage = "Negotiate";
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		try {
			clientCredentials = WindowsCredentialsHandleImpl
					.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(WindowsAccountImpl
					.getCurrentUsername());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize(null, null,
					WindowsAccountImpl.getCurrentUsername());
			SimpleHttpRequest request = new SimpleHttpRequest();
			request.setMethod("POST");
			request.setContentLength(0);
			String clientToken = Base64.encode(clientContext.getToken());
			request.addHeader("Authorization", securityPackage + " "
					+ clientToken);
			SimpleHttpResponse response = new SimpleHttpResponse();
			_authenticator.authenticate(request, response);
			assertTrue(response.getHeader("WWW-Authenticate").startsWith(
					securityPackage + " "));
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

	@Ignore
	@Test
	public void testPOSTEmpty() {
		String securityPackage = "Negotiate";
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		try {
			// client credentials handle
			clientCredentials = WindowsCredentialsHandleImpl
					.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(WindowsAccountImpl
					.getCurrentUsername());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize(null, null,
					WindowsAccountImpl.getCurrentUsername());
			// negotiate
			boolean authenticated = false;
			SimpleHttpRequest request = new SimpleHttpRequest();
			request.setMethod("POST");
			request.setContentLength(0);
			while (true) {
				String clientToken = Base64.encode(clientContext.getToken());
				request.addHeader("Authorization", securityPackage + " "
						+ clientToken);

				SimpleHttpResponse response = new SimpleHttpResponse();
				authenticated = _authenticator.authenticate(request, response);

				if (authenticated) {
					assertTrue(response.getHeaderNames().size() >= 0);
					break;
				}

				assertTrue(response.getHeader("WWW-Authenticate").startsWith(
						securityPackage + " "));
				assertEquals("keep-alive", response.getHeader("Connection"));
				assertEquals(2, response.getHeaderNames().size());
				assertEquals(401, response.getStatus());
				String continueToken = response.getHeader("WWW-Authenticate")
						.substring(securityPackage.length() + 1);
				byte[] continueTokenBytes = Base64.decode(continueToken);
				assertTrue(continueTokenBytes.length > 0);
				SecBufferDesc continueTokenBuffer = new SecBufferDesc(
						Sspi.SECBUFFER_TOKEN, continueTokenBytes);
				clientContext.initialize(clientContext.getHandle(),
						continueTokenBuffer,
						WindowsAccountImpl.getCurrentUsername());
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

	@Test
	public void testNegotiate() {
		String securityPackage = "Negotiate";
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		try {
			// client credentials handle
			clientCredentials = WindowsCredentialsHandleImpl
					.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(WindowsAccountImpl
					.getCurrentUsername());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize(null, null,
					WindowsAccountImpl.getCurrentUsername());
			// negotiate
			boolean authenticated = false;
			SimpleHttpRequest request = new SimpleHttpRequest();
			while (true) {
				String clientToken = Base64.encode(clientContext.getToken());
				request.addHeader("Authorization", securityPackage + " "
						+ clientToken);

				SimpleHttpResponse response = new SimpleHttpResponse();
				authenticated = _authenticator.authenticate(request, response);

				if (authenticated) {
					assertNotNull(request.getUserPrincipal());
					assertTrue(request.getUserPrincipal() instanceof GenericWindowsPrincipal);
					GenericWindowsPrincipal windowsPrincipal = (GenericWindowsPrincipal) request
							.getUserPrincipal();
					assertTrue(windowsPrincipal.getSidString().startsWith("S-"));
					assertTrue(windowsPrincipal.getSid().length > 0);
					assertTrue(windowsPrincipal.getGroups().containsKey(
							"Everyone"));
					assertTrue(response.getHeaderNames().size() <= 1);
					break;
				}

				assertTrue(response.getHeader("WWW-Authenticate").startsWith(
						securityPackage + " "));
				assertEquals("keep-alive", response.getHeader("Connection"));
				assertEquals(2, response.getHeaderNames().size());
				assertEquals(401, response.getStatus());
				String continueToken = response.getHeader("WWW-Authenticate")
						.substring(securityPackage.length() + 1);
				byte[] continueTokenBytes = Base64.decode(continueToken);
				assertTrue(continueTokenBytes.length > 0);
				SecBufferDesc continueTokenBuffer = new SecBufferDesc(
						Sspi.SECBUFFER_TOKEN, continueTokenBytes);
				clientContext.initialize(clientContext.getHandle(),
						continueTokenBuffer,
						WindowsAccountImpl.getCurrentUsername());
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
