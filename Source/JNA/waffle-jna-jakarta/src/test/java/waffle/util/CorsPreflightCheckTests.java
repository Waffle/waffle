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
package waffle.util;

import jakarta.servlet.http.HttpServletRequest;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class CorsPreflightCheckTests.
 */
class CorsPreFlightCheckTests {

    /** The preflight request. */
    @Mocked
    HttpServletRequest preflightRequest;

    /** The no origin preflight request. */
    @Mocked
    HttpServletRequest noOriginPreflightRequest;

    /** The no cors method preflight request. */
    @Mocked
    HttpServletRequest noCorsMethodPreflightRequest;

    /** The no cors headers preflight header request. */
    @Mocked
    HttpServletRequest noCorsHeadersPreflightHeaderRequest;

    /**
     * Test expected cors preflight headers present.
     */
    @Test
    void testExpectedCorsPreflightHeadersPresent() {

        new Expectations() {
            {
                CorsPreFlightCheckTests.this.preflightRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreFlightCheckTests.this.preflightRequest.getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
                CorsPreFlightCheckTests.this.preflightRequest.getHeader("Access-Control-Request-Headers");
                this.result = "X-Request-For";
                CorsPreFlightCheckTests.this.preflightRequest.getHeader("Origin");
                this.result = "https://theorigin.localhost";
            }
        };

        Assertions.assertTrue(CorsPreFlightCheck.isPreflight(this.preflightRequest));

        new Verifications() {
            {
                CorsPreFlightCheckTests.this.preflightRequest.getMethod();
                this.times = 1;
                CorsPreFlightCheckTests.this.preflightRequest.getHeader("Access-Control-Request-Method");
                this.times = 1;
                CorsPreFlightCheckTests.this.preflightRequest.getHeader("Access-Control-Request-Headers");
                this.times = 1;
                CorsPreFlightCheckTests.this.preflightRequest.getHeader("Origin");
                this.times = 1;
            }
        };
    }

    /**
     * Test no cors preflight origin present.
     */
    @Test
    void testNoCorsPreflightOriginPresent() {
        new Expectations() {
            {
                CorsPreFlightCheckTests.this.noOriginPreflightRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreFlightCheckTests.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
                CorsPreFlightCheckTests.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Headers");
                this.result = "X-Request-For";
                /** Origin MUST be present with Method and Headers to be a valid CORS request **/
                CorsPreFlightCheckTests.this.noOriginPreflightRequest.getHeader("Origin");
                this.result = null;
            }
        };

        Assertions.assertFalse(CorsPreFlightCheck.isPreflight(this.noOriginPreflightRequest));

        new Verifications() {
            {
                CorsPreFlightCheckTests.this.noOriginPreflightRequest.getMethod();
                this.times = 1;
                CorsPreFlightCheckTests.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Method");
                this.times = 1;
                CorsPreFlightCheckTests.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Headers");
                this.times = 1;
                CorsPreFlightCheckTests.this.noOriginPreflightRequest.getHeader("Origin");
                this.times = 1;
            }
        };

    }

    /**
     * Test cors method preflight headers present.
     */
    @Test
    void testCorsMethodPreflightHeadersPresent() {
        new Expectations() {
            {
                CorsPreFlightCheckTests.this.noCorsMethodPreflightRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreFlightCheckTests.this.noCorsMethodPreflightRequest.getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
            }
        };

        Assertions.assertFalse(CorsPreFlightCheck.isPreflight(this.noCorsMethodPreflightRequest));

        new Verifications() {
            {
                CorsPreFlightCheckTests.this.noCorsMethodPreflightRequest.getMethod();
                this.times = 1;
                CorsPreFlightCheckTests.this.noCorsMethodPreflightRequest.getHeader("Access-Control-Request-Method");
                this.times = 1;
            }
        };

    }

    /**
     * Test no cors headers preflight header present.
     */
    @Test
    void testNoCorsHeadersPreflightHeaderPresent() {

        new Expectations() {
            {
                CorsPreFlightCheckTests.this.noCorsHeadersPreflightHeaderRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreFlightCheckTests.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
                CorsPreFlightCheckTests.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Headers");
                this.result = null;
                this.result = "https://theorigin.localhost";
            }
        };

        Assertions.assertFalse(CorsPreFlightCheck.isPreflight(this.noCorsHeadersPreflightHeaderRequest));

        new Verifications() {
            {
                CorsPreFlightCheckTests.this.noCorsHeadersPreflightHeaderRequest.getMethod();
                this.times = 1;
                CorsPreFlightCheckTests.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Method");
                this.times = 1;
                CorsPreFlightCheckTests.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Headers");
                this.times = 1;
            }
        };
    }

}
