/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.shiro.dynamic;

import jakarta.servlet.ServletRequest;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class DynamicAuthenticationFilterTest.
 */
class DynamicAuthenticationFilterTest {

    /** The dynamic authentication filter. */
    @Tested
    private DynamicAuthenticationFilter dynamicAuthenticationFilter;

    /** The request. */
    @Mocked
    private ServletRequest request;

    /**
     * Test is auth type negotiate.
     */
    @Test
    void testIsAuthTypeNegotiate() {
        Assertions.assertNotNull(new Expectations() {
            {
                DynamicAuthenticationFilterTest.this.request.getParameter(this.anyString);
                this.result = null;
            }
        });
        Assertions.assertFalse(this.dynamicAuthenticationFilter.isAuthTypeNegotiate(this.request));

        Assertions.assertNotNull(new Expectations() {
            {
                DynamicAuthenticationFilterTest.this.request.getParameter(this.anyString);
                this.result = "zzz";
            }
        });
        Assertions.assertFalse(this.dynamicAuthenticationFilter.isAuthTypeNegotiate(this.request));

        Assertions.assertNotNull(new Expectations() {
            {
                DynamicAuthenticationFilterTest.this.request.getParameter(this.anyString);
                this.result = DynamicAuthenticationFilter.PARAM_VAL_AUTHTYPE_NEGOTIATE;
            }
        });
        Assertions.assertTrue(this.dynamicAuthenticationFilter.isAuthTypeNegotiate(this.request));
    }

}
