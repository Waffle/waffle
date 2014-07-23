/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
package waffle.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

import waffle.mock.MockWindowsAuthProvider;
import waffle.mock.MockWindowsIdentity;
import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationProviderTests {

    private WindowsAuthenticationProvider _provider;
    private ApplicationContext            ctx;

    @Before
    public void setUp() {
        String[] configFiles = new String[] { "springTestAuthBeans.xml" };
        ctx = new ClassPathXmlApplicationContext(configFiles);
        _provider = (WindowsAuthenticationProvider) ctx.getBean("waffleSpringAuthenticationProvider");
    }

    @After
    public void shutDown() {
        ((AbstractApplicationContext) ctx).close();
    }

    @Test
    public void testWindowsAuthenticationProvider() {
        assertTrue(_provider.isAllowGuestLogin());
        assertTrue(_provider.getAuthProvider() instanceof MockWindowsAuthProvider);
        assertEquals(PrincipalFormat.sid, _provider.getPrincipalFormat());
        assertEquals(PrincipalFormat.both, _provider.getRoleFormat());
    }

    @Test
    public void testSupports() {
        assertFalse(_provider.supports(this.getClass()));
        assertTrue(_provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testAuthenticate() {
        MockWindowsIdentity mockIdentity = new MockWindowsIdentity(WindowsAccountImpl.getCurrentUsername(),
                new ArrayList<String>());
        WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                "password");
        Authentication authenticated = _provider.authenticate(authentication);
        assertNotNull(authenticated);
        assertTrue(authenticated.isAuthenticated());
        GrantedAuthority[] authorities = authenticated.getAuthorities();
        assertEquals(3, authorities.length);
        assertEquals("ROLE_USER", authorities[0].getAuthority());
        assertEquals("ROLE_USERS", authorities[1].getAuthority());
        assertEquals("ROLE_EVERYONE", authorities[2].getAuthority());
        assertTrue(authenticated.getPrincipal() instanceof WindowsPrincipal);
    }

    @Test
    public void testAuthenticateWithCustomGrantedAuthorityFactory() {
        _provider.setDefaultGrantedAuthority(null);
        _provider.setGrantedAuthorityFactory(new FqnGrantedAuthorityFactory(null, false));

        MockWindowsIdentity mockIdentity = new MockWindowsIdentity(WindowsAccountImpl.getCurrentUsername(),
                new ArrayList<String>());
        WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                "password");

        Authentication authenticated = _provider.authenticate(authentication);
        assertNotNull(authenticated);
        assertTrue(authenticated.isAuthenticated());
        GrantedAuthority[] authorities = authenticated.getAuthorities();
        assertEquals(2, authorities.length);
        assertEquals("Users", authorities[0].getAuthority());
        assertEquals("Everyone", authorities[1].getAuthority());
        assertTrue(authenticated.getPrincipal() instanceof WindowsPrincipal);
    }

    @Test(expected = GuestLoginDisabledAuthenticationException.class)
    public void testGuestIsDisabled() {
        MockWindowsIdentity mockIdentity = new MockWindowsIdentity("Guest", new ArrayList<String>());
        _provider.setAllowGuestLogin(false);
        WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                "password");
        _provider.authenticate(authentication);
        fail("expected AuthenticationServiceException");
    }
}
