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

/**
 * A semi-persistent mapping from keys to values.
 *
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 *
 * @author Simon Legner
 */
public interface Cache<K, V> {

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
