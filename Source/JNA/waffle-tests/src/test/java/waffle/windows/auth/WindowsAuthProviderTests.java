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
package waffle.windows.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.LMAccess;
import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.LMJoin;
import com.sun.jna.platform.win32.Netapi32;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.SspiUtil.ManagedSecBufferDesc;

import java.util.Base64;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @Disabled
    @Test
    public void testLogonGuestUser() {
        final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
        final IWindowsIdentity identity = prov.logonUser("garbage", "garbage");
        WindowsAuthProviderTests.LOGGER.info("Fqn: {}", identity.getFqn());
        WindowsAuthProviderTests.LOGGER.info("Guest: {}", Boolean.valueOf(identity.isGuest()));
        Assertions.assertTrue(identity.getFqn().endsWith("\\Guest"));
        Assertions.assertTrue(identity.isGuest());
        identity.dispose();
    }

    /**
     * Test logon user.
     */
    @Test
    public void testLogonUser() {
        final LMAccess.USER_INFO_1 userInfo = new LMAccess.USER_INFO_1();
        userInfo.usri1_name = new WString("WaffleTestUser").toString();
        userInfo.usri1_password = new WString("!WAFFLEP$$Wrd0").toString();
        userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
        // ignore test if not able to add user (need to be administrator to do this).
        Assumptions.assumeTrue(LMErr.NERR_Success == Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null));
        try {
            final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
            final IWindowsIdentity identity = prov.logonUser(userInfo.usri1_name.toString(),
                    userInfo.usri1_password.toString());
            Assertions.assertTrue(identity.getFqn().endsWith("\\" + userInfo.usri1_name.toString()));
            Assertions.assertFalse(identity.isGuest());
            identity.dispose();
        } finally {
            Assertions.assertEquals(LMErr.NERR_Success,
                    Netapi32.INSTANCE.NetUserDel(null, userInfo.usri1_name.toString()));
        }
    }

    /**
     * Test impersonate logged on user.
     */
    @Test
    public void testImpersonateLoggedOnUser() {
        final LMAccess.USER_INFO_1 userInfo = new LMAccess.USER_INFO_1();
        userInfo.usri1_name = new WString(MockWindowsAccount.TEST_USER_NAME).toString();
        userInfo.usri1_password = new WString(MockWindowsAccount.TEST_PASSWORD).toString();
        userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
        // ignore test if not able to add user (need to be administrator to do this).
        Assumptions.assumeTrue(LMErr.NERR_Success == Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null));
        try {
            final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
            final IWindowsIdentity identity = prov.logonUser(userInfo.usri1_name.toString(),
                    userInfo.usri1_password.toString());
            final IWindowsImpersonationContext ctx = identity.impersonate();
            Assertions.assertTrue(userInfo.usri1_name.toString().equals(Advapi32Util.getUserName()));
            ctx.revertToSelf();
            Assertions.assertFalse(userInfo.usri1_name.toString().equals(Advapi32Util.getUserName()));
            identity.dispose();
        } finally {
            Assertions.assertEquals(LMErr.NERR_Success,
                    Netapi32.INSTANCE.NetUserDel(null, userInfo.usri1_name.toString()));
        }
    }

    /**
     * Test get current computer.
     */
    @Test
    public void testGetCurrentComputer() {
        final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
        final IWindowsComputer computer = prov.getCurrentComputer();
        WindowsAuthProviderTests.LOGGER.info(computer.getComputerName());
        assertThat(computer.getComputerName().length()).isGreaterThan(0);
        WindowsAuthProviderTests.LOGGER.info(computer.getJoinStatus());
        WindowsAuthProviderTests.LOGGER.info(computer.getMemberOf());
        final String[] localGroups = computer.getGroups();
        Assertions.assertNotNull(localGroups);
        assertThat(localGroups.length).isGreaterThan(0);
        for (final String localGroup : localGroups) {
            WindowsAuthProviderTests.LOGGER.info(" {}", localGroup);
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
        Assertions.assertNotNull(domains);
        for (final IWindowsDomain domain : domains) {
            WindowsAuthProviderTests.LOGGER.info("{}: {}", domain.getFqn(), domain.getTrustDirectionString());
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
                serverContext = provider.acceptSecurityToken(connectionId, clientContext.getToken(), securityPackage);

                if (serverContext != null && serverContext.isContinue()) {
                    // initialize on the client
                    final ManagedSecBufferDesc continueToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN,
                            serverContext.getToken());
                    clientContext.initialize(clientContext.getHandle(), continueToken, targetName);
                    WindowsAuthProviderTests.LOGGER.info("Token: {}",
                            Base64.getEncoder().encodeToString(serverContext.getToken()));
                }

            } while (serverContext != null && serverContext.isContinue());

            if (serverContext != null) {
                assertThat(serverContext.getIdentity().getFqn().length()).isGreaterThan(0);

                WindowsAuthProviderTests.LOGGER.info(serverContext.getIdentity().getFqn());
                for (final IWindowsAccount group : serverContext.getIdentity().getGroups()) {
                    WindowsAuthProviderTests.LOGGER.info(" {}", group.getFqn());
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
                assertThat(provider.getContinueContextsSize()).isGreaterThan(0);
            }
            WindowsAuthProviderTests.LOGGER.info("Cached security contexts: {}",
                    Integer.valueOf(provider.getContinueContextsSize()));
            Assertions.assertFalse(max == provider.getContinueContextsSize());
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
                serverContext = provider.acceptSecurityToken(connectionId, clientContext.getToken(), securityPackage);

                if (serverContext != null && serverContext.isContinue()) {
                    // initialize on the client
                    final ManagedSecBufferDesc continueToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN,
                            serverContext.getToken());
                    clientContext.initialize(clientContext.getHandle(), continueToken, targetName);
                }

            } while (serverContext != null && serverContext.isContinue());

            if (serverContext != null) {
                assertThat(serverContext.getIdentity().getFqn().length()).isGreaterThan(0);

                final IWindowsImpersonationContext impersonationCtx = serverContext.impersonate();
                impersonationCtx.revertToSelf();

                WindowsAuthProviderTests.LOGGER.info(serverContext.getIdentity().getFqn());
                for (final IWindowsAccount group : serverContext.getIdentity().getGroups()) {
                    WindowsAuthProviderTests.LOGGER.info(" {}", group.getFqn());
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
