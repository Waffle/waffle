package waffle.util;

import waffle.apache.catalina.SimpleHttpRequest;
import junit.framework.TestCase;

public class NtlmServletRequestTests extends TestCase {
	public void testGetConnectionId() {
		SimpleHttpRequest request1 = new SimpleHttpRequest();
		assertEquals(":1", NtlmServletRequest.getConnectionId(request1));
		SimpleHttpRequest request2 = new SimpleHttpRequest();
		assertEquals(":2", NtlmServletRequest.getConnectionId(request2));
		request2.setRemoteAddr("192.168.1.1");
		assertEquals("192.168.1.1:2", NtlmServletRequest.getConnectionId(request2));
		request2.setRemoteHost("codeplex.com");
		assertEquals("codeplex.com:2", NtlmServletRequest.getConnectionId(request2));
	}
}
