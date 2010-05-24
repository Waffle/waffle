/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import junit.framework.TestCase;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsLoginModuleTests extends TestCase {
	WindowsLoginModule _loginModule = null;
	
	@Override
	public void setUp() {
		WindowsLoginModule.setAuth(new MockWindowsAuthProvider());
		_loginModule = new WindowsLoginModule();
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
}
