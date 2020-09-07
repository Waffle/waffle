/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
