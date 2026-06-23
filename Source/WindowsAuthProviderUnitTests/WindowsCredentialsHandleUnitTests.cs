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
                Assert.That(handle.Handle, Is.Not.EqualTo(Secur32.SecHandle.Zero));
            }
        }
    }
}
