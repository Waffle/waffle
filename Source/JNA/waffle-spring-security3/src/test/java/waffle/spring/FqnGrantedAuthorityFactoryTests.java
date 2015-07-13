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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", true);
        Assert.assertEquals(new SimpleGrantedAuthority("PREFIX_GROUP"), factory.createGrantedAuthority(this.group));
    }

    @Test
    public void testPrefixAndLowercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", false);
        Assert.assertEquals(new SimpleGrantedAuthority("prefix_group"), factory.createGrantedAuthority(this.group));
    }

    @Test
    public void testNoPrefixAndUppercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, true);
        Assert.assertEquals(new SimpleGrantedAuthority("GROUP"), factory.createGrantedAuthority(this.group));
    }

    @Test
    public void testNoPrefixAndLowercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, false);
        Assert.assertEquals(new SimpleGrantedAuthority("group"), factory.createGrantedAuthority(this.group));
    }

}
