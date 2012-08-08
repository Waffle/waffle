using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Windows computer.
    /// </summary>
    [Guid("EDA1067F-076C-4ca0-9653-E9F87D8497AF")]
    [ComVisible(true)]
    [InterfaceType(ComInterfaceType.InterfaceIsDual)]
    public interface IWindowsComputer
    {
        /// <summary>
        /// Computer name.
        /// </summary>
        string ComputerName { get; }
        /// <summary>
        /// Current computer domain or workgroup name.
        /// </summary>
        string MemberOf { get; }
        /// <summary>
        /// Computer join status, <see cref="T:Waffle.Windows.Netapi32.NetJoinStatus" /> for possible values.
        /// </summary>
        string JoinStatus { get; }
        /// <summary>
        /// List of security groups available on this computer.
        /// </summary>
        string[] Groups { get; }
    }
}
