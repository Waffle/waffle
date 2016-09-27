# How does the Waffle Servlet Security Filter work with CORS?

The Servlet Security Filter works fine with CORS. Configuring it depends on your server. 

## Jetty
If you are using Jetty, then it's a simple case of using the Jetty 
[`CrossOriginFilter`](http://www.eclipse.org/jetty/documentation/current/cross-origin-filter.html) to configure CORS. 
The following `web.xml` fragment gives an example:

```
    <!-- Configure the NegotiateSecurityFilter -->
    <filter>
        <filter-name>NegotiateSecurityFilter</filter-name>
        <filter-class>waffle.servlet.NegotiateSecurityFilter</filter-class>
        <init-param>
            <param-name>principalFormat</param-name>
            <param-value>fqn</param-value>
        </init-param>
        <init-param>
            <param-name>allowGuestLogin</param-name>
            <param-value>false</param-value>
        </init-param>
        <init-param>
            <param-name>securityFilterProviders</param-name>
            <param-value>waffle.servlet.spi.NegotiateSecurityFilterProvider</param-value>
        </init-param>
        <init-param>
            <param-name>waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols</param-name>
            <param-value>Negotiate</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>NegotiateSecurityFilter</filter-name>
        <url-pattern>/test-page</url-pattern>
    </filter-mapping>

    <!-- Configure CORS support -->
    <filter>
        <filter-name>CrossOriginFilter</filter-name>
        <filter-class>org.eclipse.jetty.servlets.CrossOriginFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>CrossOriginFilter</filter-name>
        <url-pattern>/test-page</url-pattern>
    </filter-mapping>
    <init-param>
        <param-name>allowedOrigins</param-name>
        <param-value>http://trusted-server.example.com,http://another-server.example.com:8080</param-value>
    </init-param>
```

## Tomcat
Tomcat also provides a [`CorsFilter`](http://tomcat.apache.org/tomcat-9.0-doc/config/filter.html#CORS_Filter) that should 
(currently untested!) also allow you to use the with CORS. 

```
    <!-- Configure the NegotiateSecurityFilter -->
    <filter>
        <filter-name>NegotiateSecurityFilter</filter-name>
        <filter-class>waffle.servlet.NegotiateSecurityFilter</filter-class>
        <init-param>
            <param-name>principalFormat</param-name>
            <param-value>fqn</param-value>
        </init-param>
        <init-param>
            <param-name>allowGuestLogin</param-name>
            <param-value>false</param-value>
        </init-param>
        <init-param>
            <param-name>securityFilterProviders</param-name>
            <param-value>waffle.servlet.spi.NegotiateSecurityFilterProvider</param-value>
        </init-param>
        <init-param>
            <param-name>waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols</param-name>
            <param-value>Negotiate</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>NegotiateSecurityFilter</filter-name>
        <url-pattern>/test-page</url-pattern>
    </filter-mapping>

    <!-- Configure CORS support -->
    <filter>
        <filter-name>CorsFilter</filter-name>
        <filter-class>org.apache.catalina.filters.CorsFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>CorsFilter</filter-name>
        <url-pattern>/test-page</url-pattern>
    </filter-mapping>
    <init-param>
        <param-name>cors.allowed.origins</param-name>
        <param-value>http://trusted-server.example.com,http://another-server.example.com:8080</param-value>
    </init-param>
```
