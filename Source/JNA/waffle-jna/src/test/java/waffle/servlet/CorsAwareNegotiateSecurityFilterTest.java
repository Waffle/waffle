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
package waffle.servlet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mockit.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.util.CorsPreflightCheck;

class CorsAwareNegotiateSecurityFilterTest {

    @Tested
    CorsAwareNegotiateSecurityFilter corsAwareNegotiateSecurityFilter;

    @Test
    void doFilter() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getHeader("Access-Control-Request-Method")).thenReturn("LOGIN");
        when(request.getHeader("Access-Control-Request-Headers")).thenReturn("X-Request-For");
        when(request.getHeader("Origin")).thenReturn("https://theorigin.localhost");

        new Expectations() {
            {
                CorsPreflightCheck.isPreflight(request);
                chain.doFilter(request, response);
            }
        };

        // corsAwareNegotiateSecurityFilter.doFilter(request,response,chain);

        new Verifications() {
            {
                CorsPreflightCheck.isPreflight(request);
                times = 1;
                chain.doFilter(request, response);
            }
        };

    }

}
