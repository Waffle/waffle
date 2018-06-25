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
public class CorsPreflightCheck {

    /** The logger. */
    private static final String preflightAttributeValue = "PRE_FLIGHT";
    private static final List<String> CORS_PRE_FLIGHT_HEADERS;
    static {
        CORS_PRE_FLIGHT_HEADERS = new ArrayList<String>() {
            {
                add("Access-Control-Request-Method");
                add("Access-Control-Request-Headers");
                add("Origin");
            }
        };
    }

    public static boolean isPreflight(final HttpServletRequest request) {

        final String corsRequestType = (String) request.getAttribute("cors.request.type");

        /**
         * it MUST be an OPTIONS Method to be a preflight Request
         */
        if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return false;
        }
        /**
         * support Apache CorsFilter which would already add the Attribute cors.request.type with a value "PRE_FLIGHT"
         **/
        if (corsRequestType != null && corsRequestType.equalsIgnoreCase(preflightAttributeValue)) {
            return true;
        } else {
            /*
             * it is OPTIONS and it is not an CorsFilter PRE_FLIGHT request make sure that the request contains all of
             * the CORS preflight Headers
             */
            for (String header : CorsPreflightCheck.CORS_PRE_FLIGHT_HEADERS) {
                if (request.getHeader(header) == null)
                    /* one of the CORS pre-flight headers is missing */
                    return false;
            }
            return true;
        }
    }
}
