/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.util;

import waffle.apache.catalina.SimpleHttpRequest;
import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class AuthorizationHeaderTests extends TestCase {
	public void testIsNull() {
		SimpleHttpRequest request = new SimpleHttpRequest();
		AuthorizationHeader header = new AuthorizationHeader(request);
		assertTrue(header.isNull());
		request.addHeader("Authorization", "");
		assertTrue(header.isNull());
		request.addHeader("Authorization", "12344234");
		assertTrue(! header.isNull());
	}
	
	public void testGetSecurityPackage() {
		SimpleHttpRequest request = new SimpleHttpRequest();
		AuthorizationHeader header = new AuthorizationHeader(request);
		request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
		assertEquals("NTLM", header.getSecurityPackage());
		request.addHeader("Authorization", "Negotiate TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
		assertEquals("Negotiate", header.getSecurityPackage());
	}
	
	public void testIsNtlmType1Message() {
		SimpleHttpRequest request = new SimpleHttpRequest();
		AuthorizationHeader header = new AuthorizationHeader(request);
		assertFalse(header.isNtlmType1Message());
		request.addHeader("Authorization", "");
		assertFalse(header.isNtlmType1Message());
		request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
		assertTrue(header.isNtlmType1Message());	
	}
	
	public void testIsNtlmType1PostAuthorizationHeader() {
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setContentLength(0);
		request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
		// GET
		request.setMethod("GET");
		AuthorizationHeader header = new AuthorizationHeader(request);
		assertFalse(header.isNtlmType1PostAuthorizationHeader());
		// POST
		request.setMethod("POST");
		assertTrue(header.isNtlmType1PostAuthorizationHeader());
		// PUT
		request.setMethod("PUT");
		assertTrue(header.isNtlmType1PostAuthorizationHeader());
		// String(POST)
		request.setMethod(new String("POST"));
		assertTrue(header.isNtlmType1PostAuthorizationHeader());
	}
}
