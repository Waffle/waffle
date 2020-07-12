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
package waffle.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import waffle.mock.MockWindowsIdentity;
import waffle.servlet.WindowsPrincipal;

/**
 * The Class WindowsAuthenticationTokenTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationTokenTests {

    /** The principal. */
    private WindowsPrincipal principal;

    /** The token. */
    private WindowsAuthenticationToken token;

    /**
     * Sets the up.
     */
    @BeforeEach
    public void setUp() {
        final List<String> mockGroups = new ArrayList<>();
        mockGroups.add("group1");
        mockGroups.add("group2");
        final MockWindowsIdentity mockIdentity = new MockWindowsIdentity("localhost\\user1", mockGroups);
        this.principal = new WindowsPrincipal(mockIdentity);
        this.token = new WindowsAuthenticationToken(this.principal);
    }

    /**
     * Test windows authentication token.
     */
    @Test
    public void testWindowsAuthenticationToken() {
        Assertions.assertNull(this.token.getCredentials());
        Assertions.assertNull(this.token.getDetails());
        Assertions.assertTrue(this.token.isAuthenticated());
        Assertions.assertEquals("localhost\\user1", this.token.getName());
        final Collection<GrantedAuthority> authorities = this.token.getAuthorities();
        Assertions.assertEquals(3, authorities.size());

        final List<String> list = new ArrayList<>();
        for (final GrantedAuthority grantedAuthority : authorities) {
            list.add(grantedAuthority.getAuthority());
        }
        Collections.sort(list);
        Assertions.assertEquals("ROLE_GROUP1", list.get(0));
        Assertions.assertEquals("ROLE_GROUP2", list.get(1));
        Assertions.assertEquals("ROLE_USER", list.get(2));
        Assertions.assertEquals(this.principal, this.token.getPrincipal());
    }

    /**
     * Test custom granted authority factory.
     */
    @Test
    public void testCustomGrantedAuthorityFactory() {

        final WindowsAuthenticationToken myToken = new WindowsAuthenticationToken(this.principal,
                new FqnGrantedAuthorityFactory(null, false), null);

        Assertions.assertNull(myToken.getCredentials());
        Assertions.assertNull(myToken.getDetails());
        Assertions.assertTrue(myToken.isAuthenticated());
        Assertions.assertEquals("localhost\\user1", myToken.getName());
        final Collection<GrantedAuthority> authorities = myToken.getAuthorities();
        Assertions.assertEquals(2, authorities.size());

        final List<String> list = new ArrayList<>();
        for (final GrantedAuthority grantedAuthority : authorities) {
            list.add(grantedAuthority.getAuthority());
        }
        Collections.sort(list);
        Assertions.assertEquals("group1", list.get(0));
        Assertions.assertEquals("group2", list.get(1));
        Assertions.assertEquals(this.principal, myToken.getPrincipal());
    }

    /**
     * Test authenticated.
     */
    @Test
    public void testAuthenticated() {
        Assertions.assertTrue(this.token.isAuthenticated());
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.token.setAuthenticated(true);
        });
    }

}
