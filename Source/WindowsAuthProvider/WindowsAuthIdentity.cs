using System;
using System.Collections.Generic;
using System.Text;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// A Windows auth identity.
    /// </summary>
    public struct WindowsAuthIdentity
    {
        /// <summary>
        /// A string that contains the user name.
        /// </summary>
        public string Username;
        /// <summary>
        /// A string that contains the domain name or the workgroup name.
        /// </summary>
        public string Domain;
        /// <summary>
        /// A string that contains the password of the user in the domain or workgroup.
        /// </summary>
        public string Password;
    }
}
