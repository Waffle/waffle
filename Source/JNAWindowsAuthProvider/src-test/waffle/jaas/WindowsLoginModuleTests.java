/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import junit.framework.TestCase;
import waffle.mock.MockWindowsAuthProvider;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsLoginModuleTests extends TestCase {
	WindowsLoginModule _loginModule = null;
	MockWindowsAuthProvider _provider = null;
	
	@Override
	public void setUp() {
		_provider = new MockWindowsAuthProvider();
		_loginModule = new WindowsLoginModule();
		_loginModule.setAuth(_provider);
	}

	@Override
	public void tearDown() {
		_loginModule = null;
	}

	public void testInitialize() {
		Subject subject = new Subject();
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("", "");
		Map<String, String> options = new HashMap<String, String>();
		options.put("debug", "true");
		_loginModule.initialize(subject, callbackHandler, null, options);
		assertTrue(_loginModule.getDebug());
	}
	
	public void testGetSetAuth() {
		assertNotNull(_loginModule.getAuth());
		_loginModule.setAuth(null);
		assertNull(_loginModule.getAuth());
	}

	public void testLogin() throws LoginException {
		Subject subject = new Subject();
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
				WindowsAccountImpl.getCurrentUsername(), "password");
		Map<String, String> options = new HashMap<String, String>();
		options.put("debug", "true");
		_loginModule.initialize(subject, callbackHandler, null, options);
		assertTrue(_loginModule.login());
		assertEquals(0, subject.getPrincipals().size());
		assertTrue(_loginModule.commit());
		assertEquals(3, subject.getPrincipals().size());
		assertTrue(subject.getPrincipals().contains("Everyone"));
		assertTrue(subject.getPrincipals().contains("Users"));
		assertTrue(subject.getPrincipals().contains(WindowsAccountImpl.getCurrentUsername()));
		assertTrue(_loginModule.logout());
		assertTrue(subject.getPrincipals().size() == 0);
	}
	
	public void testNoCallbackHandler() {
		Subject subject = new Subject();
		Map<String, String> options = new HashMap<String, String>();
		_loginModule.initialize(subject, null, null, options);
		try {
			_loginModule.login();
		} catch (Exception e) {
			assertTrue(e instanceof LoginException);
		}
	}
	
	public void testLoginNoUsername() {
		Subject subject = new Subject();
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("", "");
		Map<String, String> options = new HashMap<String, String>();
		options.put("debug", "true");
		_loginModule.initialize(subject, callbackHandler, null, options);
		try {
			assertFalse(_loginModule.login());
			fail("Expected LoginException");
		} catch(LoginException e) {
			assertTrue(e.getMessage().startsWith("Mock error: "));
		}
	}
	
	public void testRoleFormatNone() throws LoginException {
		Subject subject = new Subject();
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
				WindowsAccountImpl.getCurrentUsername(), "password");
		Map<String, String> options = new HashMap<String, String>();
		options.put("debug", "true");
		options.put("roleFormat", "none");
		_loginModule.initialize(subject, callbackHandler, null, options);
		assertTrue(_loginModule.login());
		assertTrue(_loginModule.commit());
		assertEquals(1, subject.getPrincipals().size());
	}
	
	public void testRoleFormatBoth() throws LoginException {
		Subject subject = new Subject();
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
				WindowsAccountImpl.getCurrentUsername(), "password");
		Map<String, String> options = new HashMap<String, String>();
		options.put("debug", "true");
		options.put("roleFormat", "both");
		_loginModule.initialize(subject, callbackHandler, null, options);
		assertTrue(_loginModule.login());
		assertTrue(_loginModule.commit());
		assertEquals(5, subject.getPrincipals().size());
	}
	
	public void testPrincipalFormatBoth() throws LoginException {
		Subject subject = new Subject();
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
				WindowsAccountImpl.getCurrentUsername(), "password");
		Map<String, String> options = new HashMap<String, String>();
		options.put("debug", "true");
		options.put("principalFormat", "both");
		options.put("roleFormat", "none");
		_loginModule.initialize(subject, callbackHandler, null, options);
		assertTrue(_loginModule.login());
		assertTrue(_loginModule.commit());
		assertEquals(2, subject.getPrincipals().size());
	}

	public void testRoleFormatSid() throws LoginException {
		Subject subject = new Subject();
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
				WindowsAccountImpl.getCurrentUsername(), "password");
		Map<String, String> options = new HashMap<String, String>();
		options.put("debug", "true");
		options.put("roleFormat", "sid");
		_loginModule.initialize(subject, callbackHandler, null, options);
		assertTrue(_loginModule.login());
		assertTrue(_loginModule.commit());
		assertEquals(3, subject.getPrincipals().size());
		Iterator<Principal> principals = subject.getPrincipals().iterator();
		assertTrue(principals.next().getName().equals(WindowsAccountImpl.getCurrentUsername()));
		assertTrue(principals.next().getName().startsWith("S-"));
		assertTrue(principals.next().getName().startsWith("S-"));
	}
	
	public void testRoleUnique() throws LoginException {
		Subject subject = new Subject();
		// the mock has an "Everyone" group
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
				WindowsAccountImpl.getCurrentUsername(), "password");
		_provider.addGroup("Group 1");
		_provider.addGroup("Group 1");
		Map<String, String> options = new HashMap<String, String>();
		options.put("debug", "true");
		_loginModule.initialize(subject, callbackHandler, null, options);
		assertTrue(_loginModule.login());
		assertTrue(_loginModule.commit());
		assertEquals(4, subject.getPrincipals().size());
	}
	
	public void testGuestLogin() throws LoginException {
		Subject subject = new Subject();
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("Guest", "password");
		Map<String, String> options = new HashMap<String, String>();
		options.put("debug", "true");
		_loginModule.initialize(subject, callbackHandler, null, options);
		assertTrue(_loginModule.getAllowGuestLogin());
		assertTrue(_loginModule.login());
		assertEquals(0, subject.getPrincipals().size());
		assertTrue(_loginModule.commit());
		assertEquals(3, subject.getPrincipals().size());
		assertTrue(subject.getPrincipals().contains("Everyone"));
		assertTrue(subject.getPrincipals().contains("Users"));
		_loginModule.setAllowGuestLogin(false);
		try {
			assertTrue(_loginModule.login());
			fail("expected LoginException");
		} catch (Exception e) {
			assertTrue(e instanceof LoginException);
		}		
	}
	
	public void testAbort() throws LoginException {
		Subject subject = new Subject();
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler("Guest", "password");
		Map<String, String> options = new HashMap<String, String>();
		options.put("debug", "true");
		_loginModule.initialize(subject, callbackHandler, null, options);
		assertTrue(_loginModule.login());
		_loginModule.abort();
		assertTrue(subject.getPrincipals().size() == 0);
	}
}
