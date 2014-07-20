package waffle.apache;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WaffleAuthenticatorBaseTest {

	WaffleAuthenticatorBase	waffleAuthenticatorBase;

	@Before
	public void init() {
		waffleAuthenticatorBase = new WaffleAuthenticatorBase() {
			{
				_log = LoggerFactory.getLogger(WaffleAuthenticatorBaseTest.class);
			}

			@Override
			public boolean authenticate(Request request, Response response, LoginConfig loginConfig) throws IOException {
				return false;
			}
		};
	}

	@Test
	public void should_accept_NTLM_protocol() throws Exception {
		waffleAuthenticatorBase.setProtocols("  NTLM ");

		assertEquals("One protocol added", 1, waffleAuthenticatorBase._protocols.size());
		assertEquals("NTLM", waffleAuthenticatorBase._protocols.iterator().next());
	}

	@Test
	public void should_accept_Negotiate_protocol() throws Exception {
		waffleAuthenticatorBase.setProtocols(" Negotiate  ");

		assertEquals("One protocol added", 1, waffleAuthenticatorBase._protocols.size());
		assertEquals("Negotiate", waffleAuthenticatorBase._protocols.iterator().next());
	}

	@Test
	public void should_accept_both_protocols() throws Exception {
		waffleAuthenticatorBase.setProtocols("  NTLM , , Negotiate   ");

		assertEquals("Two protocols added", 2, waffleAuthenticatorBase._protocols.size());
		assertTrue("NTLM has been added", waffleAuthenticatorBase._protocols.contains("NTLM"));
		assertTrue("Negotiate has been added", waffleAuthenticatorBase._protocols.contains("Negotiate"));
	}

	@Test(expected = RuntimeException.class)
	public void should_refuse_other_protocol() throws Exception {
		waffleAuthenticatorBase.setProtocols("  NTLM , OTHER, Negotiate   ");
	}
}