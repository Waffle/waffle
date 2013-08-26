using System;
using System.Collections.Generic;
using System.Text;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// A Windows Account name.
    /// </summary>
    public class WindowsAccountName
    {
        private string _accountName = null;
        private string _domainName = null;

        /// <summary>
        /// Account name.
        /// </summary>
        public string AccountName
        {
            get
            {
                return _accountName;
            }
        }

        /// <summary>
        /// Domain name.
        /// </summary>
        public string DomainName
        {
            get
            {
                return _domainName;
            }
        }

        /// <summary>
        /// Fully qualified name.
        /// </summary>
        public string Fqn
        {
            get
            {
                return _domainName + "\\" + _accountName;
            }
        }

        /// <summary>
        /// A parsed Windows account name.
        /// </summary>
        /// <param name="accountname">Accunt name in the domain\name, name@domain or just username form.</param>
        public WindowsAccountName(string accountname)
        {
            string[] accountNamePartsBs = accountname.Split(@"\\".ToCharArray(), 2);
            string[] accountNamePartsAt = accountname.Split("@".ToCharArray(), 2);

            if (accountNamePartsBs.Length == 2)
            {
                _accountName = accountNamePartsBs[1];
                _domainName = accountNamePartsBs[0];
            }
            else if (accountNamePartsAt.Length == 2)
            {
                _accountName = accountNamePartsAt[0];
                _domainName = accountNamePartsAt[1];
            }
            else
            {
                _accountName = accountname;
                _domainName = Environment.MachineName;
            }
        }
    }
}
