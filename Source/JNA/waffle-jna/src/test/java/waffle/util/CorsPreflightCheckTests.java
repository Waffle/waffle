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

import static org.junit.jupiter.api.Assertions.assertFalse;

import javax.servlet.http.HttpServletRequest;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CorsPreflightCheckTests {

    @Mocked
    HttpServletRequest preflightRequest;
    @Mocked
    HttpServletRequest noOriginPreflightRequest;
    @Mocked
    HttpServletRequest noCorsMethodPreflightRequest;
    @Mocked
    HttpServletRequest noCorsHeadersPreflightHeaderRequest;

    @Test
    void testExpectedCorsPreflightHeadersPresent() {

        new Expectations() {
            {
                preflightRequest.getMethod();
                result = "OPTIONS";
                preflightRequest.getHeader("Access-Control-Request-Method");
                result = "LOGIN";
                preflightRequest.getHeader("Access-Control-Request-Headers");
                result = "X-Request-For";
                preflightRequest.getHeader("Origin");
                result = "https://theorigin.localhost";
            }
        };

        Assertions.assertTrue(CorsPreflightCheck.isPreflight(preflightRequest));

        new Verifications() {
            {
                preflightRequest.getMethod();
                times = 1;
                preflightRequest.getHeader("Access-Control-Request-Method");
                times = 1;
                preflightRequest.getHeader("Access-Control-Request-Headers");
                times = 1;
                preflightRequest.getHeader("Origin");
                times = 1;
            }
        };
    }

    @Test
    void testNoCorsPreflightOriginPresent() {
        new Expectations() {
            {
                noOriginPreflightRequest.getMethod();
                result = "OPTIONS";
                noOriginPreflightRequest.getHeader("Access-Control-Request-Method");
                result = "LOGIN";
                noOriginPreflightRequest.getHeader("Access-Control-Request-Headers");
                result = "X-Request-For";
                /** Origin MUST be present with Method and Headers to be a valid CORS request **/
                noOriginPreflightRequest.getHeader("Origin");
                result = null;
            }
        };

        assertFalse(CorsPreflightCheck.isPreflight(noOriginPreflightRequest));

        new Verifications() {
            {
                noOriginPreflightRequest.getMethod();
                times = 1;
                noOriginPreflightRequest.getHeader("Access-Control-Request-Method");
                times = 1;
                noOriginPreflightRequest.getHeader("Access-Control-Request-Headers");
                times = 1;
                noOriginPreflightRequest.getHeader("Origin");
                times = 1;
            }
        };

    }

    @Test
    void testCorsMethodPreflightHeadersPresent() {
        new Expectations() {
            {
                noCorsMethodPreflightRequest.getMethod();
                result = "OPTIONS";
                noCorsMethodPreflightRequest.getHeader("Access-Control-Request-Method");
                result = "LOGIN";
            }
        };

        assertFalse(CorsPreflightCheck.isPreflight(noCorsMethodPreflightRequest));

        new Verifications() {
            {
                noCorsMethodPreflightRequest.getMethod();
                times = 1;
                noCorsMethodPreflightRequest.getHeader("Access-Control-Request-Method");
                times = 1;
            }
        };

    }

    @Test
    void testNoCorsHeadersPreflightHeaderPresent() {

        new Expectations() {
            {
                noCorsHeadersPreflightHeaderRequest.getMethod();
                result = "OPTIONS";
                noCorsHeadersPreflightHeaderRequest.getHeader("Access-Control-Request-Method");
                result = "LOGIN";
                noCorsHeadersPreflightHeaderRequest.getHeader("Access-Control-Request-Headers");
                result = null;
                result = "https://theorigin.localhost";
            }
        };

        Assertions.assertFalse(CorsPreflightCheck.isPreflight(noCorsHeadersPreflightHeaderRequest));

        new Verifications() {
            {
                noCorsHeadersPreflightHeaderRequest.getMethod();
                times = 1;
                noCorsHeadersPreflightHeaderRequest.getHeader("Access-Control-Request-Method");
                times = 1;
                noCorsHeadersPreflightHeaderRequest.getHeader("Access-Control-Request-Headers");
                times = 1;
            }
        };
    }

}
