using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.DirectoryServices.ActiveDirectory;
using System.Runtime.InteropServices;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsAuthProvider" />.
    /// </summary>
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None), ProgId("Waffle.Windows.AuthProvider")]
    public class WindowsAuthProviderImpl : IWindowsAuthProvider
    {
        private Dictionary<string, Secur32.SecHandle> _continueSecHandles = new Dictionary<string, Secur32.SecHandle>();

        /// <summary>
        /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsAuthProvider.LogonUser" />.
        /// </summary>
        public IWindowsIdentity LogonUser(string username, string password)
        {
            WindowsAccountName windowsAccountName = new WindowsAccountName(username);
            return LogonDomainUser(windowsAccountName.AccountName, windowsAccountName.DomainName, password);
        }

        /// <summary>
        /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsAuthProvider.LogonDomainUser" />.
        /// </summary>
        public IWindowsIdentity LogonDomainUser(string username, string domain, string password)
        {
            return LogonDomainUserEx(username, domain, password,
                Advapi32.LogonType.LOGON32_LOGON_NETWORK, Advapi32.LogonProvider.LOGON32_PROVIDER_DEFAULT);
        }

        /// <summary>
        /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsAuthProvider.LogonDomainUserEx" />.
        /// </summary>
        public IWindowsIdentity LogonDomainUserEx(
            string username,
            string domain,
            string password,
            Advapi32.LogonType logonType,
            Advapi32.LogonProvider logonProvider)
        {
            IntPtr hToken = IntPtr.Zero;

            try
            {
                if (!Advapi32.LogonUser(username, domain, password, (int)logonType, (int)logonProvider, out hToken))
                    throw new Win32Exception(Marshal.GetLastWin32Error());

                return new WindowsIdentityImpl(new WindowsIdentity(hToken));
            }
            finally
            {
                if (hToken != null)
                {
                    Kernel32.CloseHandle(hToken);
                    hToken = IntPtr.Zero;
                }
            }
        }

        /// <summary>
        /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsAuthProvider.LookupAccount" />.
        /// </summary>
        public IWindowsAccount LookupAccount(string username)
        {
            return new WindowsAccountImpl(username);
        }

        /// <summary>
        /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsAuthProvider.GetCurrentComputer" />.
        /// </summary>        
        public IWindowsComputer GetCurrentComputer()
        {
            return new WindowsComputerImpl();
        }

        /// <summary>
        /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsAuthProvider.GetDomains" />.
        /// Return the list of domains that are trusted within this active directory.
        /// </summary>
        /// <remarks>
        /// This function throws an exception if the computer is not joined to an ActiveDirectory domain.
        /// Use <see cref="T:Waffle.Windows.AuthProvider.IWindowsAuthProvider.GetCurrentComputer"/> to find out
        /// whether the computer is joined to a domain.
        /// </remarks>
        /// <example>
        /// <![CDATA[
        /// WindowsAuthProviderImpl windowsAuthProviderImpl = new WindowsAuthProviderImpl();
        /// IWindowsComputer computer = windowsAuthProviderImpl.GetCurrentComputer();
        /// if (computer.JoinStatus == Netapi32.NetJoinStatus.NetSetupDomainName.ToString()) 
        /// {
        ///     IWindowsDomain[] domains = windowsAuthProviderImpl.GetDomains();
        ///     foreach (IWindowsDomain domain in domains) 
        ///     { 
        ///         Console.WriteLine("{0} ({1}, {2})", domain.Fqn, domain.TrustDirectionString, domain.TrustTypeString);
        ///     }
        /// }
        /// ]]>
        /// </example>
        /// <returns>A list of domains.</returns>
        public IWindowsDomain[] GetDomains()
        {
            WindowsDomainCollection domains = new WindowsDomainCollection();
            Domain computerDomain = Domain.GetComputerDomain();
            domains.Add(new WindowsDomainImpl(computerDomain.Name));
            foreach (TrustRelationshipInformation trust in computerDomain.GetAllTrustRelationships())
            {
                domains.Add(new WindowsDomainImpl(trust.TargetName));
                domains.Add(new WindowsDomainImpl(trust.SourceName));
            }
            return domains.ToArray();
        }

        /// <summary>
        /// Retrieve a specific Active Directory domain.
        /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsAuthProvider.GetDomain" />.
        /// </summary>
        /// <param name="friendlyDomainName">A friendly domain name.</param>
        /// <returns>An instance of a domains.</returns>
        public IWindowsDomain GetDomain(string friendlyDomainName)
        {
            return new WindowsDomainImpl(friendlyDomainName);
        }

        /// <summary>
        /// Validate a security token and return the windows account of the user that generated the token.
        /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsAuthProvider.ValidateNegotiateToken" />.
        /// </summary>
        /// <remarks>
        /// This function supports AcceptSecurityToken continuation on the same thread.
        /// </remarks>
        /// <param name="connectionId">Connection id.</param>
        /// <param name="token">Security token.</param>
        /// <param name="securityPackage">Security package, eg. "Negotiate".</param>
        /// <param name="fContextReq"></param>
        /// <param name="targetDataRep"></param>
        /// <returns></returns>
        public IWindowsSecurityContext AcceptSecurityToken(string connectionId, byte[] token, string securityPackage,
            int fContextReq, int targetDataRep)
        {
            Secur32.SecHandle newContext = Secur32.SecHandle.Zero;
            Secur32.SecBufferDesc serverToken = Secur32.SecBufferDesc.Zero;
            Secur32.SecBufferDesc clientToken = Secur32.SecBufferDesc.Zero;
            Secur32.SECURITY_INTEGER serverLifetime = Secur32.SECURITY_INTEGER.Zero;

            WindowsCredentialsHandle credentialsHandle = new WindowsCredentialsHandle(
                string.Empty, Secur32.SECPKG_CRED_INBOUND, securityPackage);

            var tokenSize = Secur32.MAX_TOKEN_SIZE;
            var rc = 0;

            do
            {
                serverToken = new Secur32.SecBufferDesc(tokenSize);
                clientToken = new Secur32.SecBufferDesc(token);
                uint serverContextAttributes = 0;

                Secur32.SecHandle continueSecHandle = Secur32.SecHandle.Zero;
                lock (_continueSecHandles)
                {
                    _continueSecHandles.TryGetValue(connectionId, out continueSecHandle);
                }

                if (continueSecHandle == Secur32.SecHandle.Zero)
                {
                    rc = Secur32.AcceptSecurityContext(
                        ref credentialsHandle.Handle,
                        IntPtr.Zero,
                        ref clientToken,
                        fContextReq,
                        targetDataRep,
                        ref newContext,
                        ref serverToken,
                        out serverContextAttributes,
                        out serverLifetime);
                }
                else
                {
                    rc = Secur32.AcceptSecurityContext(
                        ref credentialsHandle.Handle,
                        ref continueSecHandle,
                        ref clientToken,
                        fContextReq,
                        targetDataRep,
                        ref newContext,
                        ref serverToken,
                        out serverContextAttributes,
                        out serverLifetime);
                }

                switch (rc)
                {
                    case Secur32.SEC_E_INSUFFICIENT_MEMORY:
                        tokenSize += Secur32.MAX_TOKEN_SIZE;
                        break;
                    case Secur32.SEC_E_OK:

                        lock (_continueSecHandles)
                        {
                            _continueSecHandles.Remove(connectionId);
                        }

                        return new WindowsSecurityContext(
                            newContext,
                            serverContextAttributes,
                            serverLifetime,
                            serverToken,
                            securityPackage,
                            false);

                    case Secur32.SEC_I_CONTINUE_NEEDED:

                        lock (_continueSecHandles)
                        {
                            _continueSecHandles[connectionId] = newContext;
                        }

                        return new WindowsSecurityContext(
                            newContext,
                            serverContextAttributes,
                            serverLifetime,
                            serverToken,
                            securityPackage,
                            true);

                    default:

                        lock (_continueSecHandles)
                        {
                            _continueSecHandles.Remove(connectionId);
                        }

                        throw new Win32Exception(rc);
                }
            } while (rc == Secur32.SEC_E_INSUFFICIENT_MEMORY);

            return null;
        }

        /// <summary>
        /// Reset previously saved connection-based security token.
        /// </summary>
        /// <param name="connectionId">Connection id.</param>
        public void ResetSecurityToken(string connectionId)
        {
            lock (_continueSecHandles)
            {
                _continueSecHandles.Remove(connectionId);
            }
        }

    }
}
