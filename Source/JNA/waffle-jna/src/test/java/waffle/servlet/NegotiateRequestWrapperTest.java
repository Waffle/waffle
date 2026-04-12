/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import javax.servlet.http.HttpServletRequest;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

/**
 * Tests for {@link NegotiateRequestWrapper}.
 */
class NegotiateRequestWrapperTest {

    /** The Constant TEST_FQN. */
    private static final String TEST_FQN = "DOMAIN\\testuser";

    /** The windows identity. */
    @Mocked
    private IWindowsIdentity windowsIdentity;

    /** The request. */
    @Mocked
    private HttpServletRequest request;

    /** The wrapper. */
    private NegotiateRequestWrapper wrapper;

    /** The principal. */
    private WindowsPrincipal principal;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        Assertions.assertNotNull(new Expectations() {
            {
                NegotiateRequestWrapperTest.this.windowsIdentity.getFqn();
                this.result = NegotiateRequestWrapperTest.TEST_FQN;
                NegotiateRequestWrapperTest.this.windowsIdentity.getGroups();
                this.result = new IWindowsAccount[0];
            }
        });
        this.principal = new WindowsPrincipal(this.windowsIdentity, PrincipalFormat.FQN, PrincipalFormat.FQN);
        this.wrapper = new NegotiateRequestWrapper(this.request, this.principal);
    }

    /**
     * Test get user principal.
     */
    @Test
    void testGetUserPrincipal() {
        Assertions.assertEquals(this.principal, this.wrapper.getUserPrincipal());
    }

    /**
     * Test get auth type.
     */
    @Test
    void testGetAuthType() {
        Assertions.assertEquals("NEGOTIATE", this.wrapper.getAuthType());
    }

    /**
     * Test get remote user.
     */
    @Test
    void testGetRemoteUser() {
        Assertions.assertEquals(NegotiateRequestWrapperTest.TEST_FQN, this.wrapper.getRemoteUser());
    }

    /**
     * Test is user in role with valid role.
     */
    @Test
    void testIsUserInRoleValidRole() {
        Assertions.assertTrue(this.wrapper.isUserInRole(NegotiateRequestWrapperTest.TEST_FQN));
    }

    /**
     * Test is user in role with unknown role.
     */
    @Test
    void testIsUserInRoleUnknownRole() {
        Assertions.assertFalse(this.wrapper.isUserInRole("DOMAIN\\unknownrole"));
    }
}
