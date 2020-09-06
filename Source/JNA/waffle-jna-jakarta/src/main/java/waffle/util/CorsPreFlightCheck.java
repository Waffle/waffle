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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CorsPrefFlightCheck.
 */
public final class CorsPreFlightCheck {

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CorsPreFlightCheck.class);

    /** The Constant preflightAttributeValue. */
    private static final String PRE_FLIGHT_ATTRIBUTE_VALUE = "PRE_FLIGHT";

    /** The Constant CORS_PRE_FLIGHT_HEADERS. */
    private static final List<String> CORS_PRE_FLIGHT_HEADERS = new ArrayList<String>() {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        {
            this.add("Access-Control-Request-Method");
            this.add("Access-Control-Request-Headers");
            this.add("Origin");
        }
    };

    /**
     * Prevent Instantiation.
     */
    private CorsPreFlightCheck() {
        // Do Nothing
    }

    /**
     * Checks if is preflight.
     *
     * @param request
     *            the request
     * @return true, if is preflight
     */
    public static boolean isPreflight(final HttpServletRequest request) {

        final String corsRequestType = (String) request.getAttribute("cors.request.type");

        CorsPreFlightCheck.LOGGER
                .debug("[waffle.util.CorsPreflightCheck] Request is CORS preflight; continue filter chain");

        // Method MUST be an OPTIONS Method to be a preflight Request
        final String method = request.getMethod();
        if (method == null || !method.equalsIgnoreCase("OPTIONS")) {
            return false;
        }

        CorsPreFlightCheck.LOGGER.debug("[waffle.util.CorsPreflightCheck] check for PRE_FLIGHT Attribute");

        /**
         * Support Apache CorsFilter which would already add the Attribute cors.request.type with a value "PRE_FLIGHT"
         **/
        if (corsRequestType != null
                && corsRequestType.equalsIgnoreCase(CorsPreFlightCheck.PRE_FLIGHT_ATTRIBUTE_VALUE)) {
            return true;
        } else {
            /*
             * it is OPTIONS and it is not an CorsFilter PRE_FLIGHT request make sure that the request contains all of
             * the CORS preflight Headers
             */
            CorsPreFlightCheck.LOGGER.debug("[waffle.util.CorsPreflightCheck] check headers");

            for (final String header : CorsPreFlightCheck.CORS_PRE_FLIGHT_HEADERS) {
                final String headerValue = request.getHeader(header);
                CorsPreFlightCheck.LOGGER.debug("[waffle.util.CorsPreflightCheck] {}", header);

                if (headerValue == null) {
                    /* one of the CORS pre-flight headers is missing */
                    return false;
                }
            }
            CorsPreFlightCheck.LOGGER.debug("[waffle.util.CorsPreflightCheck] is preflight");

            return true;
        }
    }
}
