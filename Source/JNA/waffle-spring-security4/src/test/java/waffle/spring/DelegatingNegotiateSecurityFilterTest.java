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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import waffle.mock.http.SimpleFilterChain;
import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;
import waffle.spring.handlers.CustomAccessDeniedHandler;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * The Class NegotiateSecurityFilterTests.
 *
 * @author [unaor]
 */
public class DelegatingNegotiateSecurityFilterTest {

    /** The filter. */
    private DelegatingNegotiateSecurityFilter filter;

    /** The ctx. */
    private ApplicationContext ctx;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        final String[] configFiles = new String[] { "springTestFilterBeans.xml" };
        this.ctx = new ClassPathXmlApplicationContext(configFiles);
        SecurityContextHolder.getContext().setAuthentication(null);
        this.filter = (DelegatingNegotiateSecurityFilter) this.ctx.getBean("waffleDelegatingNegotiateSecurityFilter");
    }

    /**
     * Shut down.
     */
    @AfterEach
    void shutDown() {
        ((AbstractApplicationContext) this.ctx).close();
    }

    /**
     * Test filter. and custom handlers
     */
    @Test
    void testFilter() {
        Assertions.assertFalse(this.filter.isAllowGuestLogin());
        Assertions.assertEquals(PrincipalFormat.FQN, this.filter.getPrincipalFormat());
        Assertions.assertEquals(PrincipalFormat.BOTH, this.filter.getRoleFormat());
        Assertions.assertNull(this.filter.getFilterConfig());
        Assertions.assertNotNull(this.filter.getProvider());
        Assertions.assertTrue(this.filter.getAccessDeniedHandler() instanceof CustomAccessDeniedHandler);
    }

    /**
     * Test the delegating filter ,in case no custom authentication was passed, the filter would store the auth in the
     * security context.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testNegotiate() throws IOException, ServletException {
        final String securityPackage = "Negotiate";
        final SimpleFilterChain filterChain = new SimpleFilterChain();
        final SimpleHttpRequest request = new SimpleHttpRequest();

        final String clientToken = Base64.getEncoder()
                .encodeToString(WindowsAccountImpl.getCurrentUsername().getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", securityPackage + " " + clientToken);

        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertNotNull(auth);
        final Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Assertions.assertNotNull(authorities);
        Assertions.assertEquals(3, authorities.size());

        final List<String> list = new ArrayList<>();
        for (final GrantedAuthority grantedAuthority : authorities) {
            list.add(grantedAuthority.getAuthority());
        }
        Collections.sort(list);
        Assertions.assertEquals("ROLE_EVERYONE", list.get(0));
        Assertions.assertEquals("ROLE_USER", list.get(1));
        Assertions.assertEquals("ROLE_USERS", list.get(2));
        Assertions.assertEquals(0, response.getHeaderNamesSize());
    }

    /**
     * Tests that the same authentication token generated by the custom authentication manager is provided to both the
     * SecurityContext and the custom authentication success handler.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testNegotiate_CustomAuth() throws IOException, ServletException {
        final Authentication customToken = Mockito.mock(Authentication.class);
        Mockito.when(customToken.getName()).thenReturn("Custom Token");
        this.filter.setAuthenticationManager(authentication -> customToken);

        final CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
        this.filter.setAuthenticationSuccessHandler(successHandler);

        final String securityPackage = "Negotiate";
        final SimpleFilterChain filterChain = new SimpleFilterChain();
        final SimpleHttpRequest request = new SimpleHttpRequest();

        final String clientToken = Base64.getEncoder()
                .encodeToString(WindowsAccountImpl.getCurrentUsername().getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization", securityPackage + " " + clientToken);

        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertNotNull(auth);
        Assertions.assertEquals(customToken, auth);
        Assertions.assertEquals("Custom Token", auth.getName());

        Assertions.assertEquals(customToken, successHandler.getAuthentication());
        Assertions.assertEquals("Custom Token", successHandler.getAuthentication().getName());
    }

}

/**
 * Provides a basic implementation that simply stores the provided Authentication so it can be checked for testing.
 *
 * Class declared here rather than in the general handlers package because it should NOT be added to the overall filter
 * configuration, but only to the specific filter instance testing its use.
 */
class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private Authentication authentication;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Authentication authentication) throws IOException, ServletException {
        this.authentication = authentication;
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}
