package waffle.windows.auth.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
import com.sun.jna.platform.win32.W32API.HANDLEByReference;
import com.sun.jna.ptr.NativeLongByReference;

public class WindowsAuthProviderImpl implements IWindowsAuthProvider {
	
	private static ThreadLocal<CtxtHandle> _phServerContext = null;

	@Override
	public IWindowsSecurityContext acceptSecurityToken(byte[] token, String securityPackage) {

        IWindowsCredentialsHandle serverCredential = new WindowsCredentialsHandleImpl(
                null, Sspi.SECPKG_CRED_INBOUND, securityPackage);
        serverCredential.initialize();

        SecBufferDesc pbServerToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
        SecBufferDesc pbClientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, token);
    	NativeLongByReference pfClientContextAttr = new NativeLongByReference();
    	
    	CtxtHandle phNewServerContext = new CtxtHandle();
    	int rc = Secur32.INSTANCE.AcceptSecurityContext(serverCredential.getHandle(), 
    			_phServerContext == null ? null : _phServerContext.get(),
    			pbClientToken, new NativeLong(Sspi.ISC_REQ_CONNECTION), 
    			new NativeLong(Sspi.SECURITY_NATIVE_DREP), phNewServerContext, 
    			pbServerToken, pfClientContextAttr, null);

    	WindowsSecurityContextImpl sc = new WindowsSecurityContextImpl();
    	sc.setCredentialsHandle(serverCredential.getHandle());
    	sc.setSecurityPackage(securityPackage);
    	sc.setSecurityContext(phNewServerContext);
    	
    	switch (rc)
        {
            case W32Errors.SEC_E_OK:
            	_phServerContext = null;
            	sc.setContinue(false);
            	break;
            case W32Errors.SEC_I_CONTINUE_NEEDED:
            	_phServerContext = new ThreadLocal<CtxtHandle>();
            	_phServerContext.set(phNewServerContext);
            	sc.setToken(pbServerToken.getBytes());
            	sc.setContinue(true);
            	break;
        	default:
                throw new LastErrorException(rc);
        }
    	
    	return sc;

	}

	@Override
	public IWindowsComputer getCurrentComputer() throws UnknownHostException {
		return new WindowsComputerImpl(InetAddress.getLocalHost().getHostName());
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
			if (phUser.getValue() != Kernel32.INVALID_HANDLE_VALUE) {
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
}
