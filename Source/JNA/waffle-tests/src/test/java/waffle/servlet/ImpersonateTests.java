/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

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
import waffle.mock.http.SimpleFilterChain;
import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import com.google.common.io.BaseEncoding;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.LMAccess;
import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.Netapi32;

public class ImpersonateTests {

    private NegotiateSecurityFilter filter;
    private LMAccess.USER_INFO_1    userInfo;
    private int                     resultOfNetAddUser;

    @Before
    public void setUp() {
        this.filter = new NegotiateSecurityFilter();
        this.filter.setAuth(new WindowsAuthProviderImpl());
        try {
            this.filter.init(null);
        } catch (ServletException e) {
            fail(e.getMessage());
        }

        this.userInfo = new LMAccess.USER_INFO_1();
        this.userInfo.usri1_name = new WString(MockWindowsAccount.TEST_USER_NAME);
        this.userInfo.usri1_password = new WString(MockWindowsAccount.TEST_PASSWORD);
        this.userInfo.usri1_priv = LMAccess.USER_PRIV_USER;

        this.resultOfNetAddUser = Netapi32.INSTANCE.NetUserAdd(null, 1, this.userInfo, null);
        // ignore test if not able to add user (need to be administrator to do this).
        assumeTrue(LMErr.NERR_Success == this.resultOfNetAddUser);
    }

    @After
    public void tearDown() {
        this.filter.destroy();

        if (LMErr.NERR_Success == this.resultOfNetAddUser) {
            assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(null, this.userInfo.usri1_name.toString()));
        }
    }

    @Test
    public void testImpersonateEnabled() throws IOException, ServletException {

        assertFalse("Current user shouldn't be the test user prior to the test",
                Advapi32Util.getUserName().equals(MockWindowsAccount.TEST_USER_NAME));

        SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        String userHeaderValue = MockWindowsAccount.TEST_USER_NAME + ":" + MockWindowsAccount.TEST_PASSWORD;
        String basicAuthHeader = "Basic " + BaseEncoding.base64().encode(userHeaderValue.getBytes());
        request.addHeader("Authorization", basicAuthHeader);

        SimpleHttpResponse response = new SimpleHttpResponse();
        RecordUserNameFilterChain filterChain = new RecordUserNameFilterChain();

        AutoDisposableWindowsPrincipal windowsPrincipal = null;
        try {
            this.filter.setImpersonate(true);
            this.filter.doFilter(request, response, filterChain);

            Subject subject = (Subject) request.getSession().getAttribute("javax.security.auth.subject");
            boolean authenticated = (subject != null && subject.getPrincipals().size() > 0);
            assertTrue("Test user should be authenticated", authenticated);

            if (subject == null) {
                return;
            }
            Principal principal = subject.getPrincipals().iterator().next();
            assertTrue(principal instanceof AutoDisposableWindowsPrincipal);
            windowsPrincipal = (AutoDisposableWindowsPrincipal) principal;

            assertEquals("Test user should be impersonated", MockWindowsAccount.TEST_USER_NAME,
                    filterChain.getUserName());
            assertFalse("Impersonation context should have been reverted",
                    Advapi32Util.getUserName().equals(MockWindowsAccount.TEST_USER_NAME));
        } finally {
            if (windowsPrincipal != null) {
                windowsPrincipal.getIdentity().dispose();
            }
        }
    }

    @Test
    public void testImpersonateDisabled() throws IOException, ServletException {

        assertFalse("Current user shouldn't be the test user prior to the test",
                Advapi32Util.getUserName().equals(MockWindowsAccount.TEST_USER_NAME));
        SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        String userHeaderValue = MockWindowsAccount.TEST_USER_NAME + ":" + MockWindowsAccount.TEST_PASSWORD;
        String basicAuthHeader = "Basic " + BaseEncoding.base64().encode(userHeaderValue.getBytes());
        request.addHeader("Authorization", basicAuthHeader);
        SimpleHttpResponse response = new SimpleHttpResponse();
        RecordUserNameFilterChain filterChain = new RecordUserNameFilterChain();

        WindowsPrincipal windowsPrincipal = null;
        try {
            this.filter.setImpersonate(false);
            this.filter.doFilter(request, response, filterChain);

            Subject subject = (Subject) request.getSession().getAttribute("javax.security.auth.subject");
            boolean authenticated = (subject != null && subject.getPrincipals().size() > 0);
            assertTrue("Test user should be authenticated", authenticated);

            if (subject == null) {
                return;
            }
            Principal principal = subject.getPrincipals().iterator().next();
            assertTrue(principal instanceof WindowsPrincipal);
            windowsPrincipal = (WindowsPrincipal) principal;

            assertFalse("Test user should not be impersonated",
                    MockWindowsAccount.TEST_USER_NAME.equals(filterChain.getUserName()));
            assertFalse("Impersonation context should have been reverted",
                    Advapi32Util.getUserName().equals(MockWindowsAccount.TEST_USER_NAME));
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
        public void doFilter(ServletRequest sreq, ServletResponse srep) throws IOException, ServletException {
            this.userName = Advapi32Util.getUserName();
        }

        public String getUserName() {
            return this.userName;
        }
    }

}
