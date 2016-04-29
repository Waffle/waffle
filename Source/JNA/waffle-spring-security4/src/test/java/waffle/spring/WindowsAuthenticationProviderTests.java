/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.spring;

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
 * The Class WindowsAuthenticationProviderTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationProviderTests {

    /** The provider. */
    private WindowsAuthenticationProvider provider;

    /** The ctx. */
    private ApplicationContext            ctx;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        final String[] configFiles = new String[] { "springTestAuthBeans.xml" };
        this.ctx = new ClassPathXmlApplicationContext(configFiles);
        this.provider = (WindowsAuthenticationProvider) this.ctx.getBean("waffleSpringAuthenticationProvider");
    }

    /**
     * Shut down.
     */
    @After
    public void shutDown() {
        ((AbstractApplicationContext) this.ctx).close();
    }

    /**
     * Test windows authentication provider.
     */
    @Test
    public void testWindowsAuthenticationProvider() {
        Assert.assertTrue(this.provider.isAllowGuestLogin());
        Assert.assertTrue(this.provider.getAuthProvider() instanceof MockWindowsAuthProvider);
        Assert.assertEquals(PrincipalFormat.SID, this.provider.getPrincipalFormat());
        Assert.assertEquals(PrincipalFormat.BOTH, this.provider.getRoleFormat());
    }

    /**
     * Test supports.
     */
    @Test
    public void testSupports() {
        Assert.assertFalse(this.provider.supports(this.getClass()));
        Assert.assertTrue(this.provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    /**
     * Test authenticate.
     */
    @Test
    public void testAuthenticate() {
        final MockWindowsIdentity mockIdentity = new MockWindowsIdentity(WindowsAccountImpl.getCurrentUsername(),
                new ArrayList<String>());
        final WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                "password");
        final Authentication authenticated = this.provider.authenticate(authentication);
        Assert.assertNotNull(authenticated);
        Assert.assertTrue(authenticated.isAuthenticated());
        final Collection<? extends GrantedAuthority> authorities = authenticated.getAuthorities();
        final Iterator<? extends GrantedAuthority> authoritiesIterator = authorities.iterator();
        Assert.assertEquals(3, authorities.size());

        final List<String> list = new ArrayList<>();
        while (authoritiesIterator.hasNext()) {
            list.add(authoritiesIterator.next().getAuthority());
        }
        Collections.sort(list);
        Assert.assertEquals("ROLE_EVERYONE", list.get(0));
        Assert.assertEquals("ROLE_USER", list.get(1));
        Assert.assertEquals("ROLE_USERS", list.get(2));
        Assert.assertTrue(authenticated.getPrincipal() instanceof WindowsPrincipal);
    }

    /**
     * Test authenticate with custom granted authority factory.
     */
    @Test
    public void testAuthenticateWithCustomGrantedAuthorityFactory() {
        this.provider.setDefaultGrantedAuthority(null);
        this.provider.setGrantedAuthorityFactory(new FqnGrantedAuthorityFactory(null, false));

        final MockWindowsIdentity mockIdentity = new MockWindowsIdentity(WindowsAccountImpl.getCurrentUsername(),
                new ArrayList<String>());
        final WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                "password");

        final Authentication authenticated = this.provider.authenticate(authentication);
        Assert.assertNotNull(authenticated);
        Assert.assertTrue(authenticated.isAuthenticated());
        final Collection<? extends GrantedAuthority> authorities = authenticated.getAuthorities();
        final Iterator<? extends GrantedAuthority> authoritiesIterator = authorities.iterator();
        Assert.assertEquals(2, authorities.size());

        final List<String> list = new ArrayList<>();
        while (authoritiesIterator.hasNext()) {
            list.add(authoritiesIterator.next().getAuthority());
        }
        Collections.sort(list);
        Assert.assertEquals("Everyone", list.get(0));
        Assert.assertEquals("Users", list.get(1));
        Assert.assertTrue(authenticated.getPrincipal() instanceof WindowsPrincipal);
    }

    /**
     * Test guest is disabled.
     */
    @Test(expected = GuestLoginDisabledAuthenticationException.class)
    public void testGuestIsDisabled() {
        final MockWindowsIdentity mockIdentity = new MockWindowsIdentity("Guest", new ArrayList<String>());
        this.provider.setAllowGuestLogin(false);
        final WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                "password");
        this.provider.authenticate(authentication);
        Assert.fail("expected AuthenticationServiceException");
    }
}
