/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.spring.boot;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link WaffleProperties}.
 */
class WafflePropertiesTest {

    /** The properties. */
    private WaffleProperties properties;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.properties = new WaffleProperties();
    }

    /**
     * Test default principal format.
     */
    @Test
    void testDefaultPrincipalFormat() {
        Assertions.assertEquals("fqn", this.properties.getPrincipalFormat());
    }

    /**
     * Test default role format.
     */
    @Test
    void testDefaultRoleFormat() {
        Assertions.assertEquals("fqn", this.properties.getRoleFormat());
    }

    /**
     * Test default allow guest login.
     */
    @Test
    void testDefaultAllowGuestLogin() {
        Assertions.assertFalse(this.properties.isAllowGuestLogin());
    }

    /**
     * Test default sso null.
     */
    @Test
    void testDefaultSsoNull() {
        Assertions.assertNull(this.properties.getSso());
    }

    /**
     * Test set principal format.
     */
    @Test
    void testSetPrincipalFormat() {
        this.properties.setPrincipalFormat("sid");
        Assertions.assertEquals("sid", this.properties.getPrincipalFormat());
    }

    /**
     * Test set role format.
     */
    @Test
    void testSetRoleFormat() {
        this.properties.setRoleFormat("both");
        Assertions.assertEquals("both", this.properties.getRoleFormat());
    }

    /**
     * Test set allow guest login.
     */
    @Test
    void testSetAllowGuestLogin() {
        this.properties.setAllowGuestLogin(true);
        Assertions.assertTrue(this.properties.isAllowGuestLogin());
    }

    /**
     * Test set sso.
     */
    @Test
    void testSetSso() {
        final WaffleProperties.SingleSignOnProperties sso = new WaffleProperties.SingleSignOnProperties();
        this.properties.setSso(sso);
        Assertions.assertNotNull(this.properties.getSso());
    }

    /**
     * Test sso default enabled false.
     */
    @Test
    void testSsoDefaultEnabledFalse() {
        final WaffleProperties.SingleSignOnProperties sso = new WaffleProperties.SingleSignOnProperties();
        Assertions.assertFalse(sso.isEnabled());
    }

    /**
     * Test sso default basic enabled false.
     */
    @Test
    void testSsoDefaultBasicEnabledFalse() {
        final WaffleProperties.SingleSignOnProperties sso = new WaffleProperties.SingleSignOnProperties();
        Assertions.assertFalse(sso.isBasicEnabled());
    }

    /**
     * Test sso default impersonate false.
     */
    @Test
    void testSsoDefaultImpersonateFalse() {
        final WaffleProperties.SingleSignOnProperties sso = new WaffleProperties.SingleSignOnProperties();
        Assertions.assertFalse(sso.isImpersonate());
    }

    /**
     * Test sso default protocols.
     */
    @Test
    void testSsoDefaultProtocols() {
        final WaffleProperties.SingleSignOnProperties sso = new WaffleProperties.SingleSignOnProperties();
        final List<String> protocols = sso.getProtocols();
        Assertions.assertNotNull(protocols);
        Assertions.assertEquals(2, protocols.size());
        Assertions.assertTrue(protocols.contains("Negotiate"));
        Assertions.assertTrue(protocols.contains("NTLM"));
    }

    /**
     * Test sso set enabled.
     */
    @Test
    void testSsoSetEnabled() {
        final WaffleProperties.SingleSignOnProperties sso = new WaffleProperties.SingleSignOnProperties();
        sso.setEnabled(true);
        Assertions.assertTrue(sso.isEnabled());
    }

    /**
     * Test sso set basic enabled.
     */
    @Test
    void testSsoSetBasicEnabled() {
        final WaffleProperties.SingleSignOnProperties sso = new WaffleProperties.SingleSignOnProperties();
        sso.setBasicEnabled(true);
        Assertions.assertTrue(sso.isBasicEnabled());
    }

    /**
     * Test sso set impersonate.
     */
    @Test
    void testSsoSetImpersonate() {
        final WaffleProperties.SingleSignOnProperties sso = new WaffleProperties.SingleSignOnProperties();
        sso.setImpersonate(true);
        Assertions.assertTrue(sso.isImpersonate());
    }

    /**
     * Test sso set protocols.
     */
    @Test
    void testSsoSetProtocols() {
        final WaffleProperties.SingleSignOnProperties sso = new WaffleProperties.SingleSignOnProperties();
        sso.setProtocols(Arrays.asList("Negotiate"));
        Assertions.assertEquals(1, sso.getProtocols().size());
        Assertions.assertTrue(sso.getProtocols().contains("Negotiate"));
    }
}
