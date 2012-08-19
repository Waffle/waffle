using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// A windows (typically Active Directory) domain or workgroup.
    /// </summary>
    [Guid("F9A25E1C-0F57-479f-BE87-A0523AB07329")]
    [ComVisible(true)]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IWindowsDomain
    {
        /// <summary>
        /// Fully qualified name.
        /// </summary>
        string Fqn { get; }

        /// <summary>
        /// Canonical (DNS) name.
        /// </summary>
        string CanonicalName { get; }

        /// <summary>
        /// Trust direction.
        /// </summary>
        string TrustDirectionString { get; }

        /// <summary>
        /// Trust type.
        /// </summary>
        string TrustTypeString { get; }

        /// <summary>
        /// List of security groups available on this domain.
        /// </summary>
        string[] Groups { get; }
    }
}
