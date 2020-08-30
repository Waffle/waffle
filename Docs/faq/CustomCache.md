Can I provide a custom cache implementation?
============================================

Since version 1.9.0, Waffle uses [Caffeine](https://github.com/ben-manes/caffeine) for caching. To provide your own
implementation, see the Javadoc of `waffle.util.cache.Cache#newCache`…also shown below.

```javadoc
    * The cache implementation is obtained using {@link ServiceLoader}. To create your own implementation, implement
    * {@link CacheSupplier} and register it using the {@code /META-INF/services/waffle.cache.CacheSupplier} file on
    * your classpath.
```
