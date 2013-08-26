using System;
using System.Collections.Generic;
using System.Text;

namespace Waffle.Windows.AuthProvider
{
    /// <summary>
    /// LMCons.h definitions.
    /// </summary>
    public abstract class LMCons
    {
        /// <summary>
        /// A constant set to -1. This value tells the function to allocate the total amount of memory required.
        /// The memory must be freed by the caller.
        /// </summary>
        public static int MAX_PREFERRED_LENGTH = -1;
    }
}
