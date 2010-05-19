package waffle.windows.auth.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsCredentialsHandle;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

import com.sun.jna.LastErrorException;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.Netapi32Util.DomainTrust;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.NativeLongByReference;

public class WindowsAuthProviderImpl implements IWindowsAuthProvider {
	
	private static ConcurrentHashMap<String, CtxtHandle> _continueContexts = 
		new ConcurrentHashMap<String, CtxtHandle>();
	
	@Override
	public IWindowsSecurityContext acceptSecurityToken(String connectionId, byte[] token, String securityPackage) {

        IWindowsCredentialsHandle serverCredential = new WindowsCredentialsHandleImpl(
                null, Sspi.SECPKG_CRED_INBOUND, securityPackage);
        serverCredential.initialize();

        SecBufferDesc pbServerToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
        SecBufferDesc pbClientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, token);
    	NativeLongByReference pfClientContextAttr = new NativeLongByReference();
    	
    	CtxtHandle continueContext = _continueContexts.get(connectionId);
    	
    	CtxtHandle phNewServerContext = new CtxtHandle();
    	int rc = Secur32.INSTANCE.AcceptSecurityContext(serverCredential.getHandle(), 
    			continueContext, pbClientToken, new NativeLong(Sspi.ISC_REQ_CONNECTION), 
    			new NativeLong(Sspi.SECURITY_NATIVE_DREP), phNewServerContext, 
    			pbServerToken, pfClientContextAttr, null);

    	WindowsSecurityContextImpl sc = new WindowsSecurityContextImpl();
    	sc.setCredentialsHandle(serverCredential.getHandle());
    	sc.setSecurityPackage(securityPackage);
    	sc.setSecurityContext(phNewServerContext);
    	
    	switch (rc)
        {
            case W32Errors.SEC_E_OK:
            	_continueContexts.remove(connectionId);
            	sc.setContinue(false);
            	break;
            case W32Errors.SEC_I_CONTINUE_NEEDED:
            	_continueContexts.put(connectionId, phNewServerContext);
            	sc.setToken(pbServerToken.getBytes());
            	sc.setContinue(true);
            	break;
        	default:
            	_continueContexts.remove(connectionId);
                throw new LastErrorException(rc);
        }
    	
    	return sc;

	}

	@Override
	public IWindowsComputer getCurrentComputer() {
		try {
			return new WindowsComputerImpl(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IWindowsDomain[] getDomains() {
		List<IWindowsDomain> domains = new ArrayList<IWindowsDomain>();
		DomainTrust[] trusts = Netapi32Util.getDomainTrusts();
		for(DomainTrust trust : trusts) {
			domains.add(new WindowsDomainImpl(trust));
		}
		return domains.toArray(new IWindowsDomain[0]);
	}

	@Override
	public IWindowsIdentity logonDomainUser(String username, String domain, String password) {
		return logonDomainUserEx(username, domain, password,
				WinBase.LOGON32_LOGON_NETWORK, WinBase.LOGON32_PROVIDER_DEFAULT);
	}

	@Override
	public IWindowsIdentity logonDomainUserEx(String username, String domain,
			String password, int logonType, int logonProvider) {
		HANDLEByReference phUser = new HANDLEByReference();
		try {
			if (! Advapi32.INSTANCE.LogonUser(username, domain, password, 
					logonType, logonProvider, phUser)) {
				throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
			}
			return new WindowsIdentityImpl(phUser.getValue());
		} finally {
			if (phUser.getValue() != WinBase.INVALID_HANDLE_VALUE) {
				Kernel32.INSTANCE.CloseHandle(phUser.getValue());
			}
		}
	}

	@Override
	public IWindowsIdentity logonUser(String username, String password) {		
        // username@domain UPN format is natively supported by the 
		// Windows LogonUser API process domain\\username format
        String domain = null;
        String[] userNameDomain = username.split("\\\\", 2);
        if (userNameDomain.length == 2) {
            username = userNameDomain[1];
            domain = userNameDomain[0];
        }
        return logonDomainUser(username, domain, password);
	}

	@Override
	public IWindowsAccount lookupAccount(String username) {
		return new WindowsAccountImpl(username);
	}

	@Override
	public void resetSecurityToken(String connectionId) {
    	_continueContexts.remove(connectionId);
	}
}
