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
