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
 * A {@link CacheSupplier} supplying {@link CaffeineCache}.
 *
 * @author Simon Legner
 */
public class CaffeineCacheSupplier implements CacheSupplier {

    @Override
    public <K, V> Cache<K, V> newCache(final long timeout) {
        return new CaffeineCache<>(timeout);
    }
}
