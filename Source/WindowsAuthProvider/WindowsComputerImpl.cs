using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using Waffle.Windows;
using System.ComponentModel;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// A Windows computer joined to a workgroup or domain.
    /// </summary>
    public class WindowsComputerImpl : IWindowsComputer
    {
        private string _computerName;
        private string _memberOf;
        private Netapi32.NetJoinStatus _joinStatus = Netapi32.NetJoinStatus.NetSetupUnknownStatus;

        /// <summary>
        /// Default constructor for the current computer.
        /// </summary>
        public WindowsComputerImpl()
            : this(Environment.MachineName)
        {

        }

        /// <summary>
        /// Constructor with a computer name.
        /// </summary>
        /// <param name="computerName">Computer name.</param>
        public WindowsComputerImpl(string computerName)
        {
            _computerName = computerName;

            IntPtr pDomain = IntPtr.Zero;
            try
            {
                int rc = Netapi32.NetGetJoinInformation(computerName, out pDomain, out _joinStatus);
                if (rc == Netapi32.NERR_Success && _joinStatus != Netapi32.NetJoinStatus.NetSetupUnjoined)
                {
                    _memberOf = Marshal.PtrToStringAuto(pDomain);
                }
            }
            finally
            {
                if (pDomain != IntPtr.Zero)
                {
                    Netapi32.NetApiBufferFree(pDomain);
                }
            }
        }

        /// <summary>
        /// The name of the computer.
        /// </summary>
        public string ComputerName
        {
            get { return _computerName; }
        }

        /// <summary>
        /// Type of membership.
        /// </summary>
        public string JoinStatus
        {
            get { return _joinStatus.ToString(); }
        }

        /// <summary>
        /// The name of a domain or workgroup this computer is a member of.
        /// </summary>
        public string MemberOf
        {
            get { return _memberOf; }
        }

        /// <summary>
        /// List of security groups available on this computer.
        /// </summary>
        public string[] Groups
        {
            get
            {
                IntPtr bufptr = IntPtr.Zero;
                try
                {
                    int rc = 0;
                    int entriesread = 0;
                    int totalentries = 0;
                    int resume_handle = 0;
                    rc = Netapi32.NetLocalGroupEnum(
                        _computerName,
                        0,
                        out bufptr,
                        LMCons.MAX_PREFERRED_LENGTH,
                        out entriesread,
                        out totalentries,
                        ref resume_handle
                    );

                    if (rc != Netapi32.NERR_Success || bufptr == IntPtr.Zero)
                    {
                        throw new Win32Exception(rc);
                    }

                    string[] groups = new string[entriesread];
                    IntPtr iter = bufptr;
                    for (int i = 0; i < entriesread; i++)
                    {
                        Netapi32.LOCALGROUP_USERS_INFO_0 group = new Netapi32.LOCALGROUP_USERS_INFO_0();
                        group = (Netapi32.LOCALGROUP_USERS_INFO_0)Marshal.PtrToStructure(iter, typeof(Netapi32.LOCALGROUP_USERS_INFO_0));
                        groups[i] = string.Format(@"{0}\{1}", _computerName, group.name);
                        iter = (IntPtr)((int)iter + Marshal.SizeOf(typeof(Netapi32.LOCALGROUP_USERS_INFO_0)));
                    }
                    return groups;
                }
                finally
                {
                    if (bufptr != IntPtr.Zero)
                    {
                        Netapi32.NetApiBufferFree(bufptr);
                    }
                }
            }
        }
    }
}
