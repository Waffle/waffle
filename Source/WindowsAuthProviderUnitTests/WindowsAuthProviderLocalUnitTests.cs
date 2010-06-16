using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;
using System.Runtime.InteropServices;
using System.Security.Principal;
using System.ComponentModel;

namespace Waffle.Windows.AuthProvider.UnitTests
{
    [TestFixture]
    public class WindowsAuthProviderLocalUnitTests
    {
        private Netapi32.USER_INFO_1 _testUser;
        private String _testUserFqn;
        private Netapi32.NetJoinStatus _joinStatus = Netapi32.NetJoinStatus.NetSetupUnknownStatus;
        private String _memberOf;
        private String _computerName;

        [SetUp]
        public void SetUp()
        {
            // test user
            _testUser = new Netapi32.USER_INFO_1();
            _testUser.usri1_name = "WaffleTestUser";
            _testUser.usri1_password = "password";
            _testUser.usri1_priv = 1;
            _testUser.usri1_home_dir = null;
            _testUser.comment = "Waffle test user.";
            _testUser.usri1_script_path = null;
            int rc = Netapi32.NetUserAdd(null, 1, ref _testUser, 0);
            Assert.AreEqual(0, rc, new Win32Exception(rc).Message);
            // computer
            _computerName = Environment.MachineName;
            // fqn
            _testUserFqn = string.Format("{0}\\{1}", _computerName, _testUser.usri1_name);
            // join status 
            IntPtr pDomain = IntPtr.Zero;
            rc = Netapi32.NetGetJoinInformation(null, out pDomain, out _joinStatus);
            Assert.AreEqual(Netapi32.NERR_Success, rc, new Win32Exception(rc).Message);
            _memberOf = Marshal.PtrToStringAuto(pDomain);
            Netapi32.NetApiBufferFree(pDomain);
        }

        [TearDown]
        public void TearDown()
        {
            int rc = Netapi32.NetUserDel(null, _testUser.usri1_name);
            Assert.AreEqual(0, rc, new Win32Exception(rc).Message);
        }

        [Test]
        public void TestLogonLocalUser()
        {
            WindowsAuthProviderImpl windowsAuthProviderImpl = new WindowsAuthProviderImpl();
            IWindowsIdentity account = windowsAuthProviderImpl.LogonUser(_testUser.usri1_name, _testUser.usri1_password);
            Console.WriteLine("Sid: {0}", account.SidString);
            Console.WriteLine("Fqn: {0}", account.Fqn);
            Console.WriteLine("Guest: {0}", account.IsGuest);
            Assert.AreEqual(account.Fqn.ToLower(), _testUserFqn.ToLower());
            Assert.IsTrue(Advapi32.IsValidSid(account.Sid));
            Assert.IsFalse(account.IsGuest);
            Console.WriteLine("Groups: {0}", account.Groups.Length);
        }

        [Test]
        public void TestImpersonation()
        {
            WindowsAuthProviderImpl windowsAuthProviderImpl = new WindowsAuthProviderImpl();
            IWindowsIdentity account = windowsAuthProviderImpl.LogonUser(_testUser.usri1_name, _testUser.usri1_password);
            IWindowsImpersonationContext impersonationCtx = account.Impersonate();
            try
            {
                Console.WriteLine(account.SidString);
                Console.WriteLine(account.Fqn);
                Assert.AreEqual(account.Fqn, WindowsIdentity.GetCurrent().Name);
            }
            finally
            {
                impersonationCtx.RevertToSelf();
                Assert.AreNotEqual(account.Fqn, WindowsIdentity.GetCurrent().Name);
            }
        }

        [Test]
        public void TestLookupAccount()
        {
            WindowsAuthProviderImpl windowsAuthProviderImpl = new WindowsAuthProviderImpl();
            Console.WriteLine(_testUserFqn);
            IWindowsAccount account = windowsAuthProviderImpl.LookupAccount(_testUserFqn);
            Console.WriteLine(account.SidString);
            Console.WriteLine(account.Fqn);
            Assert.AreEqual(_testUserFqn.ToLower(), account.Fqn.ToLower());
        }

        /*
        [Test]
        public void TestDigest()
        {
            WindowsAuthIdentity identity = new WindowsAuthIdentity();
            identity.Username = _testUser.usri1_name;
            identity.Domain = _computerName;
            identity.Password = _testUser.usri1_password;

            string package = "WDigest";
            WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
            WindowsSecurityContext initContext = WindowsSecurityContext.Get(package,
                "http://localhost", identity, 0, 0);
            IWindowsSecurityContext continueContext = initContext;
            IWindowsSecurityContext responseContext = null;
            string connectionId = Guid.NewGuid().ToString();
            do
            {
                responseContext = provider.AcceptSecurityToken(connectionId, continueContext.Token, package, 0, 0);
                if (responseContext.Token != null)
                {
                    Console.WriteLine("  Token: {0}", Convert.ToBase64String(responseContext.Token));
                    Console.WriteLine("  Continue: {0}", responseContext.Continue);
                }
                continueContext = new WindowsSecurityContext(initContext, responseContext.Token, 0, 0);
            } while (responseContext.Continue);

            Assert.IsFalse(responseContext.Continue);
            Console.WriteLine(responseContext.Identity.Fqn);
        }
        */
    }
}
