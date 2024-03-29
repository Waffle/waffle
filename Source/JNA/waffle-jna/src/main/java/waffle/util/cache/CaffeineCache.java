/*
 * MIT License
 *
 * Copyright (c) 2010-2024 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
