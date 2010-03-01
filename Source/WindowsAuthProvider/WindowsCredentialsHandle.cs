using System;
using System.Collections.Generic;
using System.Text;
using System.ComponentModel;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Pre-existing credentials of a security principal.
    /// This is a handle to a previously authenticated logon data used by a security 
    /// principal to establish  its own identity, such as a password, or a Kerberos 
    /// protocol ticket.
    /// </summary>
    public class WindowsCredentialsHandle : IDisposable
    {
        /// <summary>
        /// Credential handle.
        /// </summary>
        public Secur32.SecHandle Handle = Secur32.SecHandle.Zero;
        Secur32.SECURITY_INTEGER clientLifetime = Secur32.SECURITY_INTEGER.Zero;

        /// <summary>
        /// Returns the current credentials handle for a given package.
        /// </summary>
        /// <param name="package"></param>
        /// <returns></returns>
        public static WindowsCredentialsHandle GetCurrentCredentialsHandle(string package)
        {
            return new WindowsCredentialsHandle(string.Empty, Secur32.SECPKG_CRED_OUTBOUND, package);
        }

        /// <summary>
        /// A new windows credentials handle.
        /// </summary>
        /// <param name="principal">String that specifies the name of the principal whose credentials the handle will reference.</param>
        /// <param name="package">Name of the security package with which these credentials will be used.</param>
        /// <param name="type">One of Secur32.SECPKG_CRED_OUTBOUND, Secur32.SECPKG_CRED_INBOUND.</param>
        public WindowsCredentialsHandle(string principal, int type, string package)
        {
            int rc = Secur32.AcquireCredentialsHandle(
                principal,
                package,
                type,
                IntPtr.Zero,
                IntPtr.Zero,
                0,
                IntPtr.Zero,
                out Handle,
                out clientLifetime);
            
            if (rc != Secur32.SEC_E_OK)
            {
                throw new Win32Exception(rc);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        public void Dispose()
        {
            if (Handle != Secur32.SecHandle.Zero)
                Secur32.FreeCredentialsHandle(ref Handle);            
        }
    }
}
