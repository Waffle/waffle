/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CaffeineCache}.
 */
class CaffeineCacheTest {

    /** The cache under test. */
    private CaffeineCache<String, String> cache;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.cache = new CaffeineCache<>(600);
    }

    /**
     * Test put and get.
     */
    @Test
    void testPutAndGet() {
        Assertions.assertNull(this.cache.get("key1"));
        this.cache.put("key1", "value1");
        Assertions.assertEquals("value1", this.cache.get("key1"));
    }

    /**
     * Test remove.
     */
    @Test
    void testRemove() {
        this.cache.put("key1", "value1");
        Assertions.assertEquals("value1", this.cache.get("key1"));
        this.cache.remove("key1");
        Assertions.assertNull(this.cache.get("key1"));
    }

    /**
     * Test size.
     */
    @Test
    void testSize() {
        Assertions.assertEquals(0, this.cache.size());
        this.cache.put("key1", "value1");
        Assertions.assertEquals(1, this.cache.size());
        this.cache.put("key2", "value2");
        Assertions.assertEquals(2, this.cache.size());
        this.cache.remove("key1");
        Assertions.assertEquals(1, this.cache.size());
    }

    /**
     * Test overwrite value.
     */
    @Test
    void testOverwriteValue() {
        this.cache.put("key1", "value1");
        this.cache.put("key1", "value2");
        Assertions.assertEquals("value2", this.cache.get("key1"));
        Assertions.assertEquals(1, this.cache.size());
    }

    /**
     * Test remove nonexistent key does not throw.
     */
    @Test
    void testRemoveNonexistentKey() {
        Assertions.assertDoesNotThrow(() -> this.cache.remove("nonexistent"));
    }

    /**
     * Test cache new cache via service loader returns a usable cache.
     */
    @Test
    void testCacheNewCache() {
        final Cache<String, String> newCache = Cache.newCache(600);
        Assertions.assertNotNull(newCache);
        newCache.put("hello", "world");
        Assertions.assertEquals("world", newCache.get("hello"));
        newCache.remove("hello");
        Assertions.assertNull(newCache.get("hello"));
    }
}
