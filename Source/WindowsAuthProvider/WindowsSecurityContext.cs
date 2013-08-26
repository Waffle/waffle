using System;
using System.Collections.Generic;
using System.Text;
using System.ComponentModel;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// A client side, outbound security context initialized from a credential handle. 
    /// The function is used to build a security context between the client application and 
    /// a remote peer. InitializeSecurityContext (General) returns a token that the client 
    /// must pass to the remote peer, which the peer in turn submits to the local security 
    /// implementation through the AcceptSecurityContext (General) call. The token generated 
    /// should be considered opaque by all callers.
    /// </summary>
    public class WindowsSecurityContext : IWindowsSecurityContext, IDisposable
    {
        private string _securityPackage;
        private Secur32.SecBufferDesc _token = Secur32.SecBufferDesc.Zero;
        private Secur32.SecHandle _context = Secur32.SecHandle.Zero;
        private uint _contextAttributes = 0;
        private Secur32.SECURITY_INTEGER _contextLifetime = Secur32.SECURITY_INTEGER.Zero;
        private bool _continue = false;
        private string _username;
        private Secur32.SecHandle _credentials;

        /// <summary>
        /// Security context.
        /// </summary>
        public Secur32.SecHandle Context
        {
            get
            {
                return _context;
            }
        }

        /// <summary>
        /// Continue security handshake.
        /// </summary>
        public bool Continue
        {
            get
            {
                return _continue;
            }
        }

        /// <summary>
        /// Security token.
        /// </summary>
        public byte[] Token
        {
            get
            {
                return _token.GetSecBufferByteArray();
            }
        }

        /// <summary>
        /// Security package.
        /// </summary>
        public string SecurityPackage
        {
            get
            {
                return _securityPackage;
            }
        }

        /// <summary>
        /// Acquire an existing Windows security context.
        /// </summary>
        /// <param name="context"></param>
        /// <param name="contextAttributes"></param>
        /// <param name="contextLifetime"></param>
        /// <param name="continueNeeded"></param>
        /// <param name="securityPackage"></param>
        /// <param name="token"></param>        
        public WindowsSecurityContext(
            Secur32.SecHandle context,
            uint contextAttributes,
            Secur32.SECURITY_INTEGER contextLifetime,
            Secur32.SecBufferDesc token,
            string securityPackage,
            bool continueNeeded)
        {
            _context = context;
            _contextAttributes = contextAttributes;
            _contextLifetime = contextLifetime;
            _securityPackage = securityPackage;
            _token = token;
            _continue = continueNeeded;
        }

        /// <summary>
        /// Acquire an existing Windows security context.
        /// </summary>
        /// <param name="username">Target username for this security context.</param>
        /// <param name="credentials">Credentials handle.</param>
        /// <param name="securityPackage">Security package.</param>
        /// <param name="fContextReq">Bit flags that indicate requests for the context.</param>
        /// <param name="targetDataRep">Target data representation.</param>
        public WindowsSecurityContext(string username, WindowsCredentialsHandle credentials, string securityPackage, 
            int fContextReq, int targetDataRep)
        {
            _username = username;
            _credentials = credentials.Handle;
            _securityPackage = securityPackage;
            _token = new Secur32.SecBufferDesc(Secur32.MAX_TOKEN_SIZE);
            int rc = Secur32.InitializeSecurityContext(
                ref credentials.Handle,
                IntPtr.Zero,
                username, // service principal name
                fContextReq,
                0,
                targetDataRep,
                IntPtr.Zero,
                0,
                ref _context,
                ref _token,
                out _contextAttributes,
                out _contextLifetime);

            switch (rc)
            {
                case Secur32.SEC_E_OK:
                    break;
                case Secur32.SEC_I_CONTINUE_NEEDED:
                    _continue = true;
                    break;
                default:
                    throw new Win32Exception(rc);
            }
        }

        /// <summary>
        /// A continuation Windows Security Context.
        /// </summary>
        /// <param name="init">
        /// Initial context that contains a handle to the credentials returned by 
        /// AcquireCredentialsHandle. This handle is used to build the security context. 
        /// </param>
        /// <param name="continueToken">
        /// Token returned by the remote computer.
        /// </param>
        /// <param name="fContextReq">
        /// Bit flags that indicate requests for the context. Not all packages can support all requirements. 
        /// Flags used for this parameter are prefixed with ISC_REQ_, for example, ISC_REQ_DELEGATE.
        /// </param>
        /// <param name="targetDataRep">
        /// </param>
        public WindowsSecurityContext(WindowsSecurityContext init, byte[] continueToken, int fContextReq, int targetDataRep)
        {
            _securityPackage = init._securityPackage;
            Secur32.SecBufferDesc continueTokenBuffer = new Secur32.SecBufferDesc(continueToken);
            _token = new Secur32.SecBufferDesc(Secur32.MAX_TOKEN_SIZE);

            int rc = Secur32.InitializeSecurityContext(
                ref init._credentials,
                ref init._context,
                init._username,
                fContextReq,
                0,
                targetDataRep,
                ref continueTokenBuffer,
                0,
                ref _context,
                ref _token,
                out _contextAttributes,
                out _contextLifetime);

            switch (rc)
            {
                case Secur32.SEC_E_OK:
                    break;
                case Secur32.SEC_I_CONTINUE_NEEDED:
                    _continue = true;
                    break;
                default:
                    throw new Win32Exception(rc);
            }
        }

        /// <summary>
        /// Returns the security context of the currently logged on user for a given package.
        /// </summary>
        /// <param name="package"></param>
        /// <param name="targetName"></param>
        /// <param name="fContextReq"></param>
        /// <param name="targetDataRep"></param>
        /// <returns></returns>
        public static WindowsSecurityContext GetCurrent(string package, string targetName, int fContextReq, int targetDataRep)
        {
            using (WindowsCredentialsHandle credentialsHandle = new WindowsCredentialsHandle(
                string.Empty, Secur32.SECPKG_CRED_OUTBOUND, package))
            {
                return new WindowsSecurityContext(
                    targetName,
                    credentialsHandle,
                    package, 
                    fContextReq, 
                    targetDataRep);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="package"></param>
        /// <param name="identity"></param>
        /// <param name="targetName"></param>
        /// <param name="fContextReq"></param>
        /// <param name="targetDataRep"></param>
        /// <returns></returns>
        public static WindowsSecurityContext Get(string package, string targetName, 
            WindowsAuthIdentity identity, int fContextReq, int targetDataRep)
        {
            using (WindowsCredentialsHandle credentialsHandle = new WindowsCredentialsHandle(
                string.Empty, identity, Secur32.SECPKG_CRED_OUTBOUND, package))
            {
                return new WindowsSecurityContext(
                    targetName,
                    credentialsHandle,
                    package,
                    fContextReq,
                    targetDataRep);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        public void Dispose()
        {
            if (_context != Secur32.SecHandle.Zero)
                Secur32.DeleteSecurityContext(ref _context);

            if (_credentials != Secur32.SecHandle.Zero)
                Secur32.FreeCredentialsHandle(ref _credentials);
        }

        /// <summary>
        /// Windows identity for a completed security context.
        /// </summary>
        public IWindowsIdentity Identity
        {
            get
            {
                IntPtr hToken = IntPtr.Zero;
                int rc = Secur32.QuerySecurityContextToken(ref _context, out hToken);
                if (Secur32.SEC_E_OK != rc) throw new Win32Exception(rc);
                return new WindowsIdentityImpl(new WindowsIdentity(hToken));
            }
        }
    }
}
