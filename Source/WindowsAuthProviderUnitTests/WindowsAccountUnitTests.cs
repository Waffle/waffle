using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using Waffle.Windows.AuthProvider;
using System.Security.Principal;

namespace Waffle.Windows.AuthProvider.UnitTests
{
    [TestFixture]
    public class WindowsAccountUnitTests
    {
        [Test]
        public void LookupAccountNameUnitTest()
        {
            WindowsAccountImpl windowsAccount = new WindowsAccountImpl(WindowsIdentity.GetCurrent().Name);
            Console.WriteLine(windowsAccount.Fqn);
            Console.WriteLine(windowsAccount.SidString);
            Assert.AreEqual(windowsAccount.Sid, WindowsIdentity.GetCurrent().User);
        }
    }
}
