/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
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

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthorityImpl;

import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.WindowsAccount;

public class FqnGrantedAuthorityFactoryTests {

    private WindowsAccount group;

    @Before
    public void setUp() {
        this.group = new WindowsAccount(new MockWindowsAccount("group"));
    }

    @Test
    public void testPrefixAndUppercase() {
        FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", true);
        assertEquals(new GrantedAuthorityImpl("PREFIX_GROUP"), factory.createGrantedAuthority(this.group));
    }

    @Test
    public void testPrefixAndLowercase() {
        FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", false);
        assertEquals(new GrantedAuthorityImpl("prefix_group"), factory.createGrantedAuthority(this.group));
    }

    @Test
    public void testNoPrefixAndUppercase() {
        FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, true);
        assertEquals(new GrantedAuthorityImpl("GROUP"), factory.createGrantedAuthority(this.group));
    }

    @Test
    public void testNoPrefixAndLowercase() {
        FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, false);
        assertEquals(new GrantedAuthorityImpl("group"), factory.createGrantedAuthority(this.group));
    }

}
