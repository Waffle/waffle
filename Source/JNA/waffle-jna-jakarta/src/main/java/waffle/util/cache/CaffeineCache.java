/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util.cache;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;

import org.checkerframework.checker.index.qual.NonNegative;

/**
 * A {@link Cache} based on {@link com.github.benmanes.caffeine.cache.Cache}.
 *
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
public class CaffeineCache<K, V> implements Cache<K, V> {

    /** The cache store. */
    private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;

    /**
     * Instantiate new caffeine cache with timeout.
     *
     * @param timeout
     *            Specified timeout in seconds for cache.
     */
    public CaffeineCache(@NonNegative final long timeout) {
        cache = Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(timeout)).build();
    }

    @Override
    public V get(final K key) {
        return cache.asMap().get(key);
    }

    @Override
    public void put(final K key, final V value) {
        cache.put(key, value);
    }

    @Override
    public void remove(final K key) {
        cache.asMap().remove(key);
    }

    @Override
    public int size() {
        return cache.asMap().size();
    }
}
