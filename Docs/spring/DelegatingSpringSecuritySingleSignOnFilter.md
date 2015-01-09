Delegating Spring Security Single-SignOn Filter
====================================

The Waffle Delegating Spring-Security Filter extends the [Spring Security Single-SignOn Filter](https://github.com/dblock/waffle/blob/master/Docs/spring/SpringSecuritySingleSignOnFilter.md) by allowing the application using the filter to inject an additional authenticationmanager to provide authorization to a principal
that is authenticated in towards the active directory in the single sign-on process.

Configuring Spring Security
---------------------------
Configure spring security as is done for [Spring Security Single-SignOn Filter](https://github.com/dblock/waffle/blob/master/Docs/spring/SpringSecuritySingleSignOnFilter.md)

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



Waffle Spring-Security Demo
---------------------------

A demo application can be found in the Waffle distribution in the `Samples\waffle-spring-filter` directory. Copy the entire directory into Tomcat's or Jetty's webapps directory and navigate to http://localhost:8080/waffle-spring-filter/. 
