using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace Waffle.Windows
{
    /// <summary>
    /// Kernel32.dll PInvoke.
    /// </summary>
    public abstract class Kernel32
    {
        /// <summary>
        /// Close a previously obtained windows handle.
        /// </summary>
        /// <param name="hObject">Windows handle.</param>
        /// <returns>True if the function succeeded.</returns>
        [DllImport("kernel32.dll", SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        public static extern bool CloseHandle(IntPtr hObject);

        /// <summary>
        /// Frees the specified local memory object and invalidates its handle.
        /// </summary>
        /// <param name="hMem">
        /// A handle to the local memory object. This handle is returned by either the 
        /// LocalAlloc or LocalReAlloc function. It is not safe to free memory allocated with GlobalAlloc.
        /// </param>
        /// <returns>If the function succeeds, the return value is NULL.</returns>
        [DllImport("kernel32.dll")]
        static extern IntPtr LocalFree(IntPtr hMem);
    }
}
