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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class CorsPreFlightHeaders {

    final private static List<String> EXPECTED_PRE_FLIGHT_HEADERS;
    static {
        EXPECTED_PRE_FLIGHT_HEADERS = new ArrayList<String>() {
            {
                add("Access-Control-Request-Method");
                add("Access-Control-Request-Headers");
                add("Origin");
            }
        };
    }

    public static boolean containsAllPreFlightHeaders(HttpServletRequest request) {
        for (String header : CorsPreFlightHeaders.EXPECTED_PRE_FLIGHT_HEADERS) {
            if (request.getHeader(header) == null)
                return false;
        }
        return true;
    }
}
