package waffle.servlet;

import junit.framework.TestCase;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

public class NegotiateSecurityFilterLoadTests extends TestCase {

	private NegotiateSecurityFilterTests _tests = new NegotiateSecurityFilterTests();
	
	@Override
	public void setUp() {
		_tests.setUp();
	}

	@Override
	public void tearDown() {
		_tests.tearDown();
	}

	private class NegotiateSecurityFilterLoadTest extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			_tests.testNegotiate();
		}
	}
	
	 public void testLoad() throws Throwable {
		 int load = 10;
		 TestRunnable[] runs = new TestRunnable[load];
		 for(int i = 0; i < load; i++) {
			 runs[i] = new NegotiateSecurityFilterLoadTest();
		 }
		 MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(runs);
		 mttr.runTestRunnables();
	 }
}
