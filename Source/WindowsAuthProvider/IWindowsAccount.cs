using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Windows account.
    /// </summary>
    [Guid("9E3DC4C0-1B19-427a-9C70-4A8FEAF92C22")]
    [ComVisible(true)]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IWindowsAccount
    {
        /// <summary>
        /// Security identifier.
        /// </summary>
        string SidString { get; }
        /// <summary>
        /// Fully qualified username.
        /// </summary>
        string Fqn { get; }
    }
}
