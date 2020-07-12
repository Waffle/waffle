/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.apache;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.deploy.LoginConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * Waffle Authenticator Base Tests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WaffleAuthenticatorBaseTest {

    /** The waffle authenticator base. */
    private WaffleAuthenticatorBase waffleAuthenticatorBase;

    /**
     * Inits the.
     */
    @BeforeEach
    public void init() {
        this.waffleAuthenticatorBase = new WaffleAuthenticatorBase() {
            {
                this.log = LoggerFactory.getLogger(WaffleAuthenticatorBaseTest.class);
            }

            @Override
            public boolean authenticate(final Request request, final HttpServletResponse response,
                    final LoginConfig loginConfig) throws IOException {
                return false;
            }
        };
    }

    /**
     * Should_accept_both_protocols.
     */
    @Test
    public void should_accept_both_protocols() {
        this.waffleAuthenticatorBase.setProtocols("  NTLM , , Negotiate   ");

        Assertions.assertEquals(2, this.waffleAuthenticatorBase.protocols.size(), "Two protocols added");
        Assertions.assertTrue(this.waffleAuthenticatorBase.protocols.contains("NTLM"), "NTLM has been added");
        Assertions.assertTrue(this.waffleAuthenticatorBase.protocols.contains("Negotiate"), "Negotiate has been added");
    }

    /**
     * Should_accept_ negotiate_protocol.
     */
    @Test
    public void should_accept_Negotiate_protocol() {
        this.waffleAuthenticatorBase.setProtocols(" Negotiate  ");

        Assertions.assertEquals(1, this.waffleAuthenticatorBase.protocols.size(), "One protocol added");
        Assertions.assertEquals("Negotiate", this.waffleAuthenticatorBase.protocols.iterator().next());
    }

    /**
     * Should_accept_ ntl m_protocol.
     */
    @Test
    public void should_accept_NTLM_protocol() {
        this.waffleAuthenticatorBase.setProtocols("  NTLM ");

        Assertions.assertEquals(1, this.waffleAuthenticatorBase.protocols.size(), "One protocol added");
        Assertions.assertEquals("NTLM", this.waffleAuthenticatorBase.protocols.iterator().next());
    }

    /**
     * Should_refuse_other_protocol.
     */
    @Test
    public void should_refuse_other_protocol() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            this.waffleAuthenticatorBase.setProtocols("  NTLM , OTHER, Negotiate   ");
        });
    }

}