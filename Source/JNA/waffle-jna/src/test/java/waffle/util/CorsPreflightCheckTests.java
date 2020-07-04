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
package waffle.util;

import javax.servlet.http.HttpServletRequest;

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
