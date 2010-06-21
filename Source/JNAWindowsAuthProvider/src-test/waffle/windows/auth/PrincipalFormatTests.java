package waffle.windows.auth;

import junit.framework.TestCase;
import waffle.windows.auth.PrincipalFormat;

/**
 * @author dblock[at]dblock[dot]org
 */
public class PrincipalFormatTests extends TestCase {
	public void testKnown() {
		assertEquals(PrincipalFormat.fqn, PrincipalFormat.valueOf("fqn"));
		assertEquals(PrincipalFormat.sid, PrincipalFormat.valueOf("sid"));
		assertEquals(PrincipalFormat.both, PrincipalFormat.valueOf("both"));
		assertEquals(PrincipalFormat.none, PrincipalFormat.valueOf("none"));
		assertEquals(4, PrincipalFormat.values().length);
	}

	public void testUnknown() {
		try {
			PrincipalFormat.valueOf("garbage");
			fail("expected RuntimeException");
		} catch(RuntimeException e) {
			// no enum const class
		}
	}
}
