using NUnit.Framework;
using System;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider.UnitTests
{
    [TestFixture]
    public class WindowsAccountUnitTests
    {
        [Test]
        public void LookupAccountNameUnitTest()
        {
            Console.WriteLine(WindowsIdentity.GetCurrent().Name);
            WindowsAccountImpl windowsAccount = new WindowsAccountImpl(WindowsIdentity.GetCurrent().Name);
            Console.WriteLine(windowsAccount.Fqn);
            Console.WriteLine(windowsAccount.SidString);
            Assert.AreEqual(windowsAccount.Sid, WindowsIdentity.GetCurrent().User);
        }
    }
}
