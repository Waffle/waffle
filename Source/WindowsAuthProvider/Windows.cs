using System;
using System.Collections.Generic;
using System.Text;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// Windows enumerations.
    /// </summary>
    public abstract class Windows
    {
        /// <summary>
        /// No error.
        /// </summary>
        public const int NO_ERROR = 0;
        /// <summary>
        /// Insufficient buffer.
        /// </summary>
        public const int ERROR_INSUFFICIENT_BUFFER = 122;
        /// <summary>
        /// On Windows Server 2003 this error is/can be returned, but processing can still continue.
        /// </summary>
        public const int ERROR_INVALID_FLAGS = 1004;
        /// <summary>
        /// More data is available.
        /// </summary>
        public const int ERROR_MORE_DATA = 234;
    }
}
