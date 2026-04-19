/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SimpleFilterConfig}.
 */
class SimpleFilterConfigTest {

    /**
     * Test default filter name.
     */
    @Test
    void testDefaultFilterName() {
        final SimpleFilterConfig config = new SimpleFilterConfig();
        Assertions.assertEquals("Simple Filter", config.getFilterName());
    }

    /**
     * Test set filter name.
     */
    @Test
    void testSetFilterName() {
        final SimpleFilterConfig config = new SimpleFilterConfig();
        config.setFilterName("My Filter");
        Assertions.assertEquals("My Filter", config.getFilterName());
    }

    /**
     * Test set and get parameter.
     */
    @Test
    void testSetAndGetParameter() {
        final SimpleFilterConfig config = new SimpleFilterConfig();
        config.setParameter("key1", "value1");
        Assertions.assertEquals("value1", config.getInitParameter("key1"));
        Assertions.assertNull(config.getInitParameter("nonexistent"));
    }

    /**
     * Test get init parameter names.
     */
    @Test
    void testGetInitParameterNames() {
        final SimpleFilterConfig config = new SimpleFilterConfig();
        config.setParameter("alpha", "a");
        config.setParameter("beta", "b");
        final java.util.Enumeration<String> names = config.getInitParameterNames();
        Assertions.assertNotNull(names);
        int count = 0;
        while (names.hasMoreElements()) {
            names.nextElement();
            count++;
        }
        Assertions.assertEquals(2, count);
    }

    /**
     * Test get servlet context returns null.
     */
    @Test
    void testGetServletContextReturnsNull() {
        final SimpleFilterConfig config = new SimpleFilterConfig();
        Assertions.assertNull(config.getServletContext());
    }
}
