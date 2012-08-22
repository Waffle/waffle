package waffle.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import waffle.mock.MockWindowsAccount;
import waffle.mock.http.*;
import waffle.util.Base64;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.LMAccess;
import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.Netapi32;

public class ImpersonateTests {

	private NegotiateSecurityFilter _filter = null;
	private LMAccess.USER_INFO_1 _userInfo;

	@Before
	public void setUp() {
		_filter = new NegotiateSecurityFilter();
		_filter.setAuth(new WindowsAuthProviderImpl());
		try {
			_filter.init(null);
		} catch (ServletException e) {
			fail(e.getMessage());
		}

		_userInfo = new LMAccess.USER_INFO_1();
		_userInfo.usri1_name = new WString(MockWindowsAccount.TEST_USER_NAME);
		_userInfo.usri1_password = new WString(MockWindowsAccount.TEST_PASSWORD);
		_userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
		assertEquals(LMErr.NERR_Success,
				Netapi32.INSTANCE.NetUserAdd(null, 1, _userInfo, null));
	}

	@After
	public void tearDown() {
		_filter.destroy();

		assertEquals(
				LMErr.NERR_Success,
				Netapi32.INSTANCE.NetUserDel(null,
						_userInfo.usri1_name.toString()));
	}

	@Test
	public void testImpersonateEnabled() throws IOException, ServletException {

		assertFalse(
				"Current user shouldn't be the test user prior to the test",
				Advapi32Util.getUserName().equals(
						MockWindowsAccount.TEST_USER_NAME));
		SimpleHttpRequest request = new SimpleHttpRequest();
		
		//SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		String userHeaderValue = MockWindowsAccount.TEST_USER_NAME + ":"
				+ MockWindowsAccount.TEST_PASSWORD;
		String basicAuthHeader = "Basic "
				+ Base64.encode(userHeaderValue.getBytes());
		request.addHeader("Authorization", basicAuthHeader);
		
		SimpleHttpResponse response = new SimpleHttpResponse();
		RecordUserNameFilterChain filterChain = new RecordUserNameFilterChain();

		AutoDisposableWindowsPrincipal windowsPrincipal = null;
		try {
			_filter.setImpersonate(true);
			_filter.doFilter(request, response, filterChain);

			Subject subject = (Subject) request.getSession().getAttribute(
					"javax.security.auth.subject");
			boolean authenticated = (subject != null && subject.getPrincipals()
					.size() > 0);
			assertTrue("Test user should be authenticated", authenticated);

			Principal principal = subject.getPrincipals().iterator().next();
			assertTrue(principal instanceof AutoDisposableWindowsPrincipal);
			windowsPrincipal = (AutoDisposableWindowsPrincipal) principal;

			assertEquals("Test user should be impersonated",
					MockWindowsAccount.TEST_USER_NAME,
					filterChain.getUserName());
			assertFalse(
					"Impersonation context should have been reverted",
					Advapi32Util.getUserName().equals(
							MockWindowsAccount.TEST_USER_NAME));
		} finally {
			if (windowsPrincipal != null) {
				windowsPrincipal.getIdentity().dispose();
			}
		}
	}

	@Test
	public void testImpersonateDisabled() throws IOException, ServletException {

		assertFalse(
				"Current user shouldn't be the test user prior to the test",
				Advapi32Util.getUserName().equals(
						MockWindowsAccount.TEST_USER_NAME));
		SimpleHttpRequest request = new SimpleHttpRequest();
		request.setMethod("GET");
		String userHeaderValue = MockWindowsAccount.TEST_USER_NAME + ":"
				+ MockWindowsAccount.TEST_PASSWORD;
		String basicAuthHeader = "Basic "
				+ Base64.encode(userHeaderValue.getBytes());
		request.addHeader("Authorization", basicAuthHeader);
		SimpleHttpResponse response = new SimpleHttpResponse();
		RecordUserNameFilterChain filterChain = new RecordUserNameFilterChain();

		WindowsPrincipal windowsPrincipal = null;
		try {
			_filter.setImpersonate(false);
			_filter.doFilter(request, response, filterChain);

			Subject subject = (Subject) request.getSession().getAttribute(
					"javax.security.auth.subject");
			boolean authenticated = (subject != null && subject.getPrincipals()
					.size() > 0);
			assertTrue("Test user should be authenticated", authenticated);

			Principal principal = subject.getPrincipals().iterator().next();
			assertTrue(principal instanceof WindowsPrincipal);
			windowsPrincipal = (WindowsPrincipal) principal;

			assertFalse("Test user should not be impersonated",
					MockWindowsAccount.TEST_USER_NAME.equals(filterChain
							.getUserName()));
			assertFalse(
					"Impersonation context should have been reverted",
					Advapi32Util.getUserName().equals(
							MockWindowsAccount.TEST_USER_NAME));
		} finally {
			if (windowsPrincipal != null) {
				windowsPrincipal.getIdentity().dispose();
			}
		}
	}

	/**
	 * Filter chain that records current username
	 */
	public class RecordUserNameFilterChain extends SimpleFilterChain {
		private String userName;

		@Override
		public void doFilter(ServletRequest sreq, ServletResponse srep)
				throws IOException, ServletException {
			userName = Advapi32Util.getUserName();
		}

		public String getUserName() {
			return userName;
		}
	}

}
