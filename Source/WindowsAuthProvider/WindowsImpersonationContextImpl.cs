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
    /// Implementation of <see cref="T:Waffle.Windows.AuthProvider.IWindowsImpersonationContext" />.
    /// </summary>
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None), ProgId("Waffle.Windows.ImpersonationContext")]
    public class WindowsImpersonationContextImpl : IWindowsImpersonationContext
    {
        private WindowsImpersonationContext _ctx;

        /// <summary>
        /// Create a new impersonation context wrapper.
        /// </summary>
        /// <param name="ctx">Impersonation context.</param>
        public WindowsImpersonationContextImpl(WindowsImpersonationContext ctx)
        {
            _ctx = ctx;
        }

        #region IWindowsImpersonationContext Members

        /// <summary>
        /// Revert to the previous context.
        /// </summary>
        public void RevertToSelf()
        {
            _ctx.Undo();
        }

        #endregion
    }
}
