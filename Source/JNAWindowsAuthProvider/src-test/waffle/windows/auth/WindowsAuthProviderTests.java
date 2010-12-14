/*******************************************************************************
* Waffle (http://waffle.codeplex.com)
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

import junit.framework.TestCase;
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
public class WindowsAuthProviderTests extends TestCase {

	private void debug(String s) {
		// System.out.println(s);
	}

	/*
	public void testLogonGuestUser() {
		IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
		IWindowsIdentity identity = prov.logonUser("garbage", "garbage");
		debug("Fqn: " + identity.getFqn());
		debug("Guest: " + identity.isGuest());
		assertTrue(identity.getFqn().endsWith("\\Guest"));
		assertTrue(identity.isGuest());
		identity.dispose();
	}
	*/

	public void testLogonUser() {
    	LMAccess.USER_INFO_1 userInfo = new LMAccess.USER_INFO_1();
    	userInfo.usri1_name = new WString("WaffleTestUser");
    	userInfo.usri1_password = new WString("!WAFFLEP$$Wrd0");
    	userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null));
		try {
			IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
			IWindowsIdentity identity = prov.logonUser(userInfo.usri1_name.toString(), userInfo.usri1_password.toString());
			assertTrue(identity.getFqn().endsWith("\\" + userInfo.usri1_name.toString()));
			assertFalse(identity.isGuest());
			identity.dispose();
		} finally {
	    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
	    			null, userInfo.usri1_name.toString()));			
		}
	}
	
	public void testImpersonateLoggedOnUser() {
    	LMAccess.USER_INFO_1 userInfo = new LMAccess.USER_INFO_1();
    	userInfo.usri1_name = new WString("WaffleTestUser");
    	userInfo.usri1_password = new WString("!WAFFLEP$$Wrd0");
    	userInfo.usri1_priv = LMAccess.USER_PRIV_USER;
    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserAdd(null, 1, userInfo, null));
		try {
			IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
			IWindowsIdentity identity = prov.logonUser(userInfo.usri1_name.toString(), userInfo.usri1_password.toString());
			IWindowsImpersonationContext ctx = identity.impersonate();
			assertTrue(userInfo.usri1_name.toString().equals(Advapi32Util.getUserName()));
			ctx.RevertToSelf();
			assertFalse(userInfo.usri1_name.toString().equals(Advapi32Util.getUserName()));
			identity.dispose();
		} finally {
	    	assertEquals(LMErr.NERR_Success, Netapi32.INSTANCE.NetUserDel(
	    			null, userInfo.usri1_name.toString()));			
		}		
	}
	
	public void testGetCurrentComputer() {
		IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
		IWindowsComputer computer = prov.getCurrentComputer();
		debug(computer.getComputerName());
		assertTrue(computer.getComputerName().length() > 0);
		debug(computer.getJoinStatus());
		debug(computer.getMemberOf());
		String[] localGroups = computer.getGroups();
		assertNotNull(localGroups);
		assertTrue(localGroups.length > 0);
		for(String localGroup : localGroups) {
			debug(" " + localGroup);
		}		
	}	
	
	public void testGetDomains() {
		if (Netapi32Util.getJoinStatus() != LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName)
			return;
		
		IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
		IWindowsDomain[] domains = prov.getDomains();
		assertNotNull(domains);
		for(IWindowsDomain domain : domains) {
			debug(domain.getFqn() + ": " + 
					domain.getTrustDirectionString());
		}
	}
	
	public void testAcceptSecurityToken() {
		String securityPackage = "Negotiate";
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
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize();
			// accept on the server
	        WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
	        String connectionId = "testConnection-" + Thread.currentThread().getId();
	        do
	        {
	        	// accept the token on the server
	            serverContext = provider.acceptSecurityToken(connectionId, clientContext.getToken(), 
	            		securityPackage);

	        	if (serverContext != null && serverContext.getContinue()) {
	        		// initialize on the client
	                SecBufferDesc continueToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, 
	            		serverContext.getToken());
	                clientContext.initialize(clientContext.getHandle(), continueToken);	                
                    debug("Token: " + Base64.encode(serverContext.getToken()));
	        	}
	        	            
	        } while (clientContext.getContinue() || serverContext.getContinue());
	        
	        assertTrue(serverContext.getIdentity().getFqn().length() > 0);
	
	        debug(serverContext.getIdentity().getFqn());
	        for (IWindowsAccount group : serverContext.getIdentity().getGroups()) {
	        	debug(" " + group.getFqn());
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
	
	public void testSecurityContextsExpire() throws InterruptedException {
		String securityPackage = "Negotiate";
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
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize();
			// accept on the server
	        WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl(1);
	        int max = 100;
	        for(int i = 0; i < max; i++) {
		        Thread.sleep(10);
	        	String connectionId = "testConnection_" + i;
	        	serverContext = provider.acceptSecurityToken(connectionId, 
	        			clientContext.getToken(), securityPackage);
	        	assertTrue(provider.getContinueContextsSize() > 0);
	        }
	        debug("Cached security contexts: " + provider.getContinueContextsSize());
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
	
	public void testAcceptAndImpersonateSecurityToken() {
		String securityPackage = "Negotiate";
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
			clientContext.setCredentialsHandle(clientCredentials.getHandle());
			clientContext.setSecurityPackage(securityPackage);
			clientContext.initialize();
			// accept on the server
	        WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
	        String connectionId = "testConnection";
	        do
	        {        	
	        	// accept the token on the server
	            serverContext = provider.acceptSecurityToken(connectionId, clientContext.getToken(), 
	            		securityPackage);
	            
	        	if (serverContext != null && serverContext.getContinue()) {
	        		// initialize on the client
	                SecBufferDesc continueToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, 
	            		serverContext.getToken());
	                clientContext.initialize(clientContext.getHandle(), continueToken);
	        	}
	        	
	        } while (clientContext.getContinue() || serverContext.getContinue());
	        
	        assertTrue(serverContext.getIdentity().getFqn().length() > 0);
	        
	        IWindowsImpersonationContext impersonationCtx = serverContext.impersonate();
	        impersonationCtx.RevertToSelf();
	
	        debug(serverContext.getIdentity().getFqn());
	        for (IWindowsAccount group : serverContext.getIdentity().getGroups()) {
	        	debug(" " + group.getFqn());
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