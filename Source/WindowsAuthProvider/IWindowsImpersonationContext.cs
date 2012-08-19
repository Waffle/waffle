using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Windows impersonation context.
    /// </summary>
    [Guid("F4B6534F-E7BE-404d-A73D-56EC99F1BADE")]
    [ComVisible(true)]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IWindowsImpersonationContext
    {
        /// <summary>
        /// Revert to previous impersonation context.
        /// </summary>
        void RevertToSelf();
    }
}
