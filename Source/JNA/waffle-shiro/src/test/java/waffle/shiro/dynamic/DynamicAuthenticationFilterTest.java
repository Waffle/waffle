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
package waffle.shiro.dynamic;

import javax.servlet.ServletRequest;

import org.junit.Assert;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;

/**
 * The Class DynamicAuthenticationFilterTest.
 */
public class DynamicAuthenticationFilterTest {

    /** The dynamic authentication filter. */
    @Tested
    DynamicAuthenticationFilter dynamicAuthenticationFilter;

    /** The request. */
    @Mocked
    ServletRequest              request;

    /**
     * Test is auth type negotiate.
     */
    @Test
    public void testIsAuthTypeNegotiate() {
        Assert.assertNotNull(new Expectations() {
            {
                DynamicAuthenticationFilterTest.this.request.getParameter(this.anyString);
                this.result = null;
            }
        });
        Assert.assertFalse(this.dynamicAuthenticationFilter.isAuthTypeNegotiate(this.request));

        Assert.assertNotNull(new Expectations() {
            {
                DynamicAuthenticationFilterTest.this.request.getParameter(this.anyString);
                this.result = "zzz";
            }
        });
        Assert.assertFalse(this.dynamicAuthenticationFilter.isAuthTypeNegotiate(this.request));

        Assert.assertNotNull(new Expectations() {
            {
                DynamicAuthenticationFilterTest.this.request.getParameter(this.anyString);
                this.result = DynamicAuthenticationFilter.PARAM_VAL_AUTHTYPE_NEGOTIATE;
            }
        });
        Assert.assertTrue(this.dynamicAuthenticationFilter.isAuthTypeNegotiate(this.request));
    }

}
