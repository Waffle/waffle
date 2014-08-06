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
package waffle.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;

import waffle.mock.MockWindowsIdentity;
import waffle.servlet.WindowsPrincipal;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationTokenTests {

    private WindowsPrincipal           principal;
    private WindowsAuthenticationToken token;

    @Before
    public void setUp() {
        List<String> mockGroups = new ArrayList<String>();
        mockGroups.add("group1");
        mockGroups.add("group2");
        MockWindowsIdentity mockIdentity = new MockWindowsIdentity("localhost\\user1", mockGroups);
        this.principal = new WindowsPrincipal(mockIdentity);
        this.token = new WindowsAuthenticationToken(this.principal);
    }

    @Test
    public void testWindowsAuthenticationToken() {
        assertNull(this.token.getCredentials());
        assertNull(this.token.getDetails());
        assertTrue(this.token.isAuthenticated());
        assertEquals("localhost\\user1", this.token.getName());
        GrantedAuthority[] authorities = this.token.getAuthorities();
        assertEquals(3, authorities.length);
        assertEquals("ROLE_USER", authorities[0].getAuthority());
        assertEquals("ROLE_GROUP1", authorities[1].getAuthority());
        assertEquals("ROLE_GROUP2", authorities[2].getAuthority());
        assertEquals(this.principal, this.token.getPrincipal());
    }

    @Test
    public void testCustomGrantedAuthorityFactory() {

        WindowsAuthenticationToken myToken = new WindowsAuthenticationToken(this.principal,
                new FqnGrantedAuthorityFactory(null, false), null);

        assertNull(myToken.getCredentials());
        assertNull(myToken.getDetails());
        assertTrue(myToken.isAuthenticated());
        assertEquals("localhost\\user1", myToken.getName());
        GrantedAuthority[] authorities = myToken.getAuthorities();
        assertEquals(2, authorities.length);
        assertEquals("group1", authorities[0].getAuthority());
        assertEquals("group2", authorities[1].getAuthority());
        assertEquals(this.principal, myToken.getPrincipal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticated() {
        assertTrue(this.token.isAuthenticated());
        this.token.setAuthenticated(true);
    }
}
