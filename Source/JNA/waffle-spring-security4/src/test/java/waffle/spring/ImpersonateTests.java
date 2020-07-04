/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.LMAccess;
import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.Netapi32;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;

import javax.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import waffle.mock.MockWindowsAccount;
import waffle.mock.http.RecordUserNameFilterChain;
import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;
import waffle.servlet.AutoDisposableWindowsPrincipal;
import waffle.servlet.WindowsPrincipal;
import waffle.servlet.spi.SecurityFilterProviderCollection;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * The Class ImpersonateTests.
 */
public class ImpersonateTests {

    /** The filter. */
    private waffle.spring.NegotiateSecurityFilter filter;

    /** The user info. */
    private LMAccess.USER_INFO_1 userInfo;

    /** The result of net add user. */
    private int resultOfNetAddUser;

    /**
     * Sets the up.
     */
    @BeforeEach
    public void setUp() {
        this.filter = new NegotiateSecurityFilter();
        this.filter.setProvider(new SecurityFilterProviderCollection(new WindowsAuthProviderImpl()));

        this.userInfo = new LMAccess.USER_INFO_1();
        this.userInfo.usri1_name = new WString(MockWindowsAccount.TEST_USER_NAME).toString();
        this.userInfo.usri1_password = new WString(MockWindowsAccount.TEST_PASSWORD).toString();
        this.userInfo.usri1_priv = LMAccess.USER_PRIV_USER;

        this.resultOfNetAddUser = Netapi32.INSTANCE.NetUserAdd(null, 1, this.userInfo, null);
        Assumptions.assumeTrue(this.resultOfNetAddUser == LMErr.NERR_Success,
                "Unable to add user (need to be administrator to do this).");
    }

    /**
     * Tear down.
     */
    @AfterEach
    public void tearDown() {
        this.filter.destroy();

        if (LMErr.NERR_Success == this.resultOfNetAddUser) {
            Assertions.assertEquals(LMErr.NERR_Success,
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

        Assertions.assertNotEquals("Current user shouldn't be the test user prior to the test",
                Advapi32Util.getUserName(), MockWindowsAccount.TEST_USER_NAME);

        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final String userHeaderValue = MockWindowsAccount.TEST_USER_NAME + ":" + MockWindowsAccount.TEST_PASSWORD;
        final String basicAuthHeader = "Basic "
                + Base64.getEncoder().encodeToString(userHeaderValue.getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", basicAuthHeader);

        final SimpleHttpResponse response = new SimpleHttpResponse();
        final RecordUserNameFilterChain filterChain = new RecordUserNameFilterChain();

        this.filter.setImpersonate(true);
        this.filter.doFilter(request, response, filterChain);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertTrue(authentication.isAuthenticated(), "Test user should be authenticated");

        final Principal principal = (Principal) authentication.getPrincipal();
        assertThat(principal).isInstanceOf(AutoDisposableWindowsPrincipal.class);
        final AutoDisposableWindowsPrincipal windowsPrincipal = (AutoDisposableWindowsPrincipal) principal;
        try {
            Assertions.assertEquals(MockWindowsAccount.TEST_USER_NAME, filterChain.getUserName(),
                    "Test user should be impersonated");
            Assertions.assertNotEquals(MockWindowsAccount.TEST_USER_NAME, Advapi32Util.getUserName(),
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

        Assertions.assertNotEquals("Current user shouldn't be the test user prior to the test",
                Advapi32Util.getUserName(), MockWindowsAccount.TEST_USER_NAME);
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final String userHeaderValue = MockWindowsAccount.TEST_USER_NAME + ":" + MockWindowsAccount.TEST_PASSWORD;
        final String basicAuthHeader = "Basic "
                + Base64.getEncoder().encodeToString(userHeaderValue.getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", basicAuthHeader);
        final SimpleHttpResponse response = new SimpleHttpResponse();
        final RecordUserNameFilterChain filterChain = new RecordUserNameFilterChain();

        this.filter.setImpersonate(false);
        this.filter.doFilter(request, response, filterChain);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertTrue(authentication.isAuthenticated(), "Test user should be authenticated");

        final Principal principal = (Principal) authentication.getPrincipal();
        assertThat(principal).isInstanceOf(WindowsPrincipal.class);
        final WindowsPrincipal windowsPrincipal = (WindowsPrincipal) principal;
        try {
            Assertions.assertNotEquals(MockWindowsAccount.TEST_USER_NAME, filterChain.getUserName(),
                    "Test user should not be impersonated");
            Assertions.assertNotEquals(MockWindowsAccount.TEST_USER_NAME, Advapi32Util.getUserName(),
                    "Impersonation context should have been reverted");
        } finally {
            windowsPrincipal.getIdentity().dispose();
        }
    }
}
