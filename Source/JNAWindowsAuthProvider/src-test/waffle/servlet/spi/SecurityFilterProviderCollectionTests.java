/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet.spi;

import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SecurityFilterProviderCollectionTests extends TestCase {
	
	public void testDefaultCollection() throws ClassNotFoundException {
		SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(new WindowsAuthProviderImpl());
		assertEquals(2, coll.size());
		assertNotNull(coll.getByClassName(NegotiateSecurityFilterProvider.class.getName()));
		assertNotNull(coll.getByClassName(BasicSecurityFilterProvider.class.getName()));
	}
	
	public void testGetByClassNameInvalid() {
		try {
			SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(new WindowsAuthProviderImpl());
			coll.getByClassName("classDoesNotExist");
			fail("expected ClassNotFoundException");
		} catch (ClassNotFoundException e) {
			// expected
		}
	}
	
	public void testIsSecurityPackageSupported() {
		SecurityFilterProviderCollection coll = new SecurityFilterProviderCollection(new WindowsAuthProviderImpl());
		assertTrue(coll.isSecurityPackageSupported("NTLM"));
		assertTrue(coll.isSecurityPackageSupported("Negotiate"));
		assertTrue(coll.isSecurityPackageSupported("Basic"));
		assertFalse(coll.isSecurityPackageSupported(""));
		assertFalse(coll.isSecurityPackageSupported("Invalid"));
	}
}
