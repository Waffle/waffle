/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.shiro.negotiate;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class NegotiateAuthenticationStrategyTest.
 */
class NegotiateAuthenticationStrategyTest {

    /** The auth strategy. */
    private NegotiateAuthenticationStrategy authStrategy;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.authStrategy = new NegotiateAuthenticationStrategy();
    }

    /**
     * Test after attempt.
     */
    @Test
    void testAfterAttempt() {

        final Realm otherRealm = new IniRealm();

        this.authStrategy.afterAttempt(otherRealm, null, null, null, new RuntimeException());

        final AuthenticationInProgressException authInProgressException = new AuthenticationInProgressException();

        this.authStrategy.afterAttempt(otherRealm, null, null, null, authInProgressException);

        final NegotiateAuthenticationRealm realm = new NegotiateAuthenticationRealm();
        final Throwable exception = Assertions.assertThrows(AuthenticationInProgressException.class, () -> {
            this.authStrategy.afterAttempt(realm, null, null, null, authInProgressException);
        });
        Assertions.assertNull(exception.getMessage());
    }

}
