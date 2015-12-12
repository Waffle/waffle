using System;
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
        private string _targetName;
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
        /// <param name="targetName">Target name for this security context.</param>
        /// <param name="credentials">Credentials handle.</param>
        /// <param name="securityPackage">Security package.</param>
        /// <param name="fContextReq">Bit flags that indicate requests for the context.</param>
        /// <param name="targetDataRep">Target data representation.</param>
        public WindowsSecurityContext(string targetName, WindowsCredentialsHandle credentials, string securityPackage, int fContextReq, int targetDataRep)
        {
            _targetName = targetName;
            _credentials = credentials.Handle;
            _securityPackage = securityPackage;

            Initialize(credentials.Handle, _targetName, fContextReq, targetDataRep);
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
            _targetName = init._targetName;
            _credentials = init._credentials;
            _securityPackage = init._securityPackage;

            using (var continueTokenBuffer = new Secur32.SecBufferDesc(continueToken))
            {
                Initialize(_credentials, _targetName, fContextReq, targetDataRep, init._context, continueTokenBuffer);
            }
        }

        private void Initialize(Secur32.SecHandle credentials, string targetName, int fContextReq, int targetDataRep)
        {
            this.Initialize(credentials, targetName, fContextReq, targetDataRep, Secur32.SecHandle.Zero, Secur32.SecBufferDesc.Zero);
        }

        private void Initialize(Secur32.SecHandle credentials, string targetName, int fContextReq, int targetDataRep, Secur32.SecHandle context, Secur32.SecBufferDesc continueTokenBuffer)
        {
            var tokenSize = Secur32.MAX_TOKEN_SIZE;
            var rc = 0;
            var hasContextAndContinue = context != Secur32.SecHandle.Zero && continueTokenBuffer != Secur32.SecBufferDesc.Zero;

            do
            {
                _token.Dispose();
                _token = new Secur32.SecBufferDesc(tokenSize);

                if (hasContextAndContinue)
                {
                    rc = Secur32.InitializeSecurityContext(
                        ref credentials,
                        ref context,
                        targetName,
                        fContextReq,
                        0,
                        targetDataRep,
                        ref continueTokenBuffer,
                        0,
                        ref _context,
                        ref _token,
                        out _contextAttributes,
                        out _contextLifetime);
                }
                else
                {
                    rc = Secur32.InitializeSecurityContext(
                        ref credentials,
                        IntPtr.Zero,
                        targetName,
                        fContextReq,
                        0,
                        targetDataRep,
                        IntPtr.Zero,
                        0,
                        ref _context,
                        ref _token,
                        out _contextAttributes,
                        out _contextLifetime);
                }

                switch (rc)
                {
                    case Secur32.SEC_E_INSUFFICIENT_MEMORY:
                    case Secur32.SEC_E_BUFFER_TOO_SMALL:
                        tokenSize += Secur32.MAX_TOKEN_SIZE;
                        break;
                    case Secur32.SEC_E_OK:
                        break;
                    case Secur32.SEC_I_CONTINUE_NEEDED:
                        _continue = true;
                        break;
                    default:
                        throw new Win32Exception(rc);
                }
            } while (rc == Secur32.SEC_E_INSUFFICIENT_MEMORY || rc == Secur32.SEC_E_BUFFER_TOO_SMALL);
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
