/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.apache;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
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