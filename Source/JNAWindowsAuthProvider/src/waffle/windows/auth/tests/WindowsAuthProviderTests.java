package waffle.windows.auth.tests;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

import junit.framework.TestCase;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

public class WindowsAuthProviderTests extends TestCase {

	public void testGetCurrentComputer() throws Exception {
		IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
		IWindowsComputer computer = prov.getCurrentComputer();
		System.out.println(computer.getComputerName());
		assertTrue(computer.getComputerName().length() > 0);
		System.out.println(computer.getJoinStatus());
		System.out.println(computer.getMemberOf());
		String[] localGroups = computer.getGroups();
		assertNotNull(localGroups);
		assertTrue(localGroups.length > 0);
		for(String localGroup : localGroups) {
			System.out.println(" " + localGroup);
		}		
	}	
	
	public void testGetDomains() {
		IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
		IWindowsDomain[] domains = prov.getDomains();
		assertNotNull(domains);
		for(IWindowsDomain domain : domains) {
			System.out.println(domain.getFqn() + ": " + 
					domain.getTrustDirectionString());
		}
	}
	
	public void testAcceptSecurityToken() {
		String securityPackage = "Negotiate";
		// client credentials handle
		IWindowsCredentialsHandle clientCredentials = WindowsCredentialsHandleImpl.getCurrent(
				securityPackage);
		clientCredentials.initialize();
		// initial client security context
		WindowsSecurityContextImpl clientContext = new WindowsSecurityContextImpl();
		clientContext.setPrincipalName(Advapi32Util.getUserName());
		clientContext.setCredentialsHandle(clientCredentials.getHandle());
		clientContext.setSecurityPackage(securityPackage);
		clientContext.initialize();
		// accept on the server
        WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
        IWindowsSecurityContext serverContext = null;
        do
        {        	
        	if (serverContext != null) {
        		// initialize on the client
                SecBufferDesc continueToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, 
            		serverContext.getToken());
                clientContext.initialize(clientContext.getHandle(), continueToken);
        	}
        	
        	// accept the token on the server
            serverContext = provider.acceptSecurityToken(clientContext.getToken(), 
            		securityPackage);
            
        } while (clientContext.getContinue() || serverContext.getContinue());
        
        assertTrue(serverContext.getIdentity().getFqn().length() > 0);
        // System.out.println(serverContext.getIdentity().getFqn());
        
        serverContext.dispose();
        clientContext.dispose();
        clientCredentials.dispose();
	}
}
