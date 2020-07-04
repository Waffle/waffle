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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    private ApplicationContext ctx;

    /**
     * Sets the up.
     */
    @BeforeEach
    public void setUp() {
        final String[] configFiles = new String[] { "springTestAuthBeans.xml" };
        this.ctx = new ClassPathXmlApplicationContext(configFiles);
        this.provider = (WindowsAuthenticationProvider) this.ctx.getBean("waffleSpringAuthenticationProvider");
    }

    /**
     * Shut down.
     */
    @AfterEach
    public void shutDown() {
        ((AbstractApplicationContext) this.ctx).close();
    }

    /**
     * Test windows authentication provider.
     */
    @Test
    public void testWindowsAuthenticationProvider() {
        Assertions.assertTrue(this.provider.isAllowGuestLogin());
        Assertions.assertTrue(this.provider.getAuthProvider() instanceof MockWindowsAuthProvider);
        Assertions.assertEquals(PrincipalFormat.SID, this.provider.getPrincipalFormat());
        Assertions.assertEquals(PrincipalFormat.BOTH, this.provider.getRoleFormat());
    }

    /**
     * Test supports.
     */
    @Test
    public void testSupports() {
        Assertions.assertFalse(this.provider.supports(this.getClass()));
        Assertions.assertTrue(this.provider.supports(UsernamePasswordAuthenticationToken.class));
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
        Assertions.assertNotNull(authenticated);
        Assertions.assertTrue(authenticated.isAuthenticated());
        final Collection<? extends GrantedAuthority> authorities = authenticated.getAuthorities();
        Assertions.assertEquals(3, authorities.size());

        final List<String> list = new ArrayList<>();
        for (final GrantedAuthority grantedAuthority : authorities) {
            list.add(grantedAuthority.getAuthority());
        }
        Collections.sort(list);
        Assertions.assertEquals("ROLE_EVERYONE", list.get(0));
        Assertions.assertEquals("ROLE_USER", list.get(1));
        Assertions.assertEquals("ROLE_USERS", list.get(2));
        Assertions.assertTrue(authenticated.getPrincipal() instanceof WindowsPrincipal);
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
        Assertions.assertNotNull(authenticated);
        Assertions.assertTrue(authenticated.isAuthenticated());
        final Collection<? extends GrantedAuthority> authorities = authenticated.getAuthorities();
        Assertions.assertEquals(2, authorities.size());

        final List<String> list = new ArrayList<>();
        for (final GrantedAuthority grantedAuthority : authorities) {
            list.add(grantedAuthority.getAuthority());
        }
        Collections.sort(list);
        Assertions.assertEquals("Everyone", list.get(0));
        Assertions.assertEquals("Users", list.get(1));
        Assertions.assertTrue(authenticated.getPrincipal() instanceof WindowsPrincipal);
    }

    /**
     * Test guest is disabled.
     */
    @Test
    public void testGuestIsDisabled() {
        final MockWindowsIdentity mockIdentity = new MockWindowsIdentity("Guest", new ArrayList<String>());
        this.provider.setAllowGuestLogin(false);
        final WindowsPrincipal principal = new WindowsPrincipal(mockIdentity);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                "password");
        final Throwable exception = Assertions.assertThrows(GuestLoginDisabledAuthenticationException.class, () -> {
            this.provider.authenticate(authentication);
        });
        Assertions.assertEquals("Guest", exception.getMessage());
    }

}
