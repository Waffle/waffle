/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
package waffle.windows.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.mock.MockWindowsAccount;
import waffle.util.Base64;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.LMAccess;
import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.LMJoin;
import com.sun.jna.platform.win32.Netapi32;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthProviderTests {

	private Logger _log = LoggerFactory.getLogger(WindowsAuthProviderTests.class);

	// TODO This was commented out, uncommented and ignore until I can determine if this is valid
	@Ignore
	@Test
	public void testLogonGuestUser() {
		IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
		IWindowsIdentity identity = prov.logonUser("garbage", "garbage");
		_log.debug("Fqn: {}", identity.getFqn());
		_log.debug("Guest: {}", Boolean.valueOf(identity.isGuest()));
		assertTrue(identity.getFqn().endsWith("\\Guest"));
		assertTrue(identity.isGuest());
		identity.dispose();
	}

	@Test
	public void testLogonUser() {
		LMAccess.USER_INFO_1 userInfo = new LMAccess.USER_INFO_1();
		userInfo.usri1_name = new WString("WaffleTestUser");
		userInfo.usri1_password = new WString("!WAFFLEP$$Wrd0");
		userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
		// ignore test if not able to add user (need to be administrator to do this).
		assumeTrue(LMErr.NERR_Success ==
				Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null));
		try {
			IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
			IWindowsIdentity identity = prov.logonUser(
					userInfo.usri1_name.toString(),
					userInfo.usri1_password.toString());
			assertTrue(identity.getFqn().endsWith(
					"\\" + userInfo.usri1_name.toString()));
			assertFalse(identity.isGuest());
			identity.dispose();
		} finally {
			assertEquals(
					LMErr.NERR_Success,
					Netapi32.INSTANCE.NetUserDel(null,
							userInfo.usri1_name.toString()));
		}
	}

	@Test
	public void testImpersonateLoggedOnUser() {
		LMAccess.USER_INFO_1 userInfo = new LMAccess.USER_INFO_1();
		userInfo.usri1_name = new WString(MockWindowsAccount.TEST_USER_NAME);
		userInfo.usri1_password = new WString(MockWindowsAccount.TEST_PASSWORD);
		userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
		// ignore test if not able to add user (need to be administrator to do this).
		assumeTrue(LMErr.NERR_Success ==
				Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null));
		try {
			IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
			IWindowsIdentity identity = prov.logonUser(
					userInfo.usri1_name.toString(),
					userInfo.usri1_password.toString());
			IWindowsImpersonationContext ctx = identity.impersonate();
			assertTrue(userInfo.usri1_name.toString().equals(
					Advapi32Util.getUserName()));
			ctx.revertToSelf();
			assertFalse(userInfo.usri1_name.toString().equals(
					Advapi32Util.getUserName()));
			identity.dispose();
		} finally {
			assertEquals(
					LMErr.NERR_Success,
					Netapi32.INSTANCE.NetUserDel(null,
							userInfo.usri1_name.toString()));
		}
	}

	@Test
	public void testGetCurrentComputer() {
		IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
		IWindowsComputer computer = prov.getCurrentComputer();
		_log.debug(computer.getComputerName());
		assertTrue(computer.getComputerName().length() > 0);
		_log.debug(computer.getJoinStatus());
		_log.debug(computer.getMemberOf());
		String[] localGroups = computer.getGroups();
		assertNotNull(localGroups);
		assertTrue(localGroups.length > 0);
		for (String localGroup : localGroups) {
			_log.debug(" {}", localGroup);
		}
	}

	@Test
	public void testGetDomains() {
		if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
			return;
		}

		IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
		IWindowsDomain[] domains = prov.getDomains();
		assertNotNull(domains);
		for (IWindowsDomain domain : domains) {
			_log.debug("{}: {}", domain.getFqn(), domain.getTrustDirectionString());
		}
	}

	@Test
	public void testAcceptSecurityToken() {
		String securityPackage = "Negotiate";
		String targetName = "localhost";
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		IWindowsSecurityContext serverContext = null;
		try {
			// client credentials handle
			clientCredentials = WindowsCredentialsHandleImpl
					.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(WindowsAccountImpl
					.getCurrentUsername());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize(null, null, targetName);
			// accept on the server
			WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
			String connectionId = "testConnection-"
					+ Thread.currentThread().getId();
			do {
				// accept the token on the server
				try {
					serverContext = provider.acceptSecurityToken(connectionId,
						clientContext.getToken(), securityPackage);
				} catch (Exception e) {
					_log.error("{}", e);
					break;
				}

				if (serverContext != null && serverContext.isContinue()) {
					// initialize on the client
					SecBufferDesc continueToken = new SecBufferDesc(
							Sspi.SECBUFFER_TOKEN, serverContext.getToken());
					clientContext.initialize(clientContext.getHandle(),
							continueToken, targetName);
					_log.debug("Token: {}", Base64.encode(serverContext.getToken()));
				}

			} while (clientContext.isContinue() || serverContext != null && serverContext.isContinue());

			if (serverContext != null) {
				assertTrue(serverContext.getIdentity().getFqn().length() > 0);

				_log.debug(serverContext.getIdentity().getFqn());
				for (IWindowsAccount group : serverContext.getIdentity()
					.getGroups()) {
					_log.debug(" {}", group.getFqn());
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

	@Test
	public void testSecurityContextsExpire() throws InterruptedException {
		String securityPackage = "Negotiate";
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		IWindowsSecurityContext serverContext = null;
		try {
			// client credentials handle
			clientCredentials = WindowsCredentialsHandleImpl
					.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(WindowsAccountImpl
					.getCurrentUsername());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize(null, null,
					WindowsAccountImpl.getCurrentUsername());
			// accept on the server
			WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl(1);
			int max = 100;
			for (int i = 0; i < max; i++) {
				Thread.sleep(25);
				String connectionId = "testConnection_" + i;
				serverContext = provider.acceptSecurityToken(connectionId,
						clientContext.getToken(), securityPackage);
				assertTrue(provider.getContinueContextsSize() > 0);
			}
			_log.debug("Cached security contexts: {}",
					Integer.valueOf(provider.getContinueContextsSize()));
			assertFalse(max == provider.getContinueContextsSize());
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

	@Test
	public void testAcceptAndImpersonateSecurityToken() {
		String securityPackage = "Negotiate";
		String targetName = "localhost";
		IWindowsCredentialsHandle clientCredentials = null;
		WindowsSecurityContextImpl clientContext = null;
		IWindowsSecurityContext serverContext = null;
		try {
			// client credentials handle
			clientCredentials = WindowsCredentialsHandleImpl
					.getCurrent(securityPackage);
			clientCredentials.initialize();
			// initial client security context
			clientContext = new WindowsSecurityContextImpl();
			clientContext.setPrincipalName(WindowsAccountImpl
					.getCurrentUsername());
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize(null, null, targetName);
			// accept on the server
			WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
			String connectionId = "testConnection";
			do {
				// accept the token on the server
				try {
					serverContext = provider.acceptSecurityToken(connectionId,
						clientContext.getToken(), securityPackage);
				} catch (Exception e) {
					_log.error("{}", e);
					break;
				}

				if (serverContext != null && serverContext.isContinue()) {
					// initialize on the client
					SecBufferDesc continueToken = new SecBufferDesc(
							Sspi.SECBUFFER_TOKEN, serverContext.getToken());
					clientContext.initialize(clientContext.getHandle(),
							continueToken, targetName);
				}

			} while (clientContext.isContinue() || serverContext != null && serverContext.isContinue());

			if (serverContext != null) {
				assertTrue(serverContext.getIdentity().getFqn().length() > 0);

				IWindowsImpersonationContext impersonationCtx = serverContext
					.impersonate();
				impersonationCtx.revertToSelf();

				_log.debug(serverContext.getIdentity().getFqn());
				for (IWindowsAccount group : serverContext.getIdentity()
					.getGroups()) {
					_log.debug(" {}", group.getFqn());
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