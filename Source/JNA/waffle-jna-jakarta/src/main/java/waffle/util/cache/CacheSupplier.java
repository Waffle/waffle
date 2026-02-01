/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util.cache;

/**
 * Service interface to instantiate a new {@link Cache}.
 */
public interface CacheSupplier {

    /**
     * Creates a new cache with the specified timeout
     *
     * @param timeout
     *            timeout in seconds
     * @param <K>
     *            the type of keys maintained by this cache
     * @param <V>
     *            the type of mapped values
     *
     * @return a new cache
     */
    <K, V> Cache<K, V> newCache(long timeout);
}
