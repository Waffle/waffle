/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SimpleRequestDispatcher}.
 */
class SimpleRequestDispatcherTest {

    /**
     * Test forward sets redirect status and location header.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testForward() throws IOException, ServletException {
        final SimpleRequestDispatcher dispatcher = new SimpleRequestDispatcher("/redirect/url");
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final SimpleHttpResponse response = new SimpleHttpResponse();
        dispatcher.forward(request, response);
        Assertions.assertEquals(304, response.getStatus());
        Assertions.assertEquals("/redirect/url", response.getHeader("Location"));
    }

    /**
     * Test include does not throw.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testIncludeDoesNotThrow() throws IOException, ServletException {
        final SimpleRequestDispatcher dispatcher = new SimpleRequestDispatcher("/some/url");
        final SimpleHttpRequest request = new SimpleHttpRequest();
        final SimpleHttpResponse response = new SimpleHttpResponse();
        Assertions.assertDoesNotThrow(() -> dispatcher.include(request, response));
    }
}
