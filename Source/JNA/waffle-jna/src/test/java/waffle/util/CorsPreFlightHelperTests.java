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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import mockit.Tested;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CorsPreFlightHelperTests {

    @Test
    void testExpectedCorsPreFlightHeadersPresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeader("Access-Control-Request-Method")).thenReturn("LOGIN");
        when(request.getHeader("Access-Control-Request-Headers")).thenReturn("x-request-for");
        when(request.getHeader("Origin")).thenReturn("https://theorigin.localhost");
        System.out.println(String.format("%s: %s", "Access-Control-Request-Method",
                request.getHeader("Access-Control-Request-Method")));

        System.out.println(String.format("%s: %s", "Access-Control-Request-Headers",
                request.getHeader("Access-Control-Request-Headers")));
        System.out.println(String.format("%s: %s", "Origin", request.getHeader("Origin")));
        System.out.println(CorsPreFlightHelper.isPreFlight(request));
        Assertions.assertTrue(CorsPreFlightHelper.isPreFlight(request));
    }

    @Test
    void testExpectedCorsPreFlightHeadersIncomplete() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeader("Access-Control-Request-Method")).thenReturn("LOGIN");
        when(request.getHeader("Access-Control-Request-Headers")).thenReturn(null);
        when(request.getHeader("Origin")).thenReturn(null);
        System.out.println(String.format("%s: %s", "Access-Control-Request-Method",
                request.getHeader("Access-Control-Request-Method")));

        System.out.println(String.format("%s: %s", "Access-Control-Request-Headers",
                request.getHeader("Access-Control-Request-Headers")));
        System.out.println(String.format("%s: %s", "Origin", request.getHeader("Origin")));
        System.out.println(CorsPreFlightHelper.isPreFlight(request));
        assertFalse(CorsPreFlightHelper.isPreFlight(request));
    }
}
