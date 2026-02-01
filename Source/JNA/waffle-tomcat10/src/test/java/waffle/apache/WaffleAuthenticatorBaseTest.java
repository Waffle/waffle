/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.apache;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.catalina.connector.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * Waffle Authenticator Base Test.
 */
class WaffleAuthenticatorBaseTest {

    /** The waffle authenticator base. */
    private WaffleAuthenticatorBase waffleAuthenticatorBase;

    /**
     * Inits the.
     */
    @BeforeEach
    void init() {
        this.waffleAuthenticatorBase = new WaffleAuthenticatorBase() {
            {
                this.log = LoggerFactory.getLogger(WaffleAuthenticatorBaseTest.class);
            }

            @Override
            public boolean authenticate(final Request request, final HttpServletResponse response) throws IOException {
                return false;
            }

            @Override
            protected boolean doAuthenticate(final Request request, final HttpServletResponse response)
                    throws IOException {
                return false;
            }
        };
    }

    /**
     * Should_accept_both_protocols.
     */
    @Test
    void should_accept_both_protocols() {
        this.waffleAuthenticatorBase.setProtocols("  NTLM , , Negotiate   ");

        Assertions.assertEquals(2, this.waffleAuthenticatorBase.protocols.size(), "Two protocols added");
        Assertions.assertTrue(this.waffleAuthenticatorBase.protocols.contains("NTLM"), "NTLM has been added");
        Assertions.assertTrue(this.waffleAuthenticatorBase.protocols.contains("Negotiate"), "Negotiate has been added");
    }

    /**
     * Should_accept_ negotiate_protocol.
     */
    @Test
    void should_accept_Negotiate_protocol() {
        this.waffleAuthenticatorBase.setProtocols(" Negotiate  ");

        Assertions.assertEquals(1, this.waffleAuthenticatorBase.protocols.size(), "One protocol added");
        Assertions.assertEquals("Negotiate", this.waffleAuthenticatorBase.protocols.iterator().next());
    }

    /**
     * Should_accept_ ntl m_protocol.
     */
    @Test
    void should_accept_NTLM_protocol() {
        this.waffleAuthenticatorBase.setProtocols("  NTLM ");

        Assertions.assertEquals(1, this.waffleAuthenticatorBase.protocols.size(), "One protocol added");
        Assertions.assertEquals("NTLM", this.waffleAuthenticatorBase.protocols.iterator().next());
    }

    /**
     * Should_refuse_other_protocol.
     */
    @Test
    void should_refuse_other_protocol() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            this.waffleAuthenticatorBase.setProtocols("  NTLM , OTHER, Negotiate   ");
        });
    }

}
