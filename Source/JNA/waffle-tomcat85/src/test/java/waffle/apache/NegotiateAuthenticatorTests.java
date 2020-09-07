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
package waffle.apache;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.SspiUtil.ManagedSecBufferDesc;

import java.util.Base64;

import mockit.Expectations;
import mockit.Mocked;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.apache.catalina.SimpleHttpRequest;
import waffle.apache.catalina.SimpleHttpResponse;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

/**
 * Waffle Tomcat Authenticator Tests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateAuthenticatorTests {

    /** The authenticator. */
    private NegotiateAuthenticator authenticator;

    @Mocked
    Context context;

    @Mocked
    Engine engine;

    /**
     * Sets the up.
     *
     * @throws LifecycleException
     *             the lifecycle exception
     */
    @BeforeEach
    void setUp() throws LifecycleException {
        this.authenticator = new NegotiateAuthenticator();
        this.authenticator.setContainer(this.context);
        Assertions.assertNotNull(new Expectations() {
            {
                NegotiateAuthenticatorTests.this.context.getParent();
                this.result = NegotiateAuthenticatorTests.this.engine;
                NegotiateAuthenticatorTests.this.context.getParent();
                this.result = null;
            }
        });
        this.authenticator.start();
    }

    /**
     * Tear down.
     *
     * @throws LifecycleException
     *             the lifecycle exception
     */
    @AfterEach
    void tearDown() throws LifecycleException {
        this.authenticator.stop();
    }

    /**
     * Test allow guest login.
     */
    @Test
    void testAllowGuestLogin() {
        Assertions.assertTrue(this.authenticator.isAllowGuestLogin());
        this.authenticator.setAllowGuestLogin(false);
        Assertions.assertFalse(this.authenticator.isAllowGuestLogin());
    }

    /**
     * Test challenge get.
     */
    @Test
    void testChallengeGET() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.authenticator.authenticate(request, response);
        final String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
        Assertions.assertNotNull(wwwAuthenticates);
        Assertions.assertEquals(2, wwwAuthenticates.length);
        Assertions.assertEquals("Negotiate", wwwAuthenticates[0]);
        Assertions.assertEquals("NTLM", wwwAuthenticates[1]);
        Assertions.assertEquals("close", response.getHeader("Connection"));
        Assertions.assertEquals(2, response.getHeaderNames().size());
        Assertions.assertEquals(401, response.getStatus());
    }

    /**
     * Test challenge post.
     */
    @Test
    void testChallengePOST() {
        final String securityPackage = "Negotiate";
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
            this.authenticator.authenticate(request, response);
            Assertions.assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
            Assertions.assertEquals("keep-alive", response.getHeader("Connection"));
            Assertions.assertEquals(2, response.getHeaderNames().size());
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
     * Test get info.
     */
    @Test
    void testGetInfo() {
        assertThat(this.authenticator.getInfo().length()).isGreaterThan(0);
        Assertions.assertTrue(this.authenticator.getAuth() instanceof WindowsAuthProviderImpl);
    }

    /**
     * Test negotiate.
     */
    @Test
    void testNegotiate() {
        final String securityPackage = "Negotiate";
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
            // negotiate
            boolean authenticated = false;
            final SimpleHttpRequest request = new SimpleHttpRequest();
            while (true) {
                final String clientToken = Base64.getEncoder().encodeToString(clientContext.getToken());
                request.addHeader("Authorization", securityPackage + " " + clientToken);

                final SimpleHttpResponse response = new SimpleHttpResponse();
                authenticated = this.authenticator.authenticate(request, response);

                if (authenticated) {
                    Assertions.assertNotNull(request.getUserPrincipal());
                    Assertions.assertTrue(request.getUserPrincipal() instanceof GenericWindowsPrincipal);
                    final GenericWindowsPrincipal windowsPrincipal = (GenericWindowsPrincipal) request
                            .getUserPrincipal();
                    Assertions.assertTrue(windowsPrincipal.getSidString().startsWith("S-"));
                    assertThat(windowsPrincipal.getSid().length).isGreaterThan(0);
                    Assertions.assertTrue(windowsPrincipal.getGroups().containsKey("Everyone"));
                    assertThat(response.getHeaderNames().size()).isLessThanOrEqualTo(1);
                    break;
                }

                Assertions.assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
                Assertions.assertEquals("keep-alive", response.getHeader("Connection"));
                Assertions.assertEquals(2, response.getHeaderNames().size());
                Assertions.assertEquals(401, response.getStatus());
                final String continueToken = response.getHeader("WWW-Authenticate")
                        .substring(securityPackage.length() + 1);
                final byte[] continueTokenBytes = Base64.getDecoder().decode(continueToken);
                assertThat(continueTokenBytes.length).isGreaterThan(0);
                final ManagedSecBufferDesc continueTokenBuffer = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN,
                        continueTokenBytes);
                clientContext.initialize(clientContext.getHandle(), continueTokenBuffer,
                        WindowsAccountImpl.getCurrentUsername());
            }
            Assertions.assertTrue(authenticated);
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
     * Test post empty.
     */
    @Test
    void testPOSTEmpty() {
        final String securityPackage = "Negotiate";
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
            // negotiate
            boolean authenticated = false;
            final SimpleHttpRequest request = new SimpleHttpRequest();
            request.setMethod("POST");
            request.setContentLength(0);
            String clientToken;
            String continueToken;
            byte[] continueTokenBytes;
            SimpleHttpResponse response;
            ManagedSecBufferDesc continueTokenBuffer;
            while (true) {
                clientToken = Base64.getEncoder().encodeToString(clientContext.getToken());
                request.addHeader("Authorization", securityPackage + " " + clientToken);

                response = new SimpleHttpResponse();
                authenticated = this.authenticator.authenticate(request, response);

                if (authenticated) {
                    assertThat(response.getHeaderNames().size()).isGreaterThanOrEqualTo(0);
                    break;
                }

                if (response.getHeader("WWW-Authenticate").startsWith(securityPackage + ",")) {
                    Assertions.assertEquals("close", response.getHeader("Connection"));
                    Assertions.assertEquals(2, response.getHeaderNames().size());
                    Assertions.assertEquals(401, response.getStatus());
                    return;
                }

                Assertions.assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
                Assertions.assertEquals("keep-alive", response.getHeader("Connection"));
                Assertions.assertEquals(2, response.getHeaderNames().size());
                Assertions.assertEquals(401, response.getStatus());
                continueToken = response.getHeader("WWW-Authenticate").substring(securityPackage.length() + 1);
                continueTokenBytes = Base64.getDecoder().decode(continueToken);
                assertThat(continueTokenBytes.length).isGreaterThan(0);
                continueTokenBuffer = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, continueTokenBytes);
                clientContext.initialize(clientContext.getHandle(), continueTokenBuffer,
                        WindowsAccountImpl.getCurrentUsername());
            }
            Assertions.assertTrue(authenticated);
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
     * Test principal format.
     */
    @Test
    void testPrincipalFormat() {
        Assertions.assertEquals(PrincipalFormat.FQN, this.authenticator.getPrincipalFormat());
        this.authenticator.setPrincipalFormat("both");
        Assertions.assertEquals(PrincipalFormat.BOTH, this.authenticator.getPrincipalFormat());
    }

    /**
     * Test role format.
     */
    @Test
    void testRoleFormat() {
        Assertions.assertEquals(PrincipalFormat.FQN, this.authenticator.getRoleFormat());
        this.authenticator.setRoleFormat("both");
        Assertions.assertEquals(PrincipalFormat.BOTH, this.authenticator.getRoleFormat());
    }
}
