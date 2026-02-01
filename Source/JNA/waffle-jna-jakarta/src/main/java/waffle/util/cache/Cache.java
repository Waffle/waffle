/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util.cache;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A semi-persistent mapping from keys to values.
 *
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 *
 * @see <a href="https://github.com/Waffle/waffle/blob/master/Docs/CustomCache.md">Can I provide a custom cache
 *      implementation?</a>
 */
public interface Cache<K, V> {

    /**
     * Creates a new cache with the specified timeout.
     * <p>
     * The cache implementation is obtained using {@link ServiceLoader}. To create your own implementation, implement
     * {@link CacheSupplier} and register it using the {@code /META-INF/services/waffle.util.cache.CacheSupplier} file
     * on your classpath.
     *
     * @param timeout
     *            timeout in seconds
     * @param <K>
     *            the type of keys maintained by this cache
     * @param <V>
     *            the type of mapped values
     *
     * @return a new cache
     *
     * @throws NoSuchElementException
     *             if no cache can be instantiated, use {@link Exception#getSuppressed()} to obtain details.
     */
    static <K, V> Cache<K, V> newCache(int timeout) throws NoSuchElementException {
        final NoSuchElementException exception = new NoSuchElementException();
        boolean cacheSupplierFound = false;
        for (CacheSupplier cacheSupplier : ServiceLoader.load(CacheSupplier.class)) {
            cacheSupplierFound = true;
            try {
                return cacheSupplier.newCache(timeout);
            } catch (Exception e) {
                exception.addSuppressed(e);
            }
        }

        if (!cacheSupplierFound) {
            Logger logger = LoggerFactory.getLogger(Cache.class);
            logger.error(
                    "No CacheSupplier implementation found by ServiceLoader. Please see https://github.com/Waffle/waffle/blob/master/Docs/faq/CustomCache.md for possible solutions. Falling back to default CaffeineCache implementation.",
                    exception);
            return new CaffeineCache<>(timeout);
        }

        throw exception;
    }

    /**
     * Fetches the key from the cache
     *
     * @param key
     *            the key
     *
     * @return the corresponding value
     *
     * @see java.util.Map#get
     */
    V get(K key);

    /**
     * Stores a binding for the key and the value in the cache
     *
     * @param key
     *            the key
     * @param value
     *            the value
     *
     * @see java.util.Map#put
     */
    void put(K key, V value);

    /**
     * Removes the binding for the key from the cache
     *
     * @param key
     *            the key
     *
     * @see java.util.Map#remove(Object)
     */
    void remove(K key);

    /**
     * Returns the number of bindings in this cache
     *
     * @return the size
     *
     * @see java.util.Map#size
     */
    int size();
}
