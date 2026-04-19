/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import java.io.IOException;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SimpleFilterChain}.
 */
class SimpleFilterChainTest {

    /**
     * Test get request and response after do filter.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testGetRequestAndResponseAfterDoFilter() throws IOException {
        final SimpleFilterChain chain = new SimpleFilterChain();
        Assertions.assertNull(chain.getRequest());
        Assertions.assertNull(chain.getResponse());

        final SimpleHttpRequest request = new SimpleHttpRequest();
        final SimpleHttpResponse response = new SimpleHttpResponse();
        try {
            chain.doFilter(request, response);
        } catch (final Exception e) {
            // swallow
        }
        Assertions.assertEquals(request, chain.getRequest());
        Assertions.assertEquals(response, chain.getResponse());
    }

    /**
     * Test do filter stores request and response.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testDoFilterStoresRequestResponse() throws IOException {
        final SimpleFilterChain chain = new SimpleFilterChain();
        final SimpleHttpRequest req = new SimpleHttpRequest();
        final SimpleHttpResponse resp = new SimpleHttpResponse();
        try {
            chain.doFilter(req, resp);
        } catch (final Exception e) {
            // swallow
        }
        final ServletRequest storedReq = chain.getRequest();
        final ServletResponse storedResp = chain.getResponse();
        Assertions.assertNotNull(storedReq);
        Assertions.assertNotNull(storedResp);
    }
}
