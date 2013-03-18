package waffle.shiro.negotiate;

import junit.framework.TestCase;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;

/**
 * @author Dan Rollo
 * Date: 3/18/13
 * Time: 3:34 PM
 */
public class NegotiateAuthenticationStrategyTest extends TestCase {

    private NegotiateAuthenticationStrategy authStrategy;


    protected void setUp() {
        authStrategy = new NegotiateAuthenticationStrategy();
    }

    public void testAfterAttempt() throws Exception {

        final Realm otherRealm = new IniRealm();

        authStrategy.afterAttempt(otherRealm, null, null, null, new RuntimeException());


        final AuthenticationInProgressException authInProgressException = new AuthenticationInProgressException();

        authStrategy.afterAttempt(otherRealm, null, null, null, authInProgressException);

        try {
            authStrategy.afterAttempt(new NegotiateAuthenticationRealm(), null, null, null, authInProgressException);
            fail();
        } catch (AuthenticationInProgressException e) {
            // this is expected
        }
    }
}
