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

/**
 * Service interface to instantiate a new {@link Cache}.
 *
 * @author Simon Legner
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
     * @return a new cache
     */
    <K, V> Cache<K, V> newCache(final long timeout);
}
