using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;

namespace Waffle.Windows
{
    /// <summary>
    /// Netapi32.dll PInvoke.
    /// </summary>
    public abstract class Netapi32
    {
        /// <summary>
        /// The NetGetJoinInformation function retrieves join status information for the specified computer.
        /// </summary>
        /// <param name="server">String that specifies the DNS or NetBIOS name of the computer on which to call the function.</param>
        /// <param name="domain">Receives the NetBIOS name of the domain or workgroup to which the computer is joined.</param>
        /// <param name="status">Receives the join status of the specified computer.</param>
        /// <returns></returns>
        [DllImport("Netapi32.dll", CharSet = CharSet.Unicode, SetLastError = true)]
        public static extern int NetGetJoinInformation(
          string server,
          out IntPtr domain,
          out NetJoinStatus status);

        /// <summary>
        /// The NetApiBufferFree function frees the memory that the NetApiBufferAllocate function allocates. 
        /// Applications should also call NetApiBufferFree to free the memory that other network management 
        /// functions use internally to return information.
        /// </summary>
        /// <param name="Buffer">A buffer returned previously by another network management function or memory allocated by calling the NetApiBufferAllocate function.</param>
        /// <returns>If the function succeeds, the return value is NERR_Success.</returns>
        [DllImport("Netapi32.dll")]
        public static extern int NetApiBufferFree(IntPtr Buffer);

        /// <summary>
        /// Win32 Result Code Constant.
        /// </summary>
        public const int NERR_Success = 0;
        /// <summary>
        /// Buffer is too small.
        /// </summary>
        public const int NERR_BufTooSmall = 2123;
        /// <summary>
        /// Computer spcified is invalid.
        /// </summary>
        public const int NERR_InvalidComputer = 2351;

        /// <summary>
        /// NetGetJoinInformation() Enumeration
        /// </summary>
        public enum NetJoinStatus
        {
            /// <summary>
            /// Unknown membership status.
            /// </summary>
            NetSetupUnknownStatus = 0,
            /// <summary>
            /// Computer not joined to a domain.
            /// </summary>
            NetSetupUnjoined,
            /// <summary>
            /// Computer joined to a workgroup.
            /// </summary>
            NetSetupWorkgroupName,
            /// <summary>
            /// Computer joined to a domain.
            /// </summary>
            NetSetupDomainName
        } // NETSETUP_JOIN_STATUS

        /// <summary>
        /// The NetLocalGroupEnum function returns information about each local group account on the specified server.
        /// </summary>
        /// <param name="servername">String that specifies the DNS or NetBIOS name of the remote server on which the function is to execute.</param>
        /// <param name="level">The information level of the data.</param>
        /// <param name="bufptr">Pointer to the address of the buffer that receives the information structure.</param>
        /// <param name="prefmaxlen">Specifies the preferred maximum length of returned data, in bytes.</param>
        /// <param name="entriesread">Pointer to a value that receives the count of elements actually enumerated.</param>
        /// <param name="totalentries">Pointer to a value that receives the approximate total number of entries that could have been enumerated from the current resume position.</param>
        /// <param name="resume_handle">Pointer to a value that contains a resume handle that is used to continue an existing local group search.</param>
        /// <returns>If the function succeeds, the return value is NERR_Success.</returns>
        [DllImport("Netapi32.dll")]
        public extern static int NetLocalGroupEnum(
            [MarshalAs(UnmanagedType.LPWStr)] string servername,
            int level,
            out IntPtr bufptr,
            int prefmaxlen,
            out int entriesread,
            out int totalentries,
            ref int resume_handle);

        /// <summary>
        /// LOCALGROUP_INFO_0
        /// </summary>
        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        public struct LOCALGROUP_USERS_INFO_0
        {
            /// <summary>
            /// Group name.
            /// </summary>
            [MarshalAs(UnmanagedType.LPWStr)]
            public string name;
        }
    }
}
