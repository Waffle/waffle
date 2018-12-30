/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.apache;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.SspiUtil.ManagedSecBufferDesc;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

import javax.servlet.ServletException;

import mockit.Expectations;
import mockit.Mocked;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.realm.GenericPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.apache.catalina.SimpleHttpRequest;
import waffle.apache.catalina.SimpleHttpResponse;
import waffle.mock.MockWindowsAuthProvider;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

/**
 * Waffle Tomcat Mixed Authenticator Tests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class MixedAuthenticatorTests {

    /** The authenticator. */
    MixedAuthenticator authenticator;

    /** The context. */
    @Mocked
    Context context;

    /** The engine. */
    @Mocked
    Engine engine;

    /**
     * Sets the up.
     *
     * @throws LifecycleException
     *             the lifecycle exception
     */
    @BeforeEach
    public void setUp() throws LifecycleException {
        this.authenticator = new MixedAuthenticator();
        this.authenticator.setContainer(this.context);
        Assertions.assertNotNull(new Expectations() {
            {
                MixedAuthenticatorTests.this.context.getName();
                this.result = "context";
                MixedAuthenticatorTests.this.context.getParent();
                this.result = MixedAuthenticatorTests.this.engine;
                MixedAuthenticatorTests.this.context.getParent();
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
    public void tearDown() throws LifecycleException {
        this.authenticator.stop();
    }

    /**
     * Test challenge get.
     */
    @Test
    public void testChallengeGET() {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setMethod("GET");
        request.setQueryString("j_negotiate_check");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        this.authenticator.authenticate(request, response, null);
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
            request.setQueryString("j_negotiate_check");
            request.setMethod("POST");
            request.setContentLength(0);
            final String clientToken = Base64.getEncoder().encodeToString(clientContext.getToken());
            request.addHeader("Authorization", securityPackage + " " + clientToken);
            final SimpleHttpResponse response = new SimpleHttpResponse();
            this.authenticator.authenticate(request, response, null);
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
     * Test get.
     */
    @Test
    public void testGet() {
        final LoginConfig loginConfig = new LoginConfig();
        loginConfig.setErrorPage("error.html");
        loginConfig.setLoginPage("login.html");
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final SimpleHttpResponse response = new SimpleHttpResponse();
        Assertions.assertFalse(this.authenticator.authenticate(request, response, loginConfig));
    }

    /**
     * Test get info.
     */
    @Test
    public void testGetInfo() {
        assertThat(this.authenticator.getInfo().length()).isGreaterThan(0);
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
            request.setQueryString("j_negotiate_check");
            String clientToken;
            while (true) {
                clientToken = Base64.getEncoder().encodeToString(clientContext.getToken());
                request.addHeader("Authorization", securityPackage + " " + clientToken);

                final SimpleHttpResponse response = new SimpleHttpResponse();
                authenticated = this.authenticator.authenticate(request, response, null);

                if (authenticated) {
                    assertThat(response.getHeaderNames().size()).isGreaterThanOrEqualTo(0);
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
     * Test post security check.
     */
    @Test
    public void testPostSecurityCheck() {
        final LoginConfig loginConfig = new LoginConfig();
        loginConfig.setErrorPage("error.html");
        loginConfig.setLoginPage("login.html");
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setQueryString("j_security_check");
        request.addParameter("j_username", "username");
        request.addParameter("j_password", "password");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        Assertions.assertFalse(this.authenticator.authenticate(request, response, loginConfig));
    }

    /**
     * Test programmatic security BOTH.
     *
     * @param identity
     *            the identity
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    public void testProgrammaticSecurityBoth(@Mocked final IWindowsIdentity identity) throws ServletException {
        this.authenticator.setAuth(new MockWindowsAuthProvider());
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContext((Context) this.authenticator.getContainer());

        request.login(WindowsAccountImpl.getCurrentUsername(), "");

        Assertions.assertNotNull(new Expectations() {
            {
                identity.getFqn();
                this.result = "fqn";
                identity.getSidString();
                this.result = "S-1234";
            }
        });
        request.setUserPrincipal(new GenericWindowsPrincipal(identity, PrincipalFormat.BOTH, PrincipalFormat.BOTH));

        Assertions.assertTrue(request.getUserPrincipal() instanceof GenericWindowsPrincipal);
        final GenericWindowsPrincipal windowsPrincipal = (GenericWindowsPrincipal) request.getUserPrincipal();
        Assertions.assertTrue(windowsPrincipal.getSidString().startsWith("S-"));
    }

    /**
     * Test programmatic security SID.
     *
     * @param identity
     *            the identity
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    public void testProgrammaticSecuritySID(@Mocked final IWindowsIdentity identity) throws ServletException {
        this.authenticator.setAuth(new MockWindowsAuthProvider());
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContext((Context) this.authenticator.getContainer());

        request.login(WindowsAccountImpl.getCurrentUsername(), "");

        Assertions.assertNotNull(new Expectations() {
            {
                identity.getSidString();
                this.result = "S-1234";
            }
        });
        request.setUserPrincipal(new GenericWindowsPrincipal(identity, PrincipalFormat.SID, PrincipalFormat.SID));

        Assertions.assertTrue(request.getUserPrincipal() instanceof GenericWindowsPrincipal);
        final GenericWindowsPrincipal windowsPrincipal = (GenericWindowsPrincipal) request.getUserPrincipal();
        Assertions.assertTrue(windowsPrincipal.getSidString().startsWith("S-"));
    }

    /**
     * Test programmatic security NONE.
     *
     * @param identity
     *            the identity
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    public void testProgrammaticSecurityNone(@Mocked final IWindowsIdentity identity) throws ServletException {
        this.authenticator.setAuth(new MockWindowsAuthProvider());
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setContext((Context) this.authenticator.getContainer());

        request.login(WindowsAccountImpl.getCurrentUsername(), "");

        request.setUserPrincipal(new GenericWindowsPrincipal(identity, PrincipalFormat.NONE, PrincipalFormat.NONE));

        Assertions.assertTrue(request.getUserPrincipal() instanceof GenericWindowsPrincipal);
        final GenericWindowsPrincipal windowsPrincipal = (GenericWindowsPrincipal) request.getUserPrincipal();
        Assertions.assertNull(windowsPrincipal.getSidString());
    }

    /**
     * Test security check parameters.
     */
    @Test
    public void testSecurityCheckParameters() {
        this.authenticator.setAuth(new MockWindowsAuthProvider());
        final LoginConfig loginConfig = new LoginConfig();
        loginConfig.setErrorPage("error.html");
        loginConfig.setLoginPage("login.html");
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.addParameter("j_security_check", "");
        request.addParameter("j_username", WindowsAccountImpl.getCurrentUsername());
        request.addParameter("j_password", "");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        Assertions.assertTrue(this.authenticator.authenticate(request, response, loginConfig));
    }

    /**
     * Test security check query string.
     */
    @Test
    public void testSecurityCheckQueryString() {
        this.authenticator.setAuth(new MockWindowsAuthProvider());
        final LoginConfig loginConfig = new LoginConfig();
        loginConfig.setErrorPage("error.html");
        loginConfig.setLoginPage("login.html");
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.setQueryString("j_security_check");
        request.addParameter("j_username", WindowsAccountImpl.getCurrentUsername());
        request.addParameter("j_password", "");
        final SimpleHttpResponse response = new SimpleHttpResponse();
        Assertions.assertTrue(this.authenticator.authenticate(request, response, loginConfig));
    }

    @Test
    public void testCustomPrincipal() throws LifecycleException, IOException {
        final GenericPrincipal genericPrincipal = new GenericPrincipal("my-principal", "my-password",
                Collections.emptyList());
        final MixedAuthenticator customAuthenticator = new MixedAuthenticator() {
            @Override
            protected GenericPrincipal createPrincipal(final IWindowsIdentity windowsIdentity) {
                return genericPrincipal;
            }
        };
        try {
            customAuthenticator.setContainer(this.context);
            customAuthenticator.setAlwaysUseSession(true);
            customAuthenticator.start();

            customAuthenticator.setAuth(new MockWindowsAuthProvider());
            final SimpleHttpRequest request = new SimpleHttpRequest();
            request.addParameter("j_security_check", "");
            request.addParameter("j_username", WindowsAccountImpl.getCurrentUsername());
            request.addParameter("j_password", "");
            final SimpleHttpResponse response = new SimpleHttpResponse();
            Assertions.assertTrue(customAuthenticator.authenticate(request, response));

            Assertions.assertEquals(genericPrincipal, request.getUserPrincipal());
        } finally {
            customAuthenticator.stop();
        }

    }
}
