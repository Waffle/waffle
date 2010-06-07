using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using Waffle.Windows;
using System.ComponentModel;
using System.Security.Principal;
using System.DirectoryServices;
using System.DirectoryServices.ActiveDirectory;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsIdentity" />.
    /// </summary>
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None), ProgId("Waffle.Windows.Identity")]
    public class WindowsIdentityImpl : IWindowsIdentity
    {
        WindowsIdentity _identity;

        /// <summary>
        /// Security identifier in a string format.
        /// </summary>
        public string SidString
        {
            get
            {
                return _identity.User.ToString();
            }
        }
        
        /// <summary>
        /// Security identifier in binary form.
        /// </summary>
        public byte[] Sid
        {
            get
            {
                byte[] sid = new byte[_identity.User.BinaryLength];
                _identity.User.GetBinaryForm(sid, 0);
                return sid;
            }
        }

        /// <summary>
        /// Fully qualified username.
        /// </summary>
        public string Fqn
        {
            get
            {
                return _identity.Name;
            }
        }

        /// <summary>
        /// Unqualified username.
        /// </summary>
        private string Username
        {
            get
            {
                WindowsAccountName windowsAccountName = new WindowsAccountName(_identity.Name);
                return windowsAccountName.AccountName;
            }
        }

        /// <summary>
        /// Local and domain groups that this account is a member of.
        /// </summary>
        public string[] Groups
        {
            get
            {
                List<string> groups = new List<string>(_identity.Groups.Count);
                foreach (IdentityReference ir in _identity.Groups)
                {
                    try
                    {
                        NTAccount acc = (NTAccount)ir.Translate(typeof(NTAccount));
                        groups.Add(acc.Value);
                    }
                    catch (IdentityNotMappedException)
                    {
                        // this identity reference could not be translated, ignore
                    }
                }
                return groups.ToArray();
            }
        }

        /// <summary>
        /// Default constructor.
        /// </summary>
        /// <param name="identity">Windows identity.</param>
        public WindowsIdentityImpl(WindowsIdentity identity)
        {
            _identity = identity;
        }

        /// <summary>
        /// Impersonate the current windows identity.
        /// </summary>
        /// <returns></returns>
        public IWindowsImpersonationContext Impersonate()
        {
            return new WindowsImpersonationContextImpl(_identity.Impersonate());
        }
    }
}
