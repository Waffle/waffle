/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util;

import javax.servlet.http.HttpServletRequest;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class CorsPreflightCheckTest.
 */
class CorsPreFlightCheckTest {

    /** The preflight request. */
    @Mocked
    private HttpServletRequest preflightRequest;

    /** The no origin preflight request. */
    @Mocked
    private HttpServletRequest noOriginPreflightRequest;

    /** The no cors method preflight request. */
    @Mocked
    private HttpServletRequest noCorsMethodPreflightRequest;

    /** The no cors headers preflight header request. */
    @Mocked
    private HttpServletRequest noCorsHeadersPreflightHeaderRequest;

    /**
     * Test expected cors preflight headers present.
     */
    @Test
    void testExpectedCorsPreflightHeadersPresent() {

        new Expectations() {
            {
                CorsPreFlightCheckTest.this.preflightRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreFlightCheckTest.this.preflightRequest.getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
                CorsPreFlightCheckTest.this.preflightRequest.getHeader("Access-Control-Request-Headers");
                this.result = "X-Request-For";
                CorsPreFlightCheckTest.this.preflightRequest.getHeader("Origin");
                this.result = "https://theorigin.localhost";
            }
        };

        Assertions.assertTrue(CorsPreFlightCheck.isPreflight(this.preflightRequest));

        new Verifications() {
            {
                CorsPreFlightCheckTest.this.preflightRequest.getMethod();
                this.times = 1;
                CorsPreFlightCheckTest.this.preflightRequest.getHeader("Access-Control-Request-Method");
                this.times = 1;
                CorsPreFlightCheckTest.this.preflightRequest.getHeader("Access-Control-Request-Headers");
                this.times = 1;
                CorsPreFlightCheckTest.this.preflightRequest.getHeader("Origin");
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
                CorsPreFlightCheckTest.this.noOriginPreflightRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreFlightCheckTest.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
                CorsPreFlightCheckTest.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Headers");
                this.result = "X-Request-For";
                // Origin MUST be present with Method and Headers to be a valid CORS request
                CorsPreFlightCheckTest.this.noOriginPreflightRequest.getHeader("Origin");
                this.result = null;
            }
        };

        Assertions.assertFalse(CorsPreFlightCheck.isPreflight(this.noOriginPreflightRequest));

        new Verifications() {
            {
                CorsPreFlightCheckTest.this.noOriginPreflightRequest.getMethod();
                this.times = 1;
                CorsPreFlightCheckTest.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Method");
                this.times = 1;
                CorsPreFlightCheckTest.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Headers");
                this.times = 1;
                CorsPreFlightCheckTest.this.noOriginPreflightRequest.getHeader("Origin");
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
                CorsPreFlightCheckTest.this.noCorsMethodPreflightRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreFlightCheckTest.this.noCorsMethodPreflightRequest.getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
            }
        };

        Assertions.assertFalse(CorsPreFlightCheck.isPreflight(this.noCorsMethodPreflightRequest));

        new Verifications() {
            {
                CorsPreFlightCheckTest.this.noCorsMethodPreflightRequest.getMethod();
                this.times = 1;
                CorsPreFlightCheckTest.this.noCorsMethodPreflightRequest.getHeader("Access-Control-Request-Method");
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
                CorsPreFlightCheckTest.this.noCorsHeadersPreflightHeaderRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreFlightCheckTest.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
                CorsPreFlightCheckTest.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Headers");
                this.result = null;
                this.result = "https://theorigin.localhost";
            }
        };

        Assertions.assertFalse(CorsPreFlightCheck.isPreflight(this.noCorsHeadersPreflightHeaderRequest));

        new Verifications() {
            {
                CorsPreFlightCheckTest.this.noCorsHeadersPreflightHeaderRequest.getMethod();
                this.times = 1;
                CorsPreFlightCheckTest.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Method");
                this.times = 1;
                CorsPreFlightCheckTest.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Headers");
                this.times = 1;
            }
        };
    }

}
