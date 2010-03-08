package waffle.windows.auth.impl;

import java.net.InetAddress;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

public class WindowsAuthProviderImpl implements IWindowsAuthProvider {

	@Override
	public IWindowsSecurityContext acceptSecurityToken(byte[] token,
			String securityPackage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWindowsComputer getCurrentComputer() throws Exception {
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
	public IWindowsIdentity logonDomainUser(String username, String domain,
			String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWindowsIdentity logonDomainUserEx(String username, String domain,
			String password, int logonType, int logonProvider) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWindowsIdentity logonUser(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWindowsAccount lookupAccount(String username) throws Exception {
		return new WindowsAccountImpl(username);
	}	
}
