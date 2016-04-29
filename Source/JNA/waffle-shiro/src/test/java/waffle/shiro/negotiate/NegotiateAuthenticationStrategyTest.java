/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.shiro.negotiate;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class NegotiateAuthenticationStrategyTest.
 *
 * @author Dan Rollo Date: 3/18/13 Time: 3:34 PM
 */
public class NegotiateAuthenticationStrategyTest {

    /** The auth strategy. */
    private NegotiateAuthenticationStrategy authStrategy;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        this.authStrategy = new NegotiateAuthenticationStrategy();
    }

    /**
     * Test after attempt.
     *
     * @throws Exception
     *             the exception
     */
    @Test(expected = AuthenticationInProgressException.class)
    public void testAfterAttempt() throws Exception {

        final Realm otherRealm = new IniRealm();

        this.authStrategy.afterAttempt(otherRealm, null, null, null, new RuntimeException());

        final AuthenticationInProgressException authInProgressException = new AuthenticationInProgressException();

        this.authStrategy.afterAttempt(otherRealm, null, null, null, authInProgressException);

        this.authStrategy.afterAttempt(new NegotiateAuthenticationRealm(), null, null, null, authInProgressException);
        Assert.fail();
    }
}
