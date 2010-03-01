using System;
using System.Collections.Generic;
using System.Text;
using Waffle.Windows;
using System.Runtime.InteropServices;
using System.ComponentModel;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsAccount" />.
    /// </summary>
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None), ProgId("Waffle.Windows.Account")]
    public class WindowsAccountImpl : IWindowsAccount
    {
        private Advapi32.SID_NAME_USE _sidNameUse;
        private SecurityIdentifier _sid;
        private StringBuilder _referencedDomainName;
        private string _accountName;
        private string _fqn;

        /// <summary>
        /// Lookup a Windows account on a specific system.
        /// </summary>
        /// <param name="username">
        /// Specifies the account name. Use a fully qualified string in the domain_name\user_name format 
        /// to ensure that LookupAccountName finds the account in the desired domain.
        /// </param>
        /// <param name="systemname">
        /// The name of the system. This string can be the name of a remote computer. 
        /// If this string is empty, the account name translation begins on the local system. If the name cannot be 
        /// resolved on the local system, this function will try to resolve the name using domain controllers trusted 
        /// by the local system. Generally, specify a value for lpSystemName only when the account is in an untrusted 
        /// domain and the name of a computer in that domain is known. 
        /// </param>
        public WindowsAccountImpl(string username, string systemname)
        {
            LookupAccount(username, systemname);
        }

        /// <summary>
        /// Lookup a Windows account on the local system.
        /// </summary>
        /// <param name="username">
        /// Specifies the account name. Use a fully qualified string in the domain_name\user_name format 
        /// to ensure that LookupAccountName finds the account in the desired domain.
        /// </param>
        public WindowsAccountImpl(string username)
            : this(username, string.Empty)
        {

        }

        private void LookupAccount(string accountname, string systemname)
        {
            byte[] sid = null;
            uint cbSid = 0;
            _referencedDomainName = new StringBuilder();
            uint cchReferencedDomainName = (uint)_referencedDomainName.Capacity;

            if (Advapi32.LookupAccountName(systemname, accountname, sid, ref cbSid, _referencedDomainName, ref cchReferencedDomainName, out _sidNameUse))
            {
                throw new Exception(string.Format("LookupAccountName failed for {0}\\{1}",
                    string.IsNullOrEmpty(systemname) ? "." : systemname, accountname));
            }

            int err = Marshal.GetLastWin32Error();
            switch (err)
            {
                case Windows.ERROR_INSUFFICIENT_BUFFER:
                case Windows.ERROR_INVALID_FLAGS:
                    sid = new byte[cbSid];
                    _referencedDomainName.EnsureCapacity((int)cchReferencedDomainName);
                    break;
                default:
                    throw new Win32Exception(err);
            }

            if (!Advapi32.LookupAccountName(systemname, accountname, sid, ref cbSid, _referencedDomainName, ref cchReferencedDomainName, out _sidNameUse))
            {
                throw new Win32Exception(Marshal.GetLastWin32Error(),
                    string.Format("LookupAccountName failed for {0}\\{1}",
                    string.IsNullOrEmpty(systemname) ? "." : systemname, accountname));
            }

            string[] accountNamePartsBs = accountname.Split(@"\\".ToCharArray(), 2);
            string[] accountNamePartsAt = accountname.Split("@".ToCharArray(), 2);

            if (accountNamePartsBs.Length == 2)
                _accountName = accountNamePartsBs[1];
            else if (accountNamePartsAt.Length == 2)
                _accountName = accountNamePartsAt[0];
            else 
                _accountName = accountname;

            _sid = new SecurityIdentifier(sid, 0);
            _fqn = string.Format(@"{0}\{1}", _referencedDomainName, _accountName);
        }

        /// <summary>
        /// User's security identifier (SID).
        /// </summary>
        public SecurityIdentifier Sid
        {
            get
            {
                return _sid;
            }
        }

        /// <summary>
        /// Security identifier in a string format.
        /// </summary>
        public string SidString
        {
            get
            {
                return _sid.ToString();
            }
        }

        /// <summary>
        /// Fully qualified username.
        /// </summary>
        public string Fqn
        {
            get
            {
                return _fqn;
            }
        }
    }
}
