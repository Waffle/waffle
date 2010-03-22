package waffle.windows.auth.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.sun.jna.LastErrorException;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.W32API.HANDLEByReference;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

public class WindowsAuthProviderImpl implements IWindowsAuthProvider {

	@Override
	public IWindowsSecurityContext acceptSecurityToken(byte[] token, String securityPackage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWindowsComputer getCurrentComputer() throws UnknownHostException {
		return new WindowsComputerImpl(InetAddress.getLocalHost().getHostName());
	}

	@Override
	public IWindowsDomain getDomain(String friendlyDomainName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWindowsDomain[] getDomains() {
		// TODO Auto-generated method stub
		return null;
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
