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
package waffle.windows.auth;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.LMAccess;
import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.LMJoin;
import com.sun.jna.platform.win32.Netapi32;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

/**
 * The Class WindowsAuthProviderTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthProviderTests {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsAuthProviderTests.class);

    /**
     * Test logon guest user.
     */
    // TODO This was commented out, uncommented and ignore until I can determine if this is valid
    @Ignore
    @Test
    public void testLogonGuestUser() {
        final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
        final IWindowsIdentity identity = prov.logonUser("garbage", "garbage");
        WindowsAuthProviderTests.LOGGER.debug("Fqn: {}", identity.getFqn());
        WindowsAuthProviderTests.LOGGER.debug("Guest: {}", Boolean.valueOf(identity.isGuest()));
        Assert.assertTrue(identity.getFqn().endsWith("\\Guest"));
        Assert.assertTrue(identity.isGuest());
        identity.dispose();
    }

    /**
     * Test logon user.
     */
    @Test
    public void testLogonUser() {
        final LMAccess.USER_INFO_1 userInfo = new LMAccess.USER_INFO_1();
        userInfo.usri1_name = new WString("WaffleTestUser");
        userInfo.usri1_password = new WString("!WAFFLEP$$Wrd0");
        userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
        // ignore test if not able to add user (need to be administrator to do this).
        Assume.assumeTrue(LMErr.NERR_Success == Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null));
        try {
            final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
            final IWindowsIdentity identity = prov.logonUser(userInfo.usri1_name.toString(),
                    userInfo.usri1_password.toString());
            Assert.assertTrue(identity.getFqn().endsWith("\\" + userInfo.usri1_name.toString()));
            Assert.assertFalse(identity.isGuest());
            identity.dispose();
        } finally {
            Assert.assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(null, userInfo.usri1_name.toString()));
        }
    }

    /**
     * Test impersonate logged on user.
     */
    @Test
    public void testImpersonateLoggedOnUser() {
        final LMAccess.USER_INFO_1 userInfo = new LMAccess.USER_INFO_1();
        userInfo.usri1_name = new WString(MockWindowsAccount.TEST_USER_NAME);
        userInfo.usri1_password = new WString(MockWindowsAccount.TEST_PASSWORD);
        userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
        // ignore test if not able to add user (need to be administrator to do this).
        Assume.assumeTrue(LMErr.NERR_Success == Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null));
        try {
            final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
            final IWindowsIdentity identity = prov.logonUser(userInfo.usri1_name.toString(),
                    userInfo.usri1_password.toString());
            final IWindowsImpersonationContext ctx = identity.impersonate();
            Assert.assertTrue(userInfo.usri1_name.toString().equals(Advapi32Util.getUserName()));
            ctx.revertToSelf();
            Assert.assertFalse(userInfo.usri1_name.toString().equals(Advapi32Util.getUserName()));
            identity.dispose();
        } finally {
            Assert.assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(null, userInfo.usri1_name.toString()));
        }
    }

    /**
     * Test get current computer.
     */
    @Test
    public void testGetCurrentComputer() {
        final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
        final IWindowsComputer computer = prov.getCurrentComputer();
        WindowsAuthProviderTests.LOGGER.debug(computer.getComputerName());
        Assertions.assertThat(computer.getComputerName().length()).isGreaterThan(0);
        WindowsAuthProviderTests.LOGGER.debug(computer.getJoinStatus());
        WindowsAuthProviderTests.LOGGER.debug(computer.getMemberOf());
        final String[] localGroups = computer.getGroups();
        Assert.assertNotNull(localGroups);
        Assertions.assertThat(localGroups.length).isGreaterThan(0);
        for (final String localGroup : localGroups) {
            WindowsAuthProviderTests.LOGGER.debug(" {}", localGroup);
        }
    }

    /**
     * Test get domains.
     */
    @Test
    public void testGetDomains() {
        if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            return;
        }

        final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
        final IWindowsDomain[] domains = prov.getDomains();
        Assert.assertNotNull(domains);
        for (final IWindowsDomain domain : domains) {
            WindowsAuthProviderTests.LOGGER.debug("{}: {}", domain.getFqn(), domain.getTrustDirectionString());
        }
    }

    /**
     * Test accept security token.
     */
    @Test
    public void testAcceptSecurityToken() {
        final String securityPackage = "Negotiate";
        final String targetName = "localhost";
        IWindowsCredentialsHandle clientCredentials = null;
        WindowsSecurityContextImpl clientContext = null;
        IWindowsSecurityContext serverContext = null;
        try {
            // client credentials handle
            clientCredentials = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
            clientCredentials.initialize();
            // initial client security context
            clientContext = new WindowsSecurityContextImpl();
            clientContext.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
            clientContext.setCredentialsHandle(clientCredentials);
            clientContext.setSecurityPackage(securityPackage);
            clientContext.initialize(null, null, targetName);
            // accept on the server
            final WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
            final String connectionId = "testConnection-" + Thread.currentThread().getId();
            do {
                // accept the token on the server
                try {
                    serverContext = provider.acceptSecurityToken(connectionId, clientContext.getToken(),
                            securityPackage);
                } catch (final Exception e) {
                    WindowsAuthProviderTests.LOGGER.error("", e);
                    break;
                }

                if (serverContext != null && serverContext.isContinue()) {
                    // initialize on the client
                    final SecBufferDesc continueToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN,
                            serverContext.getToken());
                    clientContext.initialize(clientContext.getHandle(), continueToken, targetName);
                    WindowsAuthProviderTests.LOGGER.debug("Token: {}",
                            BaseEncoding.base64().encode(serverContext.getToken()));
                }

            } while (clientContext.isContinue() || serverContext != null && serverContext.isContinue());

            if (serverContext != null) {
                Assertions.assertThat(serverContext.getIdentity().getFqn().length()).isGreaterThan(0);

                WindowsAuthProviderTests.LOGGER.debug(serverContext.getIdentity().getFqn());
                for (final IWindowsAccount group : serverContext.getIdentity().getGroups()) {
                    WindowsAuthProviderTests.LOGGER.debug(" {}", group.getFqn());
                }
            }
        } finally {
            if (serverContext != null) {
                serverContext.dispose();
            }
            if (clientContext != null) {
                clientContext.dispose();
            }
            if (clientCredentials != null) {
                clientCredentials.dispose();
            }
        }
    }

    /**
     * Test security contexts expire.
     *
     * @throws InterruptedException
     *             the interrupted exception
     */
    @Test
    public void testSecurityContextsExpire() throws InterruptedException {
        final String securityPackage = "Negotiate";
        IWindowsCredentialsHandle clientCredentials = null;
        WindowsSecurityContextImpl clientContext = null;
        IWindowsSecurityContext serverContext = null;
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
            // accept on the server
            final WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl(1);
            final int max = 100;
            for (int i = 0; i < max; i++) {
                Thread.sleep(25);
                final String connectionId = "testConnection_" + i;
                serverContext = provider.acceptSecurityToken(connectionId, clientContext.getToken(), securityPackage);
                Assertions.assertThat(provider.getContinueContextsSize()).isGreaterThan(0);
            }
            WindowsAuthProviderTests.LOGGER.debug("Cached security contexts: {}",
                    Integer.valueOf(provider.getContinueContextsSize()));
            Assert.assertFalse(max == provider.getContinueContextsSize());
        } finally {
            if (serverContext != null) {
                serverContext.dispose();
            }
            if (clientContext != null) {
                clientContext.dispose();
            }
            if (clientCredentials != null) {
                clientCredentials.dispose();
            }
        }
    }

    /**
     * Test accept and impersonate security token.
     */
    @Test
    public void testAcceptAndImpersonateSecurityToken() {
        final String securityPackage = "Negotiate";
        final String targetName = "localhost";
        IWindowsCredentialsHandle clientCredentials = null;
        WindowsSecurityContextImpl clientContext = null;
        IWindowsSecurityContext serverContext = null;
        try {
            // client credentials handle
            clientCredentials = WindowsCredentialsHandleImpl.getCurrent(securityPackage);
            clientCredentials.initialize();
            // initial client security context
            clientContext = new WindowsSecurityContextImpl();
            clientContext.setPrincipalName(WindowsAccountImpl.getCurrentUsername());
            clientContext.setCredentialsHandle(clientCredentials);
            clientContext.setSecurityPackage(securityPackage);
            clientContext.initialize(null, null, targetName);
            // accept on the server
            final WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
            final String connectionId = "testConnection";
            do {
                // accept the token on the server
                try {
                    serverContext = provider.acceptSecurityToken(connectionId, clientContext.getToken(),
                            securityPackage);
                } catch (final Exception e) {
                    WindowsAuthProviderTests.LOGGER.error("", e);
                    break;
                }

                if (serverContext != null && serverContext.isContinue()) {
                    // initialize on the client
                    final SecBufferDesc continueToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN,
                            serverContext.getToken());
                    clientContext.initialize(clientContext.getHandle(), continueToken, targetName);
                }

            } while (clientContext.isContinue() || serverContext != null && serverContext.isContinue());

            if (serverContext != null) {
                Assertions.assertThat(serverContext.getIdentity().getFqn().length()).isGreaterThan(0);

                final IWindowsImpersonationContext impersonationCtx = serverContext.impersonate();
                impersonationCtx.revertToSelf();

                WindowsAuthProviderTests.LOGGER.debug(serverContext.getIdentity().getFqn());
                for (final IWindowsAccount group : serverContext.getIdentity().getGroups()) {
                    WindowsAuthProviderTests.LOGGER.debug(" {}", group.getFqn());
                }
            }
        } finally {
            if (serverContext != null) {
                serverContext.dispose();
            }
            if (clientContext != null) {
                clientContext.dispose();
            }
            if (clientCredentials != null) {
                clientCredentials.dispose();
            }
        }
    }
}