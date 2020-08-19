Can I provide a custom cache implementation?
============================================

Since version 1.9.0, Waffle uses [Caffeine](https://github.com/ben-manes/caffeine) for caching. To provide your own
implementation, see the Javadoc of `waffle.util.cache.Cache#newCache`…
