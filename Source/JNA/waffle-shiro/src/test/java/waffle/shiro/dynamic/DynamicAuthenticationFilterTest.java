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
package waffle.shiro.dynamic;

import javax.servlet.ServletRequest;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class DynamicAuthenticationFilterTest.
 */
public class DynamicAuthenticationFilterTest {

    /** The dynamic authentication filter. */
    @Tested
    DynamicAuthenticationFilter dynamicAuthenticationFilter;

    /** The request. */
    @Mocked
    ServletRequest request;

    /**
     * Test is auth type negotiate.
     */
    @Test
    public void testIsAuthTypeNegotiate() {
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
