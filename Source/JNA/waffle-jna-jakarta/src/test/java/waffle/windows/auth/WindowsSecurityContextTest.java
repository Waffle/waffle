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
package waffle.windows.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

/**
 * The Class WindowsSecurityContextTest.
 *
 * @author dblock[at]dblock[dot]org
 */
class WindowsSecurityContextTest {

    /**
     * Test negotiate.
     */
    @Test
    void testNegotiate() {
        final String securityPackage = "Negotiate";
        // security context
        final IWindowsSecurityContext ctx = WindowsSecurityContextImpl.getCurrent(securityPackage,
                WindowsAccountImpl.getCurrentUsername());
        Assertions.assertTrue(ctx.isContinue());
        Assertions.assertEquals(securityPackage, ctx.getSecurityPackage());
        assertThat(ctx.getToken()).isNotEmpty();
        ctx.dispose();
    }
}
