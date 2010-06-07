using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Implements Windows authentication functions.
    /// </summary>
    /// <example>
    /// <code language="vbscript">
    /// <![CDATA[
    /// set windowsAuthProviderImpl = CreateObject("Waffle.Windows.AuthProvider")
    /// windowsAuthProviderImpl.LogonUser "Administrator", "password"
    /// windowsAuthProviderImpl.LogonDomainUser "Administrator", "dblock-green", "password"
    /// windowsAuthProviderImpl.LogonDomainUserEx "Administrator", "dblock-green", "password", 3, 0
    /// ]]>
    /// </code>
    /// </example>
    [ComVisible(true)]
    [Guid("E5591146-6974-461a-ABBA-1991E75161A6")]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IWindowsAuthProvider
    {
        /// <summary>
        /// The LogonUser function attempts to log a user on to the local computer using a network logon type and the default
        /// authentication provider.
        /// </summary>
        /// <param name="username">
        /// A string that specifies the name of the user in the UPN format.
        /// </param>
        /// <param name="password">
        /// A string that specifies the plaintext password for the user account specified by username. 
        /// </param>
        /// <returns>Windows identity.</returns>
        [DispId(1)]
        IWindowsIdentity LogonUser(string username, string password);

        /// <summary>
        /// The LogonDomainUser function attempts to log a user on to the local computer using a network logon type and the default
        /// authentication provider.
        /// </summary>
        /// <param name="username">
        /// A string that specifies the name of the user. This is the name of the user account to log on to. 
        /// If you use the user principal name (UPN) format, user@DNS_domain_name, the domain parameter must be NULL.
        /// </param>
        /// <param name="domain">
        /// A string that specifies the name of the domain or server whose account database contains 
        /// the username account. If this parameter is NULL, the user name must be specified in UPN format. 
        /// If this parameter is ".", the function validates the account by using only the local account database.
        /// </param>
        /// <param name="password">
        /// A string that specifies the plaintext password for the user account specified by username. 
        /// </param>
        /// <returns>Windows identity.</returns>
        [DispId(2)]
        IWindowsIdentity LogonDomainUser(string username, string domain, string password);

        /// <summary>
        /// The LogonDomainUserEx function attempts to log a user on to the local computer. The local computer is the computer
        /// from which LogonUser was called. You cannot use LogonUser to log on to a remote computer. You specify the 
        /// user with a user name and domain and authenticate the user with a plaintext password.
        /// </summary>
        /// <param name="username">
        /// A string that specifies the name of the user. This is the name of the user account to log on to. 
        /// If you use the user principal name (UPN) format, user@DNS_domain_name, the domain parameter must be NULL.
        /// </param>
        /// <param name="domain">
        /// A string that specifies the name of the domain or server whose account database contains 
        /// the username account. If this parameter is NULL, the user name must be specified in UPN format. 
        /// If this parameter is ".", the function validates the account by using only the local account database.
        /// </param>
        /// <param name="password">
        /// A string that specifies the plaintext password for the user account specified by username. 
        /// </param>
        /// <param name="logonType">
        /// The type of logon operation to perform.
        /// </param>
        /// <param name="logonProvider">
        /// Specifies the logon provider. 
        /// </param>
        /// <returns>Windows identity.</returns>
        [DispId(3)]
        IWindowsIdentity LogonDomainUserEx(string username, string domain, string password,
            Advapi32.LogonType logonType, Advapi32.LogonProvider logonProvider);

        /// <summary>
        /// Retrieve a security identifier (SID) for the account and the name of the domain or local computer
        /// on which the account was found.
        /// </summary>
        /// <param name="username">Fully qualified or partial username.</param>
        /// <returns>Windows account.</returns>
        [DispId(4)]
        IWindowsAccount LookupAccount(string username);

        /// <summary>
        /// Retrieve the current computer information.
        /// </summary>
        /// <returns>Current computer information.</returns>
        [DispId(5)]
        IWindowsComputer GetCurrentComputer();

        /// <summary>
        /// Retrieve a list of domains (Active Directory) on the local server.
        /// </summary>
        /// <returns>A list of domains.</returns>
        [DispId(6)]
        IWindowsDomain[] GetDomains();

        /// <summary>
        /// Retrieve a specific Active Directory domain.
        /// </summary>
        /// <param name="friendlyDomainName">A friendly domain name.</param>
        /// <returns>An instance of a domains.</returns>
        [DispId(7)]
        IWindowsDomain GetDomain(string friendlyDomainName);

        /// <summary>
        /// Attempts to validate the user using an SSPI token. This token 
        /// is generated by the client via the InitializeSecurityContext(package)
        /// method described in http://msdn.microsoft.com/en-us/library/aa375509(VS.85).aspx
        /// </summary>
        /// <param name="connectionId">Connection id.</param>
        /// <param name="token">The security token generated by the client wishing to logon.</param>
        /// <param name="securityPackage">The name of the security package to use. Can be any security
        /// package supported by both the client and the server. This is usually set to "Negotiate" which
        /// will use SPNEGO to determine which security package to use. 
        /// Other common values are "Kerberos" and "NTLM"</param>
        /// <param name="fContextReq"></param>
        /// <param name="targetDataRep"></param>
        /// <returns>Windows account.</returns>
        [DispId(8)]
        IWindowsSecurityContext AcceptSecurityToken(string connectionId, byte[] token, string securityPackage,
            int fContextReq, int targetDataRep);

        /// <summary>
        /// Reset a previously saved continuation security token for a given connection id.
        /// </summary>
        /// <param name="connectionId">Connection id.</param>
        [DispId(9)]
        void ResetSecurityToken(string connectionId);
    }
}
