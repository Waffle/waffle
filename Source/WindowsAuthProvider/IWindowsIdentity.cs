using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Windows identity.
    /// </summary>
    [Guid("049E251D-6866-439d-A2F2-E5B41F2C9058")]
    [ComVisible(true)]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IWindowsIdentity
    {
        /// <summary>
        /// Security identifier.
        /// </summary>
        string SidString { get; }
        /// <summary>
        /// Security identifier in binary form.
        /// </summary>
        byte[] Sid { get; }
        /// <summary>
        /// Fully qualified name in NETBIOS\username format.
        /// </summary>
        string Fqn { get; }
        /// <summary>
        /// Group memberships.
        /// </summary>
        string[] Groups { get; }
        /// <summary>
        /// Impersonate, return the impersonation context.
        /// </summary>
        IWindowsImpersonationContext Impersonate();
    }
}
