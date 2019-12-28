Spring Security Authentication Provider
=======================================

The Waffle Spring-Security Authenticator implements Windows authentication for Spring-Security-enabled applications. For more information about Spring-Security see [here](https://projects.spring.io/spring-security/). 

Configuring Spring Security
---------------------------

The following steps are required to configure a web server with the Waffle Spring-Security Authenticator and form login. 

We'll assume that Spring-Security is configured via `web.xml` with a filter chain and a Spring context loader listener. The Waffle beans configuration will be added to waffle-auth.xml. 

``` xml
<filter>
    <filter-name>springSecurityFilterChain</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
<filter-mapping>
    <filter-name>springSecurityFilterChain</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/waffle-auth.xml</param-value> 
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

Copy Waffle JARs, including `waffle-jna-2.1.1.jar`, `caffeine-2.8.0.jar`, `jna-5.5.0.jar`, `jna-platform-5.5.0.jar`, `slf4j-api-2.0.0-alpha1.jar` and `waffle-spring-security4-2.1.1.jar` in the application's `lib` directory along with Spring and Spring-Security JARs. Or, if you use Maven, add the following to your `pom.xml`:

- For latest snapshot instead use `waffle-jna-2.1.2-SNAPSHOT`, `caffeine-2.8.0.jar`, `jna-5.5.0.jar`, `jna-platform-5.5.0.jar`, `slf4j-api-2.0.0-alpha1.jar` and `waffle-spring-security4-2.1.2-SNAPSHOT.jar`

``` xml
<dependency>
    <groupId>com.github.dblock.waffle</groupId>
    <artifactId>waffle-spring-security4</artifactId>
    <version>${waffle.version}</version>
</dependency>            
```

Declare a Waffle Windows authentication provider. This is the link between Waffle and the operating system. 

``` xml
<bean id="waffleWindowsAuthProvider" class="waffle.windows.auth.impl.WindowsAuthProviderImpl" />
```

Add a Waffle Spring authentication provider. 

``` xml
<bean id="waffleSpringAuthenticationProvider" class="waffle.spring.WindowsAuthenticationProvider">
    <property name="AuthProvider" ref="waffleWindowsAuthProvider" />
</bean>
```

Define the Spring-Security authentication manager. 

``` xml
<sec:authentication-manager alias="authenticationProvider">
    <sec:authentication-provider ref="waffleSpringAuthenticationProvider" />
</sec:authentication-manager>
```

Granted Authorities
-------------------

Upon successful login, Waffle will populate Spring Security's Authentication object with instances of `GrantedAuthority`. 

By default, Waffle will populate the Authentication object with the following: 

* A `GrantedAuthority` with the string `ROLE_USER`. 
* One `GrantedAuthority` per group to which the user belongs. The `GrantedAuthority` strings will be the uppercase group name prefixed with `ROLE_`. For example, if a user is a member of the Everyone group, he obtains the `ROLE_EVERYONE` granted authority. 

The default behavior can be changed by configuring a different `defaultGrantedAuthority` and `grantedAuthorityFactory` on the `waffleSpringAuthenticationProvider`. 

Spring Authentication Provider Options
--------------------------------------

The `waffleSpringAuthenticationProvider` bean can be configured with the following options. 

* principalFormat: Specifies the name format for the principal.
* roleFormat: Specifies the name format for the role.
* allowGuestLogin: Allow guest login. When true and the system's Guest account is enabled, any invalid login succeeds as Guest. That that while the default value of allowGuestLogin is true, it is recommended that you disable the system's Guest account to disallow Guest login. This option is provided for systems where you don't have administrative privileges.  
* defaultGrantedAuthority: Specifies the GrantedAuthority to be added to every successfully authenticated user. By default, the `defaultGrantedAuthority` will add a GrantedAuthority for `ROLE_USER`. If you don't want this behavior, you can set the `defaultGrantedAuthority` to `null` (if you do not want a `GrantedAuthority` to be added by default), or some other `GrantedAuthority`.
* grantedAuthorityFactory: Used to create `GrantedAuthority` objects for each of the groups to which the authenticated user belongs. The default `grantedAuthorityFactory` will construct `GrantedAuthority` objects whose string is the uppercase group name prefixed with `ROLE_`. 

``` xml
<bean id="waffleSpringAuthenticationProvider" class="waffle.spring.WindowsAuthenticationProvider">
    <property name="AllowGuestLogin" value="false" />
    <property name="PrincipalFormat" value="fqn" />
    <property name="RoleFormat" value="both" />
    <property name="AuthProvider" ref="waffleWindowsAuthProvider" />
</bean>
```

Waffle Spring-Security Form Login Demo
--------------------------------------

A demo application can be found in the Waffle distribution in the `Samples\waffle-spring-form` directory. Copy the entire directory into Tomcat's or Jetty's webapps directory and navigate to http://localhost:8080/waffle-spring-form/. Login with your Windows credentials. 
