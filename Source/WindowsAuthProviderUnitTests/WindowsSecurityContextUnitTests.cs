using System;
using System.Collections.Generic;
using System.Text;
using System.Security.Principal;
using System.ComponentModel;
using NUnit.Framework;

namespace Waffle.Windows.AuthProvider.UnitTests
{
    [TestFixture]
    public class WindowsSecurityContextUnitTests
    {
        [Test]
        public void TestNegotiate()
        {
            string package = "Negotiate";
            using (WindowsCredentialsHandle credentialsHandle = new WindowsCredentialsHandle(
                string.Empty, Secur32.SECPKG_CRED_OUTBOUND, package))
            {
                using (WindowsSecurityContext context = new WindowsSecurityContext(
                    WindowsIdentity.GetCurrent().Name,
                    credentialsHandle,
                    package,
                    Secur32.ISC_REQ_CONNECTION,
                    Secur32.SECURITY_NATIVE_DREP))
                {
                    Assert.AreNotEqual(context.Context, Secur32.SecHandle.Zero);
                    Assert.IsNotNull(context.Token);
                    Assert.IsNotEmpty(context.Token);
                    Console.WriteLine(Convert.ToBase64String(context.Token));
                }
            }
        }

        public void TestGetCurrentNegotiate()
        {
            using (WindowsSecurityContext context = WindowsSecurityContext.GetCurrent("Negotiate",
                WindowsIdentity.GetCurrent().Name, Secur32.ISC_REQ_CONNECTION, Secur32.SECURITY_NATIVE_DREP))
            {
                Assert.AreNotEqual(context.Context, Secur32.SecHandle.Zero);
                Assert.IsNotNull(context.Token);
                Assert.IsNotEmpty(context.Token);
                Console.WriteLine(Convert.ToBase64String(context.Token));
            }
        }

        [Test]
        public void TestGetCurrentNTLM()
        {
            using (WindowsSecurityContext context = WindowsSecurityContext.GetCurrent("NTLM",
                WindowsIdentity.GetCurrent().Name, Secur32.ISC_REQ_CONNECTION, Secur32.SECURITY_NATIVE_DREP))
            {
                Assert.AreNotEqual(context.Context, Secur32.SecHandle.Zero);
                Assert.IsNotNull(context.Token);
                Assert.IsNotEmpty(context.Token);
                Console.WriteLine(Convert.ToBase64String(context.Token));
            }
        }

        [Test]
        public void TestGetCurrentInvalidPackage()
        {
            Assert.Throws(Is.TypeOf<Win32Exception>().And.Message.EqualTo("The requested security package does not exist"),
                delegate {
                    using (WindowsSecurityContext context = WindowsSecurityContext.GetCurrent(Guid.NewGuid().ToString(),
                        WindowsIdentity.GetCurrent().Name, 0, 0))
                    {
                        Assert.AreNotEqual(context.Context, Secur32.SecHandle.Zero);
                        Assert.IsNotNull(context.Token);
                        Assert.IsNotEmpty(context.Token);
                        Console.WriteLine(Convert.ToBase64String(context.Token));
                    }
                }
            );
        }
    }
}
