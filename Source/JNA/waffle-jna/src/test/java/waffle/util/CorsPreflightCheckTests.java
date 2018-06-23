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
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CorsPreflightCheckTests {

    @Mocked
    HttpServletRequest request;

    @Test
    void testExpectedCorsPreflightHeadersPresent() {

        new Expectations(){{
            request.getMethod(); result = "OPTIONS";
            request.getHeader("Access-Control-Request-Method"); result = "LOGIN";
            request.getHeader("Access-Control-Request-Headers"); result = "X-Request-For";
            request.getHeader("Origin"); result = "https://theorigin.localhost";
        }};

        Assertions.assertTrue(CorsPreflightCheck.isPreflight(request));

        new Verifications(){{
            request.getMethod();times = 1;
            request.getHeader("Access-Control-Request-Method"); times = 1;
            request.getHeader("Access-Control-Request-Headers"); times = 1;
            request.getHeader("Origin"); times = 1;
        }};
    }

    @Test
    void testExpectedCorsPreflightHeadersIncomplete() {
        new Expectations(){{
            request.getMethod(); result = "OPTIONS";
            request.getHeader("Access-Control-Request-Method"); result = "LOGIN";
            request.getHeader("Access-Control-Request-Headers"); result = "X-Requested-For";
            /** Origin MUST be present with Method and Headers to be a valid CORS request **/
            request.getHeader("Origin"); result = null;
        }};

        assertFalse(CorsPreflightCheck.isPreflight(request));

        new Verifications(){{
            request.getMethod(); times = 1;
            request.getHeader("Origin"); times = 1;
        }};


    }
}

