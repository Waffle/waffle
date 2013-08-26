using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using Waffle.Windows;
using System.Runtime.InteropServices;
using System.ComponentModel;
using System.Security.Principal;
using System.DirectoryServices;
using System.DirectoryServices.ActiveDirectory;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsDomain" />.
    /// </summary>
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None), ProgId("Waffle.Windows.Domain")]
    public class WindowsDomainImpl : IWindowsDomain
    {
        private string _fqn;
        private TrustDirection _trustDirection = TrustDirection.Bidirectional;
        private TrustType _trustType = TrustType.Unknown;

        /// <summary>
        /// Default constructor.
        /// </summary>
        /// <param name="fqn">Fully qualified domain name.</param>
        public WindowsDomainImpl(string fqn)
        {
            _fqn = fqn;
        }

        /// <summary>
        /// Trust constructor.
        /// </summary>
        /// <param name="fqn">Fully qualified domain name.</param>
        /// <param name="trust">Domain trust, source of the domain information.</param>
        public WindowsDomainImpl(string fqn, TrustRelationshipInformation trust)
        {
            _fqn = fqn;
            _trustDirection = trust.TrustDirection;
            _trustType = trust.TrustType;
        }

        /// <summary>
        /// Fully qualified domain name.
        /// </summary>
        public string Fqn
        {
            get { return _fqn; }
        }

        /// <summary>
        /// Trust direction.
        /// </summary>
        public string TrustDirectionString
        {
            get { return _trustDirection.ToString(); }
        }

        /// <summary>
        /// Trust type.
        /// </summary>
        public string TrustTypeString
        {
            get { return _trustType.ToString(); }
        }

        /// <summary>
        /// List of security groups on this domain.
        /// </summary>
        public string[] Groups
        {
            get
            {
                List<string> groups = new List<string>();
                DirectoryContext domainContext = new DirectoryContext(DirectoryContextType.Domain, Fqn);
                Domain domain = Domain.GetDomain(domainContext);
                DirectorySearcher groupsSearcher = new DirectorySearcher(domain.GetDirectoryEntry());
                groupsSearcher.Filter = "(|(&(objectCategory=Group)(objectClass=Group)(|(groupType=-2147483644)(groupType=-2147483646)(groupType=-2147483640))))"; 
                SearchResultCollection results = groupsSearcher.FindAll();
                foreach (SearchResult searchResult in results)
                {
                    string groupName = string.Format(@"{0}\{1}",
                        domain.GetDirectoryEntry().Properties["name"].Value,
                        searchResult.GetDirectoryEntry().Properties["samAccountName"].Value);
                    groups.Add(groupName);
                }
                return groups.ToArray();
            }
        }

        /// <summary>
        /// Domain's canonical (distinguished) name.
        /// </summary>
        public string CanonicalName
        {
            get
            {
                DirectoryContext domainContext = new DirectoryContext(DirectoryContextType.Domain, Fqn);
                Domain domain = Domain.GetDomain(domainContext);
                return domain.Name;
            }
        }
    }
}
