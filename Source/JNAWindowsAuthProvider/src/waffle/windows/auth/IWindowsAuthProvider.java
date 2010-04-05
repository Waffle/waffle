package waffle.windows.auth;

/**
 * Implements Windows authentication functions.
 */
public interface IWindowsAuthProvider {

	/**
	 * The LogonUser function attempts to log a user on to the local computer using a network logon 
	 * type and the default authentication provider.
	 * @param username A string that specifies the name of the user in the UPN format.
	 * @param password A string that specifies the plaintext password for the user account
	 * specified by username. 
	 * @return
	 */
	public IWindowsIdentity logonUser(String username, String password);
	
	/**
	 * The LogonDomainUser function attempts to log a user on to the local computer using 
	 * a network logon type and the default authentication provider.
	 * @param username A string that specifies the name of the user. 
	 * This is the name of the user account to log on to. If you use the user principal 
	 * name (UPN) format, user@DNS_domain_name, the domain parameter must be NULL.
	 * @param domain A string that specifies the name of the domain or server whose 
	 * account database contains the username account. If this parameter is NULL, the 
	 * user name must be specified in UPN format. If this parameter is ".", the function 
	 * validates the account by using only the local account database.
	 * @param password A string that specifies the plaintext password for the user account 
	 * specified by username. 
	 * @return Windows identity.
	 */
	public IWindowsIdentity logonDomainUser(String username, String domain, String password);
	
	/**
	 * The LogonDomainUserEx function attempts to log a user on to the local computer. The 
	 * local computer is the computer from which LogonUser was called. You cannot use LogonUser 
	 * to log on to a remote computer. You specify the user with a user name and domain and 
	 * authenticate the user with a plaintext password.
	 * @param username
	 * @param domain
	 * @param password
	 * @param logonType
	 * @param logonProvider
	 * @return
	 */
	public IWindowsIdentity logonDomainUserEx(String username, String domain, String password, int logonType, int logonProvider);
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public IWindowsAccount lookupAccount(String username) throws Exception;
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public IWindowsComputer getCurrentComputer() throws Exception;
	
	/**
	 * 
	 * @return
	 */
	public IWindowsDomain[] getDomains();
	
	/**
	 * 
	 * @param token
	 * @param securityPackage
	 * @return
	 */
	public IWindowsSecurityContext acceptSecurityToken(byte[] token, String securityPackage);
}
