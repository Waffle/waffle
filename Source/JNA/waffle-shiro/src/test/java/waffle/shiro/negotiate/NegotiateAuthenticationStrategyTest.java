package waffle.shiro.negotiate;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dan Rollo
 * Date: 3/18/13
 * Time: 3:34 PM
 */
public class NegotiateAuthenticationStrategyTest {

	private NegotiateAuthenticationStrategy authStrategy;

	@Before
	public void setUp() {
		authStrategy = new NegotiateAuthenticationStrategy();
	}

	@Test (expected = AuthenticationInProgressException.class)
	public void testAfterAttempt() throws Exception {

		final Realm otherRealm = new IniRealm();

		authStrategy.afterAttempt(otherRealm, null, null, null, new RuntimeException());

		final AuthenticationInProgressException authInProgressException = new AuthenticationInProgressException();

		authStrategy.afterAttempt(otherRealm, null, null, null, authInProgressException);

		authStrategy.afterAttempt(new NegotiateAuthenticationRealm(), null, null, null, authInProgressException);
		Assert.fail();
	}
}
