Can I provide a custom cache implementation?
============================================

Since version 1.9.0, Waffle uses [Caffeine](https://github.com/ben-manes/caffeine) for caching. To provide your own
implementation, see the Javadoc of `waffle.util.cache.Cache#newCache`…also shown below.

```javadoc
    * The cache implementation is obtained using {@link ServiceLoader}. To create your own implementation, implement
    * {@link CacheSupplier} and register it using the {@code /META-INF/services/waffle.util.cache.CacheSupplier} file
    * on your classpath.
```

### Error message (in log file)
`No CacheSupplier implementation found by ServiceLoader. Please see 
https://github.com/Waffle/waffle/blob/master/Docs/faq/CustomCache.md for possible solutions. Falling back to default
CaffeineCache implementation.`

#### Solutions
See the primary answer above for specifying a custom cache, or even just re-specifying the default load of 
`waffle.util.cache.CaffeineCacheSupplier`, by using the `META-INF/services/waffle.util.cache.CacheSupplier` file on
your classpath.

If you are still getting the message after trying that (most likely in use cases where you don't have much control
over the application classpath, like in a plugin environment), then you may need to adapt the thread
ContextClassLoader prior to the call in your code that leads to the error message, like in the below example. Note
that this is a workaround for when providing the file mentioned above in your classpath just doesn't work.

```java
// Save the current ContextClassLoader
Thread thread = Thread.currentThread();
ClassLoader loader = thread.getContextClassLoader();

// Set the desired ContextClassLoader
// In this case, using CacheSupplier to use the default CacheSupplier specified by the waffle-jna package
// You may choose a different class in the waffle-jna package
// You will want to choose a class in your classpath if you use the 
//   META-INF/services/waffle.util.cache.CacheSupplier file to provide a custom cache implementation
thread.setContextClassLoader(CacheSupplier.class.getClassLoader());

// Call Waffle-jna code that produced the error
// example:
try {
    this.negotiateSecurityFilter.init();
} finally {
    thread.setContextClassLoader(loader);
}
```
