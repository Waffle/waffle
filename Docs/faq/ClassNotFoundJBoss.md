# ClassNotFoundException on JBoss

# Question

Can anyone please let me know that we can use Waffle Frame work with Jboss 5.X or not? I am able to do that with 4.X. I am trying to use Waffle with Jboss 5.X/6.X I am getting the following error can any one help me out.

```
Exception starting filter SecurityFilter: java.lang.RuntimeException: java.lang.ClassNotFoundException: waffle.servlet.spi.BasicSecurityFilterProvider waffle.servlet.spi.NegotiateSecurityFilterProvider from BaseClassLoader@909419{vfs:///C:/DEV/jboss-6.0.0/server/default/deploy/Test.war}
    at waffle.servlet.spi.SecurityFilterProviderCollection.<init>(Unknown Source) [:]
    at waffle.servlet.NegotiateSecurityFilter.init(Unknown Source) [:]
    at org.apache.catalina.core.ApplicationFilterConfig.getFilter(ApplicationFilterConfig.java:447) [:6.0.0.Final]
    at org.apache.catalina.core.StandardContext.filterStart(StandardContext.java:3246) [:6.0.0.Final]
    at org.apache.catalina.core.StandardContext.start(StandardContext.java:3843) [:6.0.0.Final]
    at org.jboss.web.tomcat.service.deployers.TomcatDeployment.performDeployInternal(TomcatDeployment.java:294) [:6.0.0.Final]
    at org.jboss.
```

# Answer

Looks like your waffle JARs aren't in the right place. They should be in the location of the global classloader
We run JBoss 5.1.0_GA, and our jars are in .../application.ear/APP-INF/lib