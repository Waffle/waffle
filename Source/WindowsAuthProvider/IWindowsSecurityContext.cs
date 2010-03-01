using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// A Windows security context.
    /// </summary>
    [Guid("3090324C-9C7A-4bee-8DDE-37530FE6D24A")]
    [ComVisible(true)]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IWindowsSecurityContext
    {
        /// <summary>
        /// Security package.
        /// </summary>
        string SecurityPackage { get; }

        /// <summary>
        /// A security token that can be sent between client and server.
        /// </summary>
        byte[] Token { get; }

        /// <summary>
        /// True when the security context requires continuation.
        /// </summary>
        bool Continue { get; }

        /// <summary>
        /// Windows identity, available only when this security context doesn't require continuation.
        /// </summary>
        IWindowsIdentity Identity { get; }
    }
}
