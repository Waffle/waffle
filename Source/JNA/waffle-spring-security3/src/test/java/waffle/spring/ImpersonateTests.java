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
package waffle.spring;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.LMAccess;
import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.Netapi32;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;

import waffle.mock.MockWindowsAccount;
import waffle.mock.http.RecordUserNameFilterChain;
import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;
import waffle.servlet.AutoDisposableWindowsPrincipal;
import waffle.servlet.WindowsPrincipal;
import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * The Class ImpersonateTests.
 */
public class ImpersonateTests {

    /** The filter. */
    private waffle.spring.NegotiateSecurityFilter filter;

    /** The user info. */
    private LMAccess.USER_INFO_1                  userInfo;

    /** The result of net add user. */
    private int                                   resultOfNetAddUser;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        this.filter = new NegotiateSecurityFilter();
        this.filter.setProvider(new SecurityFilterProviderCollection(new WindowsAuthProviderImpl()));

        this.userInfo = new LMAccess.USER_INFO_1();
        this.userInfo.usri1_name = new WString(MockWindowsAccount.TEST_USER_NAME);
        this.userInfo.usri1_password = new WString(MockWindowsAccount.TEST_PASSWORD);
        this.userInfo.usri1_priv = LMAccess.USER_PRIV_USER;

        this.resultOfNetAddUser = Netapi32.INSTANCE.NetUserAdd(null, 1, this.userInfo, null);
        Assume.assumeThat("Unable to add user (need to be administrator to do this).", this.resultOfNetAddUser,
                equalTo(LMErr.NERR_Success));
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

        assertThat(Advapi32Util.getUserName()).isNotEqualTo(MockWindowsAccount.TEST_USER_NAME).withFailMessage(
                "Current user shouldn't be the test user prior to the test");

        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final String userHeaderValue = MockWindowsAccount.TEST_USER_NAME + ":" + MockWindowsAccount.TEST_PASSWORD;
        final String basicAuthHeader = "Basic " + Base64.encode(userHeaderValue.getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", basicAuthHeader);

        final SimpleHttpResponse response = new SimpleHttpResponse();
        final RecordUserNameFilterChain filterChain = new RecordUserNameFilterChain();

        this.filter.setImpersonate(true);
        this.filter.doFilter(request, response, filterChain);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertTrue("Test user should be authenticated", authentication.isAuthenticated());

        final Principal principal = (Principal) authentication.getPrincipal();
        assertThat(principal).isInstanceOf(AutoDisposableWindowsPrincipal.class);
        AutoDisposableWindowsPrincipal windowsPrincipal = (AutoDisposableWindowsPrincipal) principal;
        try {
            assertThat(filterChain.getUserName()).isEqualTo(MockWindowsAccount.TEST_USER_NAME).withFailMessage(
                    "Test user should be impersonated");
            assertThat(Advapi32Util.getUserName()).isNotEqualTo(MockWindowsAccount.TEST_USER_NAME).withFailMessage(
                    "Impersonation context should have been reverted");
        } finally {
            windowsPrincipal.getIdentity().dispose();
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

        assertThat(Advapi32Util.getUserName()).isNotEqualTo(MockWindowsAccount.TEST_USER_NAME).withFailMessage(
                "Current user shouldn't be the test user prior to the test");
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final String userHeaderValue = MockWindowsAccount.TEST_USER_NAME + ":" + MockWindowsAccount.TEST_PASSWORD;
        final String basicAuthHeader = "Basic " + Base64.encode(userHeaderValue.getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", basicAuthHeader);
        final SimpleHttpResponse response = new SimpleHttpResponse();
        final RecordUserNameFilterChain filterChain = new RecordUserNameFilterChain();

        this.filter.setImpersonate(false);
        this.filter.doFilter(request, response, filterChain);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertTrue("Test user should be authenticated", authentication.isAuthenticated());

        final Principal principal = (Principal) authentication.getPrincipal();
        assertThat(principal).isInstanceOf(WindowsPrincipal.class);
        WindowsPrincipal windowsPrincipal = (WindowsPrincipal) principal;
        try {
            assertThat(filterChain.getUserName()).isNotEqualTo(MockWindowsAccount.TEST_USER_NAME).withFailMessage(
                    "Test user should not be impersonated");
            assertThat(Advapi32Util.getUserName()).isNotEqualTo(MockWindowsAccount.TEST_USER_NAME).withFailMessage(
                    "Impersonation context should have been reverted");
        } finally {
            windowsPrincipal.getIdentity().dispose();
        }
    }
}
