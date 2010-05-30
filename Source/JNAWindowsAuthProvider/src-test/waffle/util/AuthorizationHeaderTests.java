package waffle.util;

import waffle.apache.catalina.SimpleHttpRequest;
import junit.framework.TestCase;

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
}
