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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import waffle.mock.MockWindowsAuthProvider;
import waffle.mock.MockWindowsIdentity;
import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationProviderTests {

    private WindowsAuthenticationProvider provider;
    private ApplicationContext            ctx;

    @Before
    public void setUp() {
        String[] configFiles = new String[] { "springTestAuthBeans.xml" };
        this.ctx = new ClassPathXmlApplicationContext(configFiles);
        this.provider = (WindowsAuthenticationProvider) this.ctx.getBean("waffleSpringAuthenticationProvider");
    }

    @After
    public void shutDown() {
        ((AbstractApplicationContext) this.ctx).close();
    }

    @Test
    public void testWindowsAuthenticationProvider() {
        Assert.assertTrue(this.provider.isAllowGuestLogin());
        Assert.assertTrue(this.provider.getAuthProvider() instanceof MockWindowsAuthProvider);
        Assert.assertEquals(PrincipalFormat.SID, this.provider.getPrincipalFormat());
        Assert.assertEquals(PrincipalFormat.BOTH, this.provider.getRoleFormat());
    }

    @Test
    public void testSupports() {
        Assert.assertFalse(this.provider.supports(this.getClass()));
        Assert.assertTrue(this.provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testAuthenticate() {
        MockWindowsIdentity mockIdentity = new MockWindowsIdentity(WindowsAccountImpl.getCurrentUsername(),
                new ArrayList<String>());
        WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                "password");
        Authentication authenticated = this.provider.authenticate(authentication);
        Assert.assertNotNull(authenticated);
        Assert.assertTrue(authenticated.isAuthenticated());
        Collection<? extends GrantedAuthority> authorities = authenticated.getAuthorities();
        Iterator<? extends GrantedAuthority> authoritiesIterator = authorities.iterator();
        Assert.assertEquals(3, authorities.size());

        final List<String> list = new ArrayList<String>();
        while (authoritiesIterator.hasNext()) {
            list.add(authoritiesIterator.next().getAuthority());
        }
        Collections.sort(list);
        Assert.assertEquals("ROLE_EVERYONE", list.get(0));
        Assert.assertEquals("ROLE_USER", list.get(1));
        Assert.assertEquals("ROLE_USERS", list.get(2));
        Assert.assertTrue(authenticated.getPrincipal() instanceof WindowsPrincipal);
    }

    @Test
    public void testAuthenticateWithCustomGrantedAuthorityFactory() {
        this.provider.setDefaultGrantedAuthority(null);
        this.provider.setGrantedAuthorityFactory(new FqnGrantedAuthorityFactory(null, false));

        MockWindowsIdentity mockIdentity = new MockWindowsIdentity(WindowsAccountImpl.getCurrentUsername(),
                new ArrayList<String>());
        WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                "password");

        Authentication authenticated = this.provider.authenticate(authentication);
        Assert.assertNotNull(authenticated);
        Assert.assertTrue(authenticated.isAuthenticated());
        Collection<? extends GrantedAuthority> authorities = authenticated.getAuthorities();
        Iterator<? extends GrantedAuthority> authoritiesIterator = authorities.iterator();
        Assert.assertEquals(2, authorities.size());

        final List<String> list = new ArrayList<String>();
        while (authoritiesIterator.hasNext()) {
            list.add(authoritiesIterator.next().getAuthority());
        }
        Collections.sort(list);
        Assert.assertEquals("Everyone", list.get(0));
        Assert.assertEquals("Users", list.get(1));
        Assert.assertTrue(authenticated.getPrincipal() instanceof WindowsPrincipal);
    }

    @Test(expected = GuestLoginDisabledAuthenticationException.class)
    public void testGuestIsDisabled() {
        MockWindowsIdentity mockIdentity = new MockWindowsIdentity("Guest", new ArrayList<String>());
        this.provider.setAllowGuestLogin(false);
        WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                "password");
        this.provider.authenticate(authentication);
        Assert.fail("expected AuthenticationServiceException");
    }
}
