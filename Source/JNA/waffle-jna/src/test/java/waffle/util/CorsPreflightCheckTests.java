/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
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
class CorsPreflightCheckTests {

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
                CorsPreflightCheckTests.this.preflightRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreflightCheckTests.this.preflightRequest.getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
                CorsPreflightCheckTests.this.preflightRequest.getHeader("Access-Control-Request-Headers");
                this.result = "X-Request-For";
                CorsPreflightCheckTests.this.preflightRequest.getHeader("Origin");
                this.result = "https://theorigin.localhost";
            }
        };

        Assertions.assertTrue(CorsPreflightCheck.isPreflight(this.preflightRequest));

        new Verifications() {
            {
                CorsPreflightCheckTests.this.preflightRequest.getMethod();
                this.times = 1;
                CorsPreflightCheckTests.this.preflightRequest.getHeader("Access-Control-Request-Method");
                this.times = 1;
                CorsPreflightCheckTests.this.preflightRequest.getHeader("Access-Control-Request-Headers");
                this.times = 1;
                CorsPreflightCheckTests.this.preflightRequest.getHeader("Origin");
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
                CorsPreflightCheckTests.this.noOriginPreflightRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreflightCheckTests.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
                CorsPreflightCheckTests.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Headers");
                this.result = "X-Request-For";
                /** Origin MUST be present with Method and Headers to be a valid CORS request **/
                CorsPreflightCheckTests.this.noOriginPreflightRequest.getHeader("Origin");
                this.result = null;
            }
        };

        Assertions.assertFalse(CorsPreflightCheck.isPreflight(this.noOriginPreflightRequest));

        new Verifications() {
            {
                CorsPreflightCheckTests.this.noOriginPreflightRequest.getMethod();
                this.times = 1;
                CorsPreflightCheckTests.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Method");
                this.times = 1;
                CorsPreflightCheckTests.this.noOriginPreflightRequest.getHeader("Access-Control-Request-Headers");
                this.times = 1;
                CorsPreflightCheckTests.this.noOriginPreflightRequest.getHeader("Origin");
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
                CorsPreflightCheckTests.this.noCorsMethodPreflightRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreflightCheckTests.this.noCorsMethodPreflightRequest.getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
            }
        };

        Assertions.assertFalse(CorsPreflightCheck.isPreflight(this.noCorsMethodPreflightRequest));

        new Verifications() {
            {
                CorsPreflightCheckTests.this.noCorsMethodPreflightRequest.getMethod();
                this.times = 1;
                CorsPreflightCheckTests.this.noCorsMethodPreflightRequest.getHeader("Access-Control-Request-Method");
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
                CorsPreflightCheckTests.this.noCorsHeadersPreflightHeaderRequest.getMethod();
                this.result = "OPTIONS";
                CorsPreflightCheckTests.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Method");
                this.result = "LOGIN";
                CorsPreflightCheckTests.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Headers");
                this.result = null;
                this.result = "https://theorigin.localhost";
            }
        };

        Assertions.assertFalse(CorsPreflightCheck.isPreflight(this.noCorsHeadersPreflightHeaderRequest));

        new Verifications() {
            {
                CorsPreflightCheckTests.this.noCorsHeadersPreflightHeaderRequest.getMethod();
                this.times = 1;
                CorsPreflightCheckTests.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Method");
                this.times = 1;
                CorsPreflightCheckTests.this.noCorsHeadersPreflightHeaderRequest
                        .getHeader("Access-Control-Request-Headers");
                this.times = 1;
            }
        };
    }

}
