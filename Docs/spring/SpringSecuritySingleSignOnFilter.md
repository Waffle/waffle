Spring Security Single-SignOn Filter
====================================

The Waffle Spring-Security Filter implements the Negotiate and Basic protocols with Kerberos and NTLM single sign-on support for web applications that utilize Spring-Security. This allows users to browse to a Windows intranet site without having to re-enter credentials for browsers that support Kerberos or NTLM and to fall back to Basic authentication for those that do not. For more information about Spring-Security see [http://static.springsource.org/spring-security/site/](http://static.springsource.org/spring-security/site/). 
NOTE: Also available with delegation to support authentication for the service provider [here] (https://github.com/dblock/waffle/blob/master/Docs/spring/DelegatingSpringSecuritySingleSignOnFilter.md)
Configuring Spring Security
---------------------------

The following steps are required to configure a web server with the Waffle Spring-Security Filter. 

We'll assume that Spring-Security is configured via `web.xml` with a filter chain and a Spring context loader listener. The Waffle beans configuration will be added to `waffle-filter.xml`.
 
```
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
    <param-value>/WEB-INF/waffle-filter.xml</param-value> 
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

Copy Waffle JARs, including `waffle-jna.jar`, `guava-18.0.jar`, `jna-4.2.0.jar`, `jna-platform-4.2.0.jar`, `slf4j*.jar` and `waffle-spring-security-3.jar` in the application's `lib` directory along with Spring and Spring-Security JARs. Or, if you use Maven, add the following to your `pom.xml`:

``` xml
<dependency>
    <groupId>com.github.dblock.waffle</groupId>
    <artifactId>waffle-spring-security3</artifactId>
    <version>${waffle.version}</version>
</dependency>            
```

Declare a Windows Authentication provider. This is the link between Waffle and the operating system. 

``` xml
<bean id="waffleWindowsAuthProvider" class="waffle.windows.auth.impl.WindowsAuthProviderImpl" />
```

Declare a collection of Waffle security filter providers that implement various authentication protocols. 

``` xml
<bean id="negotiateSecurityFilterProvider" class="waffle.servlet.spi.NegotiateSecurityFilterProvider">
    <constructor-arg ref="waffleWindowsAuthProvider" />
</bean>

<bean id="basicSecurityFilterProvider" class="waffle.servlet.spi.BasicSecurityFilterProvider">
    <constructor-arg ref="waffleWindowsAuthProvider" />
</bean>

<bean id="waffleSecurityFilterProviderCollection" class="waffle.servlet.spi.SecurityFilterProviderCollection">
    <constructor-arg>
        <list>
            <ref bean="negotiateSecurityFilterProvider" />               
            <ref bean="basicSecurityFilterProvider" />               
        </list>
    </constructor-arg>
</bean>
```

Add the Waffle security filter and entry point to the `sec:http` configuration section. The filter will be placed before the `BASIC` authentication filter that ships with Spring-Security. The filter uses the collection of authentication filter providers defined above to perform authentication. 

``` xml
<sec:http entry-point-ref="negotiateSecurityFilterEntryPoint">
    <sec:intercept-url pattern="/**" access="IS_AUTHENTICATED_FULLY" />
    <sec:custom-filter ref="waffleNegotiateSecurityFilter" position="BASIC_AUTH_FILTER" />
</sec:http>

<bean id="negotiateSecurityFilterEntryPoint" class="waffle.spring.NegotiateSecurityFilterEntryPoint">
    <property name="Provider" ref="waffleSecurityFilterProviderCollection" />
</bean>
```

Define a required default Spring-Security authentication manager. 

``` xml
<sec:authentication-manager alias="authenticationProvider" />
```

Define a Spring-Security filter that uses the collection of security filter providers to perform authentication. 

``` xml
<bean id="waffleNegotiateSecurityFilter" class="waffle.spring.NegotiateSecurityFilter">
    <property name="Provider" ref="waffleSecurityFilterProviderCollection" />
</bean>
```

Granted Authorities
-------------------

Upon successful login, Waffle will populate Spring Security's Authentication instance with `GrantedAuthority` objects. 

By default, Waffle will populate the Authentication instance with the following `GrantedAuthority` objects: 

* A `GrantedAuthority` with the string `ROLE_USER`. 
* One `GrantedAuthority` per group to which the user belongs. The `GrantedAuthority` strings will be the uppercase group name prefixed with `ROLE_`. For example, if a user is a member of the `Everyone` group, he obtains the `ROLE_EVERYONE` granted authority. 

The default behavior can be changed by configuring a different `defaultGrantedAuthority` and `grantedAuthorityFactory` on the `waffleNegotiateSecurityFilter` object. 

Security Filter Options
-----------------------

The `waffleNegotiateSecurityFilter` bean can be configured with the following options. 

* principalFormat: Specifies the name format for the principal.
* roleFormat: Specifies the name format for the role.
* allowGuestLogin: Allow guest login. When true and the system's Guest account is enabled, any invalid login succeeds as Guest. Note that while the default value of allowGuestLogin is true, it is recommended that you disable the system's Guest account to disallow Guest login. This option is provided for systems where you don't have administrative privileges. 
* impersonate: Allow impersonation. When true the remote user will be impersonated. Note that there is no mapping between the Windows native threads, under which the impersonation takes place, and the Java threads. Thus you'll need to use Windows native APIs to perform impersonated actions. Any action done in Java will still be performed with the user account running the servlet container.
* defaultGrantedAuthority: Specifies the `GrantedAuthority` to be added to every successfully authenticated user. By default, the `defaultGrantedAuthority` will add a `GrantedAuthority` for `ROLE_USER`. If you do not want this behavior, you can set the `defaultGrantedAuthority` to `null` (if you do not want a `GrantedAuthority` to be added by default), or some other `GrantedAuthority`. 
* grantedAuthorityFactory: Used to create `GrantedAuthority` objects for each of the groups to which the authenticated user belongs. The default `grantedAuthorityFactory` will construct `GrantedAuthority` objects whose string is the uppercase group name prefixed with `ROLE_`. 

``` xml
<bean id="waffleNegotiateSecurityFilter" class="waffle.spring.NegotiateSecurityFilter">
    <property name="Provider" ref="waffleSecurityFilterProviderCollection" />
    <property name="AllowGuestLogin" value="false" />
    <property name="Impersonate" value="true" />
    <property name="PrincipalFormat" value="fqn" />
    <property name="RoleFormat" value="both" />
</bean>
```
Security Filter Provider Collection Options
-------------------------------------------

The `waffleSecurityFilterProviderCollection` bean can be constructed with a list of available security filter providers. Waffle supplies a Negotiate and a BASIC authentication security filter provider. In addition, each provider may implement more options. 

``` xml
<bean id="waffleSecurityFilterProviderCollection" class="waffle.servlet.spi.SecurityFilterProviderCollection">
    <constructor-arg>
        <list>
            <ref bean="negotiateSecurityFilterProvider" />               
            <ref bean="basicSecurityFilterProvider" />               
        </list>
    </constructor-arg>
</bean>
```

Negotiate Security Filter Provider Options
------------------------------------------

The `negotiateSecurityFilterProvider` bean supports a list of protocols. Choose one or the combination of Negotiate and NTLM. 

``` xml
<bean id="negotiateSecurityFilterProvider" class="waffle.servlet.spi.NegotiateSecurityFilterProvider">
    <constructor-arg ref="waffleWindowsAuthProvider" />
    <property name="protocols">
        <list>
            <value>Negotiate</value>
            <value>NTLM</value>
        </list>
    </property>
</bean>
```

Basic Security Filter Provider Options
--------------------------------------

The `basicSecurityFilterProvider` bean supports a custom Basic authentication Realm name. 

``` xml
<bean id="basicSecurityFilterProvider" class="waffle.servlet.spi.BasicSecurityFilterProvider">
    <constructor-arg ref="waffleWindowsAuthProvider" />
    <property name="realm" value="DemoRealm" />
</bean>
```

Waffle Spring-Security Demo
---------------------------

A demo application can be found in the Waffle distribution in the `Samples\waffle-spring-filter` directory. Copy the entire directory into Tomcat's or Jetty's webapps directory and navigate to http://localhost:8080/waffle-spring-filter/. 
