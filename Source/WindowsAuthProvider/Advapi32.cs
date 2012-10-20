using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace Waffle.Windows
{
    /// <summary>
    /// Advapi32.dll PInvoke.
    /// </summary>
    public abstract class Advapi32
    {
        /// <summary>
        /// The LogonUser function attempts to log a user on to the local computer. The local computer is the computer
        /// from which LogonUser was called. You cannot use LogonUser to log on to a remote computer. You specify the 
        /// user with a user name and domain and authenticate the user with a plaintext password.
        /// </summary>
        /// <param name="lpszUsername">
        /// A string that specifies the name of the user. This is the name of the user account to log on to. 
        /// If you use the user principal name (UPN) format, user@DNS_domain_name, the domain parameter must be NULL.
        /// </param>
        /// <param name="lpszDomain">
        /// A string that specifies the name of the domain or server whose account database contains 
        /// the username account. If this parameter is NULL, the user name must be specified in UPN format. 
        /// If this parameter is ".", the function validates the account by using only the local account database.
        /// </param>
        /// <param name="lpszPassword">
        /// A string that specifies the plaintext password for the user account specified by username. 
        /// </param>
        /// <param name="dwLogonType">
        /// The type of logon operation to perform.
        /// </param>
        /// <param name="dwLogonProvider">
        /// Specifies the logon provider. 
        /// </param>
        /// <param name="phToken">
        /// Returned impersonation token.
        /// </param>
        [DllImport("advapi32.dll", SetLastError = true)]
        public static extern bool LogonUser(
            string lpszUsername,
            string lpszDomain,
            string lpszPassword,
            int dwLogonType,
            int dwLogonProvider,
            out IntPtr phToken
            );

        /// <summary>
        /// User logon type.
        /// </summary>
        [Guid("FA3F1B69-D60F-4cbf-9A44-8CE47E5C9FF3")]
        [ComVisible(true)]
        public enum LogonType : int
        {
            /// <summary>
            /// This logon type is intended for users who will be interactively using the computer, such as a user being logged on  
            /// by a terminal server, remote shell, or similar process.
            /// This logon type has the additional expense of caching logon information for disconnected operations; 
            /// therefore, it is inappropriate for some client/server applications,
            /// such as a mail server.
            /// </summary>
            LOGON32_LOGON_INTERACTIVE = 2,

            /// <summary>
            /// This logon type is intended for high performance servers to authenticate plaintext passwords.
            /// The LogonUser function does not cache credentials for this logon type.
            /// </summary>
            LOGON32_LOGON_NETWORK = 3,

            /// <summary>
            /// This logon type is intended for batch servers, where processes may be executing on behalf of a user without 
            /// their direct intervention. This type is also for higher performance servers that process many plaintext
            /// authentication attempts at a time, such as mail or Web servers. 
            /// The LogonUser function does not cache credentials for this logon type.
            /// </summary>
            LOGON32_LOGON_BATCH = 4,

            /// <summary>
            /// Indicates a service-type logon. The account provided must have the service privilege enabled. 
            /// </summary>
            LOGON32_LOGON_SERVICE = 5,

            /// <summary>
            /// This logon type is for GINA DLLs that log on users who will be interactively using the computer. 
            /// This logon type can generate a unique audit record that shows when the workstation was unlocked. 
            /// </summary>
            LOGON32_LOGON_UNLOCK = 7,

            /// <summary>
            /// This logon type preserves the name and password in the authentication package, which allows the server to make 
            /// connections to other network servers while impersonating the client. A server can accept plaintext credentials 
            /// from a client, call LogonUser, verify that the user can access the system across the network, and still 
            /// communicate with other servers.
            /// NOTE: Windows NT:  This value is not supported. 
            /// </summary>
            LOGON32_LOGON_NETWORK_CLEARTEXT = 8,

            /// <summary>
            /// This logon type allows the caller to clone its current token and specify new credentials for outbound connections.
            /// The new logon session has the same local identifier but uses different credentials for other network connections. 
            /// NOTE: This logon type is supported only by the LOGON32_PROVIDER_WINNT50 logon provider.
            /// NOTE: Windows NT:  This value is not supported. 
            /// </summary>
            LOGON32_LOGON_NEW_CREDENTIALS = 9,
        }

        /// <summary>
        /// Windows security provider.
        /// </summary>
        [Guid("32843EC8-13A5-499a-AED3-EAEB08C3381A")]
        [ComVisible(true)]
        public enum LogonProvider : int
        {
            /// <summary>
            /// Use the standard logon provider for the system. 
            /// The default security provider is negotiate, unless you pass NULL for the domain name and the user name 
            /// is not in UPN format. In this case, the default provider is NTLM. 
            /// NOTE: Windows 2000/NT:   The default security provider is NTLM.
            /// </summary>
            LOGON32_PROVIDER_DEFAULT = 0,
        }

        /// <summary>
        /// The SID_NAME_USE enumeration type contains values that specify the type of a security identifier (SID).
        /// </summary>
        [Guid("673D9C4F-13FD-430c-AFD1-4E706FFC0DC4")]
        [ComVisible(true)]
        public enum SID_NAME_USE : int
        {
            /// <summary>
            /// SidTypeUserIndicates a user SID.
            /// </summary>
            SidTypeUser = 1,
            /// <summary>
            /// SidTypeGroupIndicates a group SID.
            /// </summary>
            SidTypeGroup,
            /// <summary>
            /// SidTypeDomainIndicates a domain SID.
            /// </summary>
            SidTypeDomain,
            /// <summary>
            /// SidTypeAliasIndicates an alias SID.
            /// </summary>
            SidTypeAlias,
            /// <summary>
            /// SidTypeWellKnownGroupIndicates a SID for a well-known group.
            /// </summary>
            SidTypeWellKnownGroup,
            /// <summary>
            /// SidTypeDeletedAccountIndicates a SID for a deleted account.
            /// </summary>
            SidTypeDeletedAccount,
            /// <summary>
            /// SidTypeInvalidIndicates an invalid SID.
            /// </summary>
            SidTypeInvalid,
            /// <summary>
            /// SidTypeUnknownIndicates an unknown SID type.
            /// </summary>
            SidTypeUnknown,
            /// <summary>
            /// SidTypeComputerIndicates a SID for a computer.
            /// </summary>
            SidTypeComputer
        }

        /// <summary>
        /// The LookupAccountName function attempts to find a SID for the specified name by first checking a
        /// list of well-known SIDs. If the name does not correspond to a well-known SID, the function checks
        /// built-in and administratively defined local accounts. Next, the function checks the primary domain. 
        /// If the name is not found there, trusted domains are checked.
        /// </summary>
        /// <remarks>
        /// Use fully qualified account names (for example, domain_name\user_name) instead of isolated names 
        /// (for example, user_name). Fully qualified names are unambiguous and provide better performance when 
        /// the lookup is performed. This function also supports fully qualified DNS names 
        /// (for example, example.example.com\user_name) and user principal names (UPN) (for example, someone@example.com).
        /// </remarks>
        /// <param name="lpSystemName">
        /// Specifies the name of the system. This string can be the name of a remote computer. If this string is empty,
        /// the account name translation begins on the local system. If the name cannot be resolved on the local system, 
        /// this function will try to resolve the name using domain controllers trusted by the local system. Generally, 
        /// specify a value for lpSystemName only when the account is in an untrusted domain and the name of a computer 
        /// in that domain is known.
        /// </param>
        /// <param name="lpAccountName">
        /// Specifies the account name. Use a fully qualified string in the domain_name\user_name format to ensure 
        /// that LookupAccountName finds the account in the desired domain.
        /// </param>
        /// <param name="Sid">
        /// Buffer that receives the SID structure that corresponds to the account name pointed to by the lpAccountName parameter.
        /// </param>
        /// <param name="cbSid">
        /// On input, this value specifies the size, in bytes, of the Sid buffer.
        /// If the function fails because the buffer is too small or if cbSid is zero, this variable receives the required buffer size.
        /// </param>
        /// <param name="ReferencedDomainName">
        /// Receives the name of the domain where the account name is found. For computers that are not joined to a domain, 
        /// this buffer receives the computer name. 
        /// </param>
        /// <param name="cchReferencedDomainName">
        /// On input, this value specifies the size, in TCHARs, of the ReferencedDomainName buffer.
        /// If the function fails because the buffer is too small, this variable receives the required 
        /// buffer size, including the terminating null character. 
        /// </param>
        /// <param name="peUse">
        /// Indicates the type of the account when the function returns.
        /// </param>
        /// <returns>If the function succeeds, the function returns nonzero.</returns>
        [DllImport("advapi32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        public static extern bool LookupAccountName(
            string lpSystemName,
            string lpAccountName,
            [MarshalAs(UnmanagedType.LPArray)] byte[] Sid,
            ref uint cbSid,
            StringBuilder ReferencedDomainName,
            ref uint cchReferencedDomainName,
            out SID_NAME_USE peUse);


        /// <summary>
        /// The ConvertSidToStringSid function converts a security identifier (SID) to a string format suitable for display, 
        /// storage, or transmission.
        /// </summary>
        /// <remarks>
        /// Memory for the returned unmanaged string must be freed by calling LocalFree. 
        /// </remarks>
        /// <param name="pSID">A pointer to the SID structure to be converted.</param>
        /// <param name="ptrSid">A pointer to a variable that receives a pointer to a null-terminated SID string. To free the returned buffer, call the LocalFree function.</param>
        /// <returns>If the function succeeds, the return value is nonzero.</returns>
        [DllImport("advapi32", CharSet=CharSet.Auto, SetLastError=true)]
        public static extern bool ConvertSidToStringSid(
            [MarshalAs(UnmanagedType.LPArray)] byte [] pSID, 
            out IntPtr ptrSid);
        /// <summary>
        /// The IsValidSid function validates a  security identifier (SID) by verifying
        /// that the revision number is within a known range, and that the number of
        /// subauthorities is less than the maximum.
        /// </summary>
        /// <param name="pSid">A pointer to the  SID structure to validate. This parameter cannot be NULL</param>
        /// <returns>If the function succeeds, returrns 'true', 'false' othervise.</returns>
        [DllImport("advapi32.dll")]
        public static extern bool IsValidSid([MarshalAs(UnmanagedType.LPArray)] byte[] pSid);
    }
}
