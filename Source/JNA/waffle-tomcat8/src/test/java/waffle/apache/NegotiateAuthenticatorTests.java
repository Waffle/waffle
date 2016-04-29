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
package waffle.apache;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleException;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

import mockit.Expectations;
import mockit.Mocked;
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

    /** The Constant LOGGER. */
    private static final Logger    LOGGER = LoggerFactory.getLogger(NegotiateAuthenticatorTests.class);

    /** The authenticator. */
    private NegotiateAuthenticator authenticator;

    @Mocked
    Context                        context;

    @Mocked
    Engine                         engine;

    /**
     * Sets the up.
     *
     * @throws LifecycleException
     *             the lifecycle exception
     */
    @Before
    public void setUp() throws LifecycleException {
        this.authenticator = new NegotiateAuthenticator();
        this.authenticator.setContainer(this.context);
        Assert.assertNotNull(new Expectations() {
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
    @After
    public void tearDown() throws LifecycleException {
        this.authenticator.stop();
    }

    /**
     * Test allow guest login.
     */
    @Test
    public void testAllowGuestLogin() {
        Assert.assertTrue(this.authenticator.isAllowGuestLogin());
        this.authenticator.setAllowGuestLogin(false);
        Assert.assertFalse(this.authenticator.isAllowGuestLogin());
    }

    /**
     * Test challenge get.
     */
    @Test
    public void testChallengeGET() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.authenticator.authenticate(request, response);
        final String[] wwwAuthenticates = response.getHeaderValues("WWW-Authenticate");
        Assert.assertNotNull(wwwAuthenticates);
        Assert.assertEquals(2, wwwAuthenticates.length);
        Assert.assertEquals("Negotiate", wwwAuthenticates[0]);
        Assert.assertEquals("NTLM", wwwAuthenticates[1]);
        Assert.assertEquals("close", response.getHeader("Connection"));
        Assert.assertEquals(2, response.getHeaderNames().size());
        Assert.assertEquals(401, response.getStatus());
    }

    /**
     * Test challenge post.
     */
    @Test
    public void testChallengePOST() {
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
            final String clientToken = BaseEncoding.base64().encode(clientContext.getToken());
            request.addHeader("Authorization", securityPackage + " " + clientToken);
            final SimpleHttpResponse response = new SimpleHttpResponse();
            this.authenticator.authenticate(request, response);
            Assert.assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
            Assert.assertEquals("keep-alive", response.getHeader("Connection"));
            Assert.assertEquals(2, response.getHeaderNames().size());
            Assert.assertEquals(401, response.getStatus());
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
    public void testGetInfo() {
        Assertions.assertThat(this.authenticator.getInfo().length()).isGreaterThan(0);
        Assert.assertTrue(this.authenticator.getAuth() instanceof WindowsAuthProviderImpl);
    }

    /**
     * Test negotiate.
     */
    @Test
    public void testNegotiate() {
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
                final String clientToken = BaseEncoding.base64().encode(clientContext.getToken());
                request.addHeader("Authorization", securityPackage + " " + clientToken);

                final SimpleHttpResponse response = new SimpleHttpResponse();
                authenticated = this.authenticator.authenticate(request, response);

                if (authenticated) {
                    Assert.assertNotNull(request.getUserPrincipal());
                    Assert.assertTrue(request.getUserPrincipal() instanceof GenericWindowsPrincipal);
                    final GenericWindowsPrincipal windowsPrincipal = (GenericWindowsPrincipal) request
                            .getUserPrincipal();
                    Assert.assertTrue(windowsPrincipal.getSidString().startsWith("S-"));
                    Assertions.assertThat(windowsPrincipal.getSid().length).isGreaterThan(0);
                    Assert.assertTrue(windowsPrincipal.getGroups().containsKey("Everyone"));
                    Assertions.assertThat(response.getHeaderNames().size()).isLessThanOrEqualTo(1);
                    break;
                }

                Assert.assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
                Assert.assertEquals("keep-alive", response.getHeader("Connection"));
                Assert.assertEquals(2, response.getHeaderNames().size());
                Assert.assertEquals(401, response.getStatus());
                final String continueToken = response.getHeader("WWW-Authenticate").substring(
                        securityPackage.length() + 1);
                final byte[] continueTokenBytes = BaseEncoding.base64().decode(continueToken);
                Assertions.assertThat(continueTokenBytes.length).isGreaterThan(0);
                final SecBufferDesc continueTokenBuffer = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, continueTokenBytes);
                clientContext.initialize(clientContext.getHandle(), continueTokenBuffer,
                        WindowsAccountImpl.getCurrentUsername());
            }
            Assert.assertTrue(authenticated);
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
    public void testPOSTEmpty() {
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
            SecBufferDesc continueTokenBuffer;
            while (true) {
                clientToken = BaseEncoding.base64().encode(clientContext.getToken());
                request.addHeader("Authorization", securityPackage + " " + clientToken);

                response = new SimpleHttpResponse();
                try {
                    authenticated = this.authenticator.authenticate(request, response);
                } catch (final Exception e) {
                    NegotiateAuthenticatorTests.LOGGER.error("", e);
                    return;
                }

                if (authenticated) {
                    Assertions.assertThat(response.getHeaderNames().size()).isGreaterThanOrEqualTo(0);
                    break;
                }

                if (response.getHeader("WWW-Authenticate").startsWith(securityPackage + ",")) {
                    Assert.assertEquals("close", response.getHeader("Connection"));
                    Assert.assertEquals(2, response.getHeaderNames().size());
                    Assert.assertEquals(401, response.getStatus());
                    return;
                }

                Assert.assertTrue(response.getHeader("WWW-Authenticate").startsWith(securityPackage + " "));
                Assert.assertEquals("keep-alive", response.getHeader("Connection"));
                Assert.assertEquals(2, response.getHeaderNames().size());
                Assert.assertEquals(401, response.getStatus());
                continueToken = response.getHeader("WWW-Authenticate").substring(securityPackage.length() + 1);
                continueTokenBytes = BaseEncoding.base64().decode(continueToken);
                Assertions.assertThat(continueTokenBytes.length).isGreaterThan(0);
                continueTokenBuffer = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, continueTokenBytes);
                clientContext.initialize(clientContext.getHandle(), continueTokenBuffer,
                        WindowsAccountImpl.getCurrentUsername());
            }
            Assert.assertTrue(authenticated);
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
    public void testPrincipalFormat() {
        Assert.assertEquals(PrincipalFormat.FQN, this.authenticator.getPrincipalFormat());
        this.authenticator.setPrincipalFormat("both");
        Assert.assertEquals(PrincipalFormat.BOTH, this.authenticator.getPrincipalFormat());
    }

    /**
     * Test role format.
     */
    @Test
    public void testRoleFormat() {
        Assert.assertEquals(PrincipalFormat.FQN, this.authenticator.getRoleFormat());
        this.authenticator.setRoleFormat("both");
        Assert.assertEquals(PrincipalFormat.BOTH, this.authenticator.getRoleFormat());
    }
}
