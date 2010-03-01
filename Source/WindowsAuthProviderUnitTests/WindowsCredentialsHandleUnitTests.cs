using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;

namespace Waffle.Windows.AuthProvider.UnitTests
{
    [TestFixture]
    public class WindowsCredentialsHandleUnitTests
    {
        [Test]
        public void TestNegotiate()
        {
            using (WindowsCredentialsHandle handle = new WindowsCredentialsHandle(
                string.Empty, Secur32.SECPKG_CRED_OUTBOUND, "Negotiate"))
            {
                Assert.AreNotEqual(handle.Handle, Secur32.SecHandle.Zero);
            }
        }
    }
}
