package waffle.windows.auth;

import junit.framework.TestCase;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

public class WindowsAuthProviderLoadTests extends TestCase {

	private class WindowsAuthProviderLoadTest extends TestRunnable {
		WindowsAuthProviderTests _tests = new WindowsAuthProviderTests();
		
		@Override
		public void runTest() throws Throwable {
			_tests.testAcceptSecurityToken();
		}
	}
	
	 public void testLoad() throws Throwable {
		 int load = 10;
		 TestRunnable[] runs = new TestRunnable[load];
		 for(int i = 0; i < load; i++) {
			 runs[i] = new WindowsAuthProviderLoadTest();
		 }
		 MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(runs);
		 mttr.runTestRunnables();
	 }
}
