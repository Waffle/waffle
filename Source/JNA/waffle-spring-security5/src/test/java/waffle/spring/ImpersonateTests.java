/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    void setUp() {
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
    void tearDown() {
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
    void testImpersonateEnabled() throws IOException, ServletException {

        Assertions.assertNotEquals("Current user shouldn't be the test user prior to the test",
                MockWindowsAccount.TEST_USER_NAME, Advapi32Util.getUserName());

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
    void testImpersonateDisabled() throws IOException, ServletException {

        Assertions.assertNotEquals("Current user shouldn't be the test user prior to the test",
                MockWindowsAccount.TEST_USER_NAME, Advapi32Util.getUserName());
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
