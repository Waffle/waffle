/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.util.cache;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;

import org.checkerframework.checker.index.qual.NonNegative;

/**
 * A {@link Cache} based on {@link com.github.benmanes.caffeine.cache.Cache}
 *
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 *
 * @author Simon Legner
 */
public class CaffeineCache<K, V> implements Cache<K, V> {

    private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;

    public CaffeineCache(@NonNegative final long timeout) {
        cache = Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(timeout)).build();
    }

    @Override
    public V get(K key) {
        return cache.asMap().get(key);
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public void remove(K key) {
        cache.asMap().remove(key);
    }

    @Override
    public int size() {
        return cache.asMap().size();
    }
}
