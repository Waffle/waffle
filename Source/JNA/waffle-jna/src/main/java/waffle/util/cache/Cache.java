/**
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

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * A semi-persistent mapping from keys to values.
 *
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 *
 * @author Simon Legner
 * @see <a href="https://github.com/Waffle/waffle/blob/master/Docs/CustomCache.md">Can I provide a custom cache implementation?</a>
 */
public interface Cache<K, V> {

    /**
     * Creates a new cache with the specified timeout.
     *
     * The cache implementation is obtained using {@link ServiceLoader}. To create your own implementation, implement
     * {@link CacheSupplier} and register it using the {@code /META-INF/services/waffle.cache.CacheSupplier} file on
     * your classpath.
     *
     * @param timeout
     *            timeout in seconds
     * @param <K>
     *            the type of keys maintained by this cache
     * @param <V>
     *            the type of mapped values
     * @return a new cache
     * @throws NoSuchElementException
     *             if no cache can be instantiated, use {@link Exception#getSuppressed()} to obtain details.
     */
    static <K, V> Cache<K, V> newCache(int timeout) throws NoSuchElementException {
        final NoSuchElementException exception = new NoSuchElementException();
        for (CacheSupplier cacheSupplier : ServiceLoader.load(CacheSupplier.class)) {
            try {
                return cacheSupplier.newCache(timeout);
            } catch (Exception e) {
                exception.addSuppressed(e);
            }
        }
        throw exception;
    }

    /**
     * Fetches the key from the cache
     *
     * @param key
     *            the key
     * @return the corresponding value
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
     * @see java.util.Map#put
     */
    void put(K key, V value);

    /**
     * Removes the binding for the key from the cache
     *
     * @param key
     *            the key
     * @see java.util.Map#remove(Object)
     */
    void remove(K key);

    /**
     * Returns the number of bindings in this cache
     *
     * @return the size
     * @see java.util.Map#size
     */
    int size();
}
