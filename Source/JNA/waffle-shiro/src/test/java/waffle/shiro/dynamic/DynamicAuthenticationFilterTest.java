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
package waffle.shiro.dynamic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

import mockit.Deencapsulation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * The Class DynamicAuthenticationFilterTest.
 *
 * @author Dan Rollo Date: 2/26/13 Time: 5:47 PM
 */
public class DynamicAuthenticationFilterTest {

    /**
     * The Class MockServletRequest.
     */
    private static abstract class MockServletRequest implements ServletRequest {

        /** The parameters. */
        private final Map<String, String> parameters = new HashMap<>();

        /*
         * (non-Javadoc)
         * 
         * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
         */
        @Override
        public String getParameter(final String name) {
            return this.parameters.get(name);
        }

    }

    /** The dynamic authentication filter. */
    private DynamicAuthenticationFilter dynamicAuthenticationFilter;

    /** The request. */
    private MockServletRequest          request;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        this.dynamicAuthenticationFilter = new DynamicAuthenticationFilter();

        this.request = Mockito.mock(MockServletRequest.class, Mockito.CALLS_REAL_METHODS);
        Deencapsulation.setField(this.request, new HashMap<String, String>());
    }

    /**
     * Test is auth type negotiate.
     */
    @Test
    public void testIsAuthTypeNegotiate() {
        Mockito.when(this.request.getParameter(Matchers.anyString())).thenReturn(null);
        Assert.assertFalse(this.dynamicAuthenticationFilter.isAuthTypeNegotiate(this.request));

        Mockito.when(this.request.getParameter(Matchers.anyString())).thenReturn("zzz");
        Assert.assertFalse(this.dynamicAuthenticationFilter.isAuthTypeNegotiate(this.request));

        Mockito.when(this.request.getParameter(Matchers.anyString())).thenReturn(
                DynamicAuthenticationFilter.PARAM_VAL_AUTHTYPE_NEGOTIATE);
        Assert.assertTrue(this.dynamicAuthenticationFilter.isAuthTypeNegotiate(this.request));
    }

}
