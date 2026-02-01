/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util.cache;

/**
 * A {@link CacheSupplier} supplying {@link CaffeineCache}.
 */
public class CaffeineCacheSupplier implements CacheSupplier {

    @Override
    public <K, V> Cache<K, V> newCache(final long timeout) {
        return new CaffeineCache<>(timeout);
    }
}
