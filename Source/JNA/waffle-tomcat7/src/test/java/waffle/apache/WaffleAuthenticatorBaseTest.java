/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.apache;

import org.apache.catalina.connector.Request;
import org.apache.catalina.deploy.LoginConfig;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WaffleAuthenticatorBaseTest {

    private WaffleAuthenticatorBase waffleAuthenticatorBase;

    @Before
    public void init() {
        this.waffleAuthenticatorBase = new WaffleAuthenticatorBase() {
            {
                this.log = LoggerFactory.getLogger(WaffleAuthenticatorBaseTest.class);
            }

            @Override
            public boolean authenticate(Request request, HttpServletResponse httpServletResponse,
                    LoginConfig loginConfig) throws IOException {
                return false;
            }
        };
    }

    @Test
    public void should_accept_NTLM_protocol() throws Exception {
        this.waffleAuthenticatorBase.setProtocols("  NTLM ");

        assertEquals("One protocol added", 1, this.waffleAuthenticatorBase.protocols.size());
        assertEquals("NTLM", this.waffleAuthenticatorBase.protocols.iterator().next());
    }

    @Test
    public void should_accept_Negotiate_protocol() throws Exception {
        this.waffleAuthenticatorBase.setProtocols(" Negotiate  ");

        assertEquals("One protocol added", 1, this.waffleAuthenticatorBase.protocols.size());
        assertEquals("Negotiate", this.waffleAuthenticatorBase.protocols.iterator().next());
    }

    @Test
    public void should_accept_both_protocols() throws Exception {
        this.waffleAuthenticatorBase.setProtocols("  NTLM , , Negotiate   ");

        assertEquals("Two protocols added", 2, this.waffleAuthenticatorBase.protocols.size());
        assertTrue("NTLM has been added", this.waffleAuthenticatorBase.protocols.contains("NTLM"));
        assertTrue("Negotiate has been added", this.waffleAuthenticatorBase.protocols.contains("Negotiate"));
    }

    @Test(expected = RuntimeException.class)
    public void should_refuse_other_protocol() throws Exception {
        this.waffleAuthenticatorBase.setProtocols("  NTLM , OTHER, Negotiate   ");
    }
}