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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class CorsPreflightCheck {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CorsPreflightCheck.class);

    private static final String preflightAttributeValue = "PRE_FLIGHT";
    private static final List<String> CORS_PRE_FLIGHT_HEADERS = new ArrayList<String>() {
        {
            add("Access-Control-Request-Method");
            add("Access-Control-Request-Headers");
            add("Origin");
        }
    };

    public static boolean isPreflight(final HttpServletRequest request) {

        final String corsRequestType = (String) request.getAttribute("cors.request.type");

        CorsPreflightCheck.LOGGER
                .debug("[waffle.servlet.CorsPreflightCheck] Request is CORS preflight; continue filter chain");

        /**
         * it MUST be an OPTIONS Method to be a preflight Request
         */
        String method = request.getMethod();
        if (method == null || !method.equalsIgnoreCase("OPTIONS")) {
            return false;
        }

        CorsPreflightCheck.LOGGER.debug("[waffle.servlet.CorsPreflightCheck] check for PRE_FLIGHT Attribute");

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
            CorsPreflightCheck.LOGGER.debug("[waffle.servlet.CorsPreflightCheck] check headers");

            for (String header : CorsPreflightCheck.CORS_PRE_FLIGHT_HEADERS) {
                String headerValue = request.getHeader(header);
                CorsPreflightCheck.LOGGER.debug("[waffle.servlet.CorsPreflightCheck] {} {} ", header);

                if (headerValue == null)
                    /* one of the CORS pre-flight headers is missing */
                    return false;
            }
            CorsPreflightCheck.LOGGER.debug("[waffle.servlet.CorsPreflightCheck] is preflight");

            return true;
        }
    }
}
