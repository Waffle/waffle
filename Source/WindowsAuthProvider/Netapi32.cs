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

        /// <summary>
        /// The USER_INFO_1 structure contains information about a user account, including account name, 
        /// password data, privilege level, and the path to the user's home directory.
        /// </summary>
        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
        public struct USER_INFO_1
        {
            /// <summary>
            /// A Unicode string that specifies the name of the user account. 
            /// </summary>
            public string usri1_name;
            /// <summary>
            /// a Unicode string that specifies the password of the user indicated by the usri1_name member. 
            /// The length cannot exceed PWLEN bytes. By convention, the length of passwords is limited 
            /// to LM20_PWLEN characters.
            /// </summary>
            public string usri1_password;
            /// <summary>
            /// The number of seconds that have elapsed since the usri1_password member was last changed. 
            /// </summary>
            public int usri1_password_age;
            /// <summary>
            /// The level of privilege assigned to the usri1_name member. 
            /// </summary>
            public int usri1_priv;
            /// <summary>
            /// A Unicode string specifying the path of the home directory for the user specified in the 
            /// usri1_name member. The string can be null.
            /// </summary>
            public string usri1_home_dir;
            /// <summary>
            /// A Unicode string that contains a comment to associate with the user account. This string can 
            /// be a null string, or it can have any number of characters before the terminating null character.
            /// </summary>
            public string comment;
            /// <summary>
            /// User flags, one of UF_ values.
            /// </summary>
            public int usri1_flags;
            /// <summary>
            /// A Unicode string specifying the path for the user's logon script file. 
            /// The script file can be a .CMD file, an .EXE file, or a .BAT file. The string can also be null.
            /// </summary>
            public string usri1_script_path;
        }

        /// <summary>
        /// The NetUserAdd function adds a user account and assigns a password and privilege level.
        /// </summary>
        /// <param name="servername"></param>
        /// <param name="level"></param>
        /// <param name="buf"></param>
        /// <param name="parm_err"></param>
        /// <returns></returns>
        [DllImport("Netapi32.dll")]
        public extern static int NetUserAdd(
            [MarshalAs(UnmanagedType.LPWStr)] string servername, 
            int level,
            ref USER_INFO_1 buf, 
            int parm_err);

        /// <summary>
        /// The NetUserDel function deletes a user account from a server.
        /// </summary>
        /// <param name="servername"></param>
        /// <param name="username"></param>
        /// <returns></returns>
        [DllImport("Netapi32.dll")]
        public extern static int NetUserDel(
            [MarshalAs(UnmanagedType.LPWStr)] string servername, 
            [MarshalAs(UnmanagedType.LPWStr)] string username);
    }
}
