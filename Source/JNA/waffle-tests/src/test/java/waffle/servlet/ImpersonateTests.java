/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.BaseEncoding;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.LMAccess;
import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.Netapi32;

import waffle.mock.MockWindowsAccount;
import waffle.mock.http.RecordUserNameFilterChain;
import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * The Class ImpersonateTests.
 */
public class ImpersonateTests {

    /** The filter. */
    private NegotiateSecurityFilter filter;

    /** The user info. */
    private LMAccess.USER_INFO_1    userInfo;

    /** The result of net add user. */
    private int                     resultOfNetAddUser;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        this.filter = new NegotiateSecurityFilter();
        this.filter.setAuth(new WindowsAuthProviderImpl());
        try {
            this.filter.init(null);
        } catch (final ServletException e) {
            Assert.fail(e.getMessage());
        }

        this.userInfo = new LMAccess.USER_INFO_1();
        this.userInfo.usri1_name = new WString(MockWindowsAccount.TEST_USER_NAME);
        this.userInfo.usri1_password = new WString(MockWindowsAccount.TEST_PASSWORD);
        this.userInfo.usri1_priv = LMAccess.USER_PRIV_USER;

        this.resultOfNetAddUser = Netapi32.INSTANCE.NetUserAdd(null, 1, this.userInfo, null);
        // ignore test if not able to add user (need to be administrator to do this).
        Assume.assumeTrue(LMErr.NERR_Success == this.resultOfNetAddUser);
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {
        this.filter.destroy();

        if (LMErr.NERR_Success == this.resultOfNetAddUser) {
            Assert.assertEquals(LMErr.NERR_Success,
                    Netapi32.INSTANCE.NetUserDel(null, this.userInfo.usri1_name.toString()));
        }
    }

    /**
     * Test impersonate enabled.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    public void testImpersonateEnabled() throws IOException, ServletException {

        Assert.assertFalse("Current user shouldn't be the test user prior to the test", Advapi32Util.getUserName()
                .equals(MockWindowsAccount.TEST_USER_NAME));

        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final String userHeaderValue = MockWindowsAccount.TEST_USER_NAME + ":" + MockWindowsAccount.TEST_PASSWORD;
        final String basicAuthHeader = "Basic "
                + BaseEncoding.base64().encode(userHeaderValue.getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", basicAuthHeader);

        final SimpleHttpResponse response = new SimpleHttpResponse();
        final RecordUserNameFilterChain filterChain = new RecordUserNameFilterChain();

        AutoDisposableWindowsPrincipal windowsPrincipal = null;
        try {
            this.filter.setImpersonate(true);
            this.filter.doFilter(request, response, filterChain);

            final Subject subject = (Subject) request.getSession().getAttribute("javax.security.auth.subject");
            final boolean authenticated = subject != null && subject.getPrincipals().size() > 0;
            Assert.assertTrue("Test user should be authenticated", authenticated);

            final Principal principal = subject.getPrincipals().iterator().next();
            Assert.assertTrue(principal instanceof AutoDisposableWindowsPrincipal);
            windowsPrincipal = (AutoDisposableWindowsPrincipal) principal;

            Assert.assertEquals("Test user should be impersonated", MockWindowsAccount.TEST_USER_NAME,
                    filterChain.getUserName());
            Assert.assertFalse("Impersonation context should have been reverted",
                    Advapi32Util.getUserName().equals(MockWindowsAccount.TEST_USER_NAME));
        } finally {
            if (windowsPrincipal != null) {
                windowsPrincipal.getIdentity().dispose();
            }
        }
    }

    /**
     * Test impersonate disabled.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    public void testImpersonateDisabled() throws IOException, ServletException {

        Assert.assertFalse("Current user shouldn't be the test user prior to the test", Advapi32Util.getUserName()
                .equals(MockWindowsAccount.TEST_USER_NAME));
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final String userHeaderValue = MockWindowsAccount.TEST_USER_NAME + ":" + MockWindowsAccount.TEST_PASSWORD;
        final String basicAuthHeader = "Basic "
                + BaseEncoding.base64().encode(userHeaderValue.getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", basicAuthHeader);
        final SimpleHttpResponse response = new SimpleHttpResponse();
        final RecordUserNameFilterChain filterChain = new RecordUserNameFilterChain();

        WindowsPrincipal windowsPrincipal = null;
        try {
            this.filter.setImpersonate(false);
            this.filter.doFilter(request, response, filterChain);

            final Subject subject = (Subject) request.getSession().getAttribute("javax.security.auth.subject");
            final boolean authenticated = subject != null && subject.getPrincipals().size() > 0;
            Assert.assertTrue("Test user should be authenticated", authenticated);

            final Principal principal = subject.getPrincipals().iterator().next();
            Assert.assertTrue(principal instanceof WindowsPrincipal);
            windowsPrincipal = (WindowsPrincipal) principal;

            Assert.assertFalse("Test user should not be impersonated",
                    MockWindowsAccount.TEST_USER_NAME.equals(filterChain.getUserName()));
            Assert.assertFalse("Impersonation context should have been reverted",
                    Advapi32Util.getUserName().equals(MockWindowsAccount.TEST_USER_NAME));
        } finally {
            if (windowsPrincipal != null) {
                windowsPrincipal.getIdentity().dispose();
            }
        }
    }
}
