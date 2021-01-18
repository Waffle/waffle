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
package waffle.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.platform.win32.Secur32Util;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.SspiUtil.ManagedSecBufferDesc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import javax.security.auth.Subject;
import javax.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.mock.MockWindowsAuthProvider;
import waffle.mock.MockWindowsIdentity;
import waffle.mock.http.SimpleFilterChain;
import waffle.mock.http.SimpleFilterConfig;
import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

/**
 * Waffle Tomcat Security Filter Test.
 *
 * @author dblock[at]dblock[dot]org
 */
class NegotiateSecurityFilterTest {

    /** The Constant NEGOTIATE. */
    private static final String NEGOTIATE = "Negotiate";

    /** The Constant NTLM. */
    private static final String NTLM = "NTLM";

    /** The filter. */
    private NegotiateSecurityFilter filter;

    /**
     * Sets the up.
     *
     * @throws ServletException
     *             the servlet exception
     */
    @BeforeEach
    void setUp() throws ServletException {
        this.filter = new NegotiateSecurityFilter();
        this.filter.setAuth(new WindowsAuthProviderImpl());
        this.filter.init(null);
    }

    /**
     * Tear down.
     */
    @AfterEach
    void tearDown() {
        this.filter.destroy();
    }

    /**
     * Test challenge get.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testChallengeGET() throws IOException, ServletException {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, null);
        final String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
        Assertions.assertEquals(3, wwwAuthenticates.length);
        Assertions.assertEquals(NegotiateSecurityFilterTest.NEGOTIATE, wwwAuthenticates[0]);
        Assertions.assertEquals(NegotiateSecurityFilterTest.NTLM, wwwAuthenticates[1]);
        Assertions.assertTrue(wwwAuthenticates[2].startsWith("Basic realm=\""));
        Assertions.assertEquals(2, response.getHeaderNamesSize());
        Assertions.assertEquals("keep-alive", response.getHeader("Connection"));
        Assertions.assertEquals(401, response.getStatus());
    }

    /**
     * Test challenge post.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testChallengePOST() throws IOException, ServletException {
        final String securityPackage = NegotiateSecurityFilterTest.NEGOTIATE;
        IWindowsCredentialsHandle clientCredentials = null;
        WindowsSecurityContextImpl clientContext = null;
        try {
            // client credentials handle
            clientCredentials = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
            clientCredentials.initialize();
            // initial client security context
            clientContext = new WindowsSecurityContextImpl();
            clientContext.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
            clientContext.setCredentialsHandle(clientCredentials);
            clientContext.setSecurityPackage(securityPackage);
            clientContext.initialize(null, null, WindowsAccountImpl.getCurrentUsername());
            final SimpleHttpRequest request = new SimpleHttpRequest();
            request.setMethod("POST");
            request.setContentLength(0);
            final String clientToken = Base64.getEncoder().encodeToString(clientContext.getToken());
            request.addHeader("Authorization", securityPackage + " " + clientToken);
            final SimpleHttpResponse response = new SimpleHttpResponse();
            this.filter.doFilter(request, response, null);
            Assertions.assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
            Assertions.assertEquals("keep-alive", response.getHeader("Connection"));
            Assertions.assertEquals(2, response.getHeaderNamesSize());
            Assertions.assertEquals(401, response.getStatus());
        } finally {
            if (clientContext != null) {
                clientContext.dispose();
            }
            if (clientCredentials != null) {
                clientCredentials.dispose();
            }
        }
    }

    /**
     * Test negotiate.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testNegotiate() throws IOException, ServletException {
        final String securityPackage = NegotiateSecurityFilterTest.NEGOTIATE;
        // client credentials handle
        IWindowsCredentialsHandle clientCredentials = null;
        WindowsSecurityContextImpl clientContext = null;
        // role will contain both Everyone and SID
        this.filter.setRoleFormat("both");
        try {
            // client credentials handle
            clientCredentials = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
            clientCredentials.initialize();
            // initial client security context
            clientContext = new WindowsSecurityContextImpl();
            clientContext.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
            clientContext.setCredentialsHandle(clientCredentials);
            clientContext.setSecurityPackage(securityPackage);
            clientContext.initialize(null, null, WindowsAccountImpl.getCurrentUsername());
            // filter chain
            final SimpleFilterChain filterChain = new SimpleFilterChain();
            // negotiate
            boolean authenticated = false;
            final SimpleHttpRequest request = new SimpleHttpRequest();
            while (true) {
                final String clientToken = Base64.getEncoder().encodeToString(clientContext.getToken());
                request.addHeader("Authorization", securityPackage + " " + clientToken);

                final SimpleHttpResponse response = new SimpleHttpResponse();
                this.filter.doFilter(request, response, filterChain);

                final Subject subject = (Subject) request.getSession(false).getAttribute("javax.security.auth.subject");
                authenticated = subject != null && subject.getPrincipals().size() > 0;

                if (authenticated) {
                    assertThat(response.getHeaderNamesSize()).isNotNegative();
                    break;
                }

                Assertions.assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
                Assertions.assertEquals("keep-alive", response.getHeader("Connection"));
                Assertions.assertEquals(2, response.getHeaderNamesSize());
                Assertions.assertEquals(401, response.getStatus());
                final String continueToken = response.getHeader("WWW-Authenticate")
                        .substring(securityPackage.length() + 1);
                final byte[] continueTokenBytes = Base64.getDecoder().decode(continueToken);
                assertThat(continueTokenBytes).isNotEmpty();
                final ManagedSecBufferDesc continueTokenBuffer = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN,
                        continueTokenBytes);
                clientContext.initialize(clientContext.getHandle(), continueTokenBuffer, "localhost");
            }
            Assertions.assertTrue(authenticated);
            Assertions.assertTrue(filterChain.getRequest() instanceof NegotiateRequestWrapper);
            Assertions.assertTrue(filterChain.getResponse() instanceof SimpleHttpResponse);
            final NegotiateRequestWrapper wrappedRequest = (NegotiateRequestWrapper) filterChain.getRequest();
            Assertions.assertEquals(NegotiateSecurityFilterTest.NEGOTIATE.toUpperCase(), wrappedRequest.getAuthType());
            Assertions.assertEquals(Secur32Util.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible),
                    wrappedRequest.getRemoteUser());
            Assertions.assertTrue(wrappedRequest.getUserPrincipal() instanceof WindowsPrincipal);
            final String everyoneGroupName = Advapi32Util.getAccountBySid("S-1-1-0").name;
            Assertions.assertTrue(wrappedRequest.isUserInRole(everyoneGroupName));
            Assertions.assertTrue(wrappedRequest.isUserInRole("S-1-1-0"));
        } finally {
            if (clientContext != null) {
                clientContext.dispose();
            }
            if (clientCredentials != null) {
                clientCredentials.dispose();
            }
        }
    }

    /**
     * Test negotiate previous auth with windows principal.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testNegotiatePreviousAuthWithWindowsPrincipal() throws IOException, ServletException {
        final MockWindowsIdentity mockWindowsIdentity = new MockWindowsIdentity("user", new ArrayList<String>());
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final WindowsPrincipal windowsPrincipal = new WindowsPrincipal(mockWindowsIdentity);
        request.setUserPrincipal(windowsPrincipal);
        final SimpleFilterChain filterChain = new SimpleFilterChain();
        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);
        Assertions.assertTrue(filterChain.getRequest() instanceof NegotiateRequestWrapper);
        final NegotiateRequestWrapper wrappedRequest = (NegotiateRequestWrapper) filterChain.getRequest();
        Assertions.assertTrue(wrappedRequest.getUserPrincipal() instanceof WindowsPrincipal);
        Assertions.assertEquals(windowsPrincipal, wrappedRequest.getUserPrincipal());
    }

    /**
     * Test challenge ntlmpost.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testChallengeNTLMPOST() throws IOException, ServletException {
        final MockWindowsIdentity mockWindowsIdentity = new MockWindowsIdentity("user", new ArrayList<String>());
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final WindowsPrincipal windowsPrincipal = new WindowsPrincipal(mockWindowsIdentity);
        request.setUserPrincipal(windowsPrincipal);
        request.setMethod("POST");
        request.setContentLength(0);
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        final SimpleFilterChain filterChain = new SimpleFilterChain();
        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);
        Assertions.assertEquals(401, response.getStatus());
        final String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
        Assertions.assertEquals(1, wwwAuthenticates.length);
        Assertions.assertTrue(wwwAuthenticates[0].startsWith("NTLM "));
        Assertions.assertEquals(2, response.getHeaderNamesSize());
        Assertions.assertEquals("keep-alive", response.getHeader("Connection"));
        Assertions.assertEquals(401, response.getStatus());
    }

    /**
     * Test challenge ntlmput.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testChallengeNTLMPUT() throws IOException, ServletException {
        final MockWindowsIdentity mockWindowsIdentity = new MockWindowsIdentity("user", new ArrayList<String>());
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final WindowsPrincipal windowsPrincipal = new WindowsPrincipal(mockWindowsIdentity);
        request.setUserPrincipal(windowsPrincipal);
        request.setMethod("PUT");
        request.setContentLength(0);
        request.addHeader("Authorization", "NTLM TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==");
        final SimpleFilterChain filterChain = new SimpleFilterChain();
        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.filter.doFilter(request, response, filterChain);
        Assertions.assertEquals(401, response.getStatus());
        final String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
        Assertions.assertEquals(1, wwwAuthenticates.length);
        Assertions.assertTrue(wwwAuthenticates[0].startsWith("NTLM "));
        Assertions.assertEquals(2, response.getHeaderNamesSize());
        Assertions.assertEquals("keep-alive", response.getHeader("Connection"));
        Assertions.assertEquals(401, response.getStatus());
    }

    /**
     * Test init basic security filter provider.
     *
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testInitBasicSecurityFilterProvider() throws ServletException {
        final SimpleFilterConfig filterConfig = new SimpleFilterConfig();
        filterConfig.setParameter("principalFormat", "sid");
        filterConfig.setParameter("roleFormat", "none");
        filterConfig.setParameter("allowGuestLogin", "true");
        filterConfig.setParameter("securityFilterProviders", "waffle.servlet.spi.BasicSecurityFilterProvider");
        filterConfig.setParameter("waffle.servlet.spi.BasicSecurityFilterProvider/realm", "DemoRealm");
        filterConfig.setParameter("authProvider", MockWindowsAuthProvider.class.getName());
        this.filter.init(filterConfig);
        Assertions.assertEquals(this.filter.getPrincipalFormat(), PrincipalFormat.SID);
        Assertions.assertEquals(this.filter.getRoleFormat(), PrincipalFormat.NONE);
        Assertions.assertTrue(this.filter.isAllowGuestLogin());
        Assertions.assertEquals(1, this.filter.getProviders().size());
        Assertions.assertTrue(this.filter.getAuth() instanceof MockWindowsAuthProvider);
    }

    /**
     * Test init two security filter providers.
     *
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testInitTwoSecurityFilterProviders() throws ServletException {
        // make sure that providers can be specified separated by any kind of space
        final SimpleFilterConfig filterConfig = new SimpleFilterConfig();
        filterConfig.setParameter("securityFilterProviders", "waffle.servlet.spi.BasicSecurityFilterProvider\n"
                + "waffle.servlet.spi.NegotiateSecurityFilterProvider waffle.servlet.spi.BasicSecurityFilterProvider");
        this.filter.init(filterConfig);
        Assertions.assertEquals(3, this.filter.getProviders().size());
    }

    /**
     * Test init negotiate security filter provider.
     *
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testInitNegotiateSecurityFilterProvider() throws ServletException {
        final SimpleFilterConfig filterConfig = new SimpleFilterConfig();
        filterConfig.setParameter("securityFilterProviders", "waffle.servlet.spi.NegotiateSecurityFilterProvider");
        filterConfig.setParameter("waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols",
                "NTLM\nNegotiate NTLM");
        this.filter.init(filterConfig);
        Assertions.assertEquals(this.filter.getPrincipalFormat(), PrincipalFormat.FQN);
        Assertions.assertEquals(this.filter.getRoleFormat(), PrincipalFormat.FQN);
        Assertions.assertTrue(this.filter.isAllowGuestLogin());
        Assertions.assertEquals(1, this.filter.getProviders().size());
    }

    /**
     * Test init negotiate security filter provider invalid protocol.
     */
    @Test
    void testInitNegotiateSecurityFilterProviderInvalidProtocol() {
        final SimpleFilterConfig filterConfig = new SimpleFilterConfig();
        filterConfig.setParameter("securityFilterProviders", "waffle.servlet.spi.NegotiateSecurityFilterProvider");
        filterConfig.setParameter("waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols", "INVALID");
        try {
            this.filter.init(filterConfig);
            Assertions.fail("expected ServletException");
        } catch (final ServletException e) {
            Assertions.assertEquals("java.lang.RuntimeException: Unsupported protocol: INVALID", e.getMessage());
        }
    }

    /**
     * Test init invalid parameter.
     */
    @Test
    void testInitInvalidParameter() {
        try {
            final SimpleFilterConfig filterConfig = new SimpleFilterConfig();
            filterConfig.setParameter("invalidParameter", "random");
            this.filter.init(filterConfig);
            Assertions.fail("expected ServletException");
        } catch (final ServletException e) {
            Assertions.assertEquals("Invalid parameter: invalidParameter", e.getMessage());
        }
    }

    /**
     * Test init invalid class in parameter.
     */
    @Test
    void testInitInvalidClassInParameter() {
        try {
            final SimpleFilterConfig filterConfig = new SimpleFilterConfig();
            filterConfig.setParameter("invalidClass/invalidParameter", "random");
            this.filter.init(filterConfig);
            Assertions.fail("expected ServletException");
        } catch (final ServletException e) {
            Assertions.assertEquals("java.lang.ClassNotFoundException: invalidClass", e.getMessage());
        }
    }
}
