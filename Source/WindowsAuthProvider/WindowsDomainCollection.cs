using System;
using System.Collections.Generic;
using System.Text;
using System.DirectoryServices.ActiveDirectory;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// A collection of domains.
    /// </summary>
    public class WindowsDomainCollection : ICollection<IWindowsDomain>, IEnumerable<IWindowsDomain>
    {
        private List<IWindowsDomain> _domains = new List<IWindowsDomain>();
        
        #region IEnumerable<IWindowsDomain> Members

        /// <summary>
        /// Domain enumerator.
        /// </summary>
        /// <returns>Domain enumerator.</returns>
        public IEnumerator<IWindowsDomain> GetEnumerator()
        {
            return _domains.GetEnumerator();
        }

        #endregion

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return _domains.GetEnumerator();
        }

        #endregion

        #region ICollection<IWindowsDomain> Members

        /// <summary>
        /// Add a unique domain.
        /// </summary>
        /// <param name="item">Domain name.</param>
        public void Add(IWindowsDomain item)
        {
            if (! Contains(item))
            {
                _domains.Add(item);
            }
        }

        /// <summary>
        /// Add a domain by name.
        /// </summary>
        /// <param name="domainName">Domain name.</param>
        public void Add(string domainName)
        {
            Add(new WindowsDomainImpl(domainName));
        }

        /// <summary>
        /// Add a domain by name.
        /// </summary>
        /// <param name="domainTrust">Domain trust, source of the domain information.</param>
        public void Add(TrustRelationshipInformation domainTrust)
        {
            Add(new WindowsDomainImpl(domainTrust.SourceName, domainTrust));
            Add(new WindowsDomainImpl(domainTrust.TargetName, domainTrust));
        }

        /// <summary>
        /// Remove all elements from this collection.
        /// </summary>
        public void Clear()
        {
            _domains.Clear();
        }

        /// <summary>
        /// Returns true if the domain is already in the collection.
        /// </summary>
        /// <param name="item">Target domain.</param>
        /// <returns>True if the domain is in the collection.</returns>
        public bool Contains(IWindowsDomain item)
        {
            foreach (IWindowsDomain domain in _domains)
            {
                if (item.Fqn == domain.Fqn)
                    return true;
            }

            return false;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="array"></param>
        /// <param name="arrayIndex"></param>
        public void CopyTo(IWindowsDomain[] array, int arrayIndex)
        {
            _domains.CopyTo(array, arrayIndex);
        }

        /// <summary>
        /// Number of items in this collection.
        /// </summary>
        public int Count
        {
            get { return _domains.Count; }
        }

        /// <summary>
        /// 
        /// </summary>
        public bool IsReadOnly
        {
            get { return false; }
        }

        /// <summary>
        /// Remove a domain from this collection.
        /// </summary>
        /// <param name="item">Domain.</param>
        /// <returns>True if removed.</returns>
        public bool Remove(IWindowsDomain item)
        {
            bool removed = false;
            for (int i = _domains.Count - 1; i >= 0; i--)
            {
                if (_domains[i].Fqn == item.Fqn)
                {
                    _domains.RemoveAt(i);
                    removed = true;
                }
            }
            return removed;
        }

        #endregion

        /// <summary>
        /// Covert domain to an array.
        /// </summary>
        /// <returns>An array of domains.</returns>
        public IWindowsDomain[] ToArray()
        {
            return _domains.ToArray();
        }
    }
}
