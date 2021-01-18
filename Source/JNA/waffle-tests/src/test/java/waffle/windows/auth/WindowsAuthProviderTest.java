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
 * The Class WindowsAuthProviderTest.
 *
 * @author dblock[at]dblock[dot]org
 */
class WindowsAuthProviderTest {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsAuthProviderTest.class);

    /**
     * Test logon guest user.
     */
    // TODO This was commented out, uncommented and ignore until I can determine if this is valid
    @Disabled
    @Test
    void testLogonGuestUser() {
        final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
        final IWindowsIdentity identity = prov.logonUser("garbage", "garbage");
        WindowsAuthProviderTest.LOGGER.info("Fqn: {}", identity.getFqn());
        WindowsAuthProviderTest.LOGGER.info("Guest: {}", Boolean.valueOf(identity.isGuest()));
        Assertions.assertTrue(identity.getFqn().endsWith("\\Guest"));
        Assertions.assertTrue(identity.isGuest());
        identity.dispose();
    }

    /**
     * Test logon user.
     */
    @Test
    void testLogonUser() {
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
    void testImpersonateLoggedOnUser() {
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
    void testGetCurrentComputer() {
        final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
        final IWindowsComputer computer = prov.getCurrentComputer();
        WindowsAuthProviderTest.LOGGER.info(computer.getComputerName());
        assertThat(computer.getComputerName()).isNotEmpty();
        WindowsAuthProviderTest.LOGGER.info(computer.getJoinStatus());
        WindowsAuthProviderTest.LOGGER.info(computer.getMemberOf());
        final String[] localGroups = computer.getGroups();
        Assertions.assertNotNull(localGroups);
        assertThat(localGroups).isNotEmpty();
        for (final String localGroup : localGroups) {
            WindowsAuthProviderTest.LOGGER.info(" {}", localGroup);
        }
    }

    /**
     * Test get domains.
     */
    @Test
    void testGetDomains() {
        if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            return;
        }

        final IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
        final IWindowsDomain[] domains = prov.getDomains();
        Assertions.assertNotNull(domains);
        for (final IWindowsDomain domain : domains) {
            WindowsAuthProviderTest.LOGGER.info("{}: {}", domain.getFqn(), domain.getTrustDirectionString());
        }
    }

    /**
     * Test accept security token.
     */
    @Test
    void testAcceptSecurityToken() {
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
                    WindowsAuthProviderTest.LOGGER.info("Token: {}",
                            Base64.getEncoder().encodeToString(serverContext.getToken()));
                }

            } while (serverContext != null && serverContext.isContinue());

            if (serverContext != null) {
                assertThat(serverContext.getIdentity().getFqn()).isNotEmpty();

                WindowsAuthProviderTest.LOGGER.info(serverContext.getIdentity().getFqn());
                for (final IWindowsAccount group : serverContext.getIdentity().getGroups()) {
                    WindowsAuthProviderTest.LOGGER.info(" {}", group.getFqn());
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
    void testSecurityContextsExpire() throws InterruptedException {
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
                assertThat(provider.getContinueContextsSize()).isPositive();
            }
            WindowsAuthProviderTest.LOGGER.info("Cached security contexts: {}",
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
    void testAcceptAndImpersonateSecurityToken() {
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
                assertThat(serverContext.getIdentity().getFqn()).isNotEmpty();

                final IWindowsImpersonationContext impersonationCtx = serverContext.impersonate();
                impersonationCtx.revertToSelf();

                WindowsAuthProviderTest.LOGGER.info(serverContext.getIdentity().getFqn());
                for (final IWindowsAccount group : serverContext.getIdentity().getGroups()) {
                    WindowsAuthProviderTest.LOGGER.info(" {}", group.getFqn());
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
