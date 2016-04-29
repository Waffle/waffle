Servlet Single-SignOn Security Filter
=====================================

The Waffle Servlet Security Filter implements the Negotiate and Basic protocols with Kerberos and NTLM single sign-on support. This allows users to browse to a Windows intranet site without having to re-enter credentials for browsers that support Kerberos or NTLM and to fall back to Basic authentication for those that do not. While it still enables you to protect different resources based on different user roles (user groups), the filter is a catch-all solution to enable Windows single-sign-on for a web server that implements servlets (Tomcat, Jetty, etc.). 

Moreover, this filter supports impersonating the remote client, enabling to perform actions as the remote user using native Windows APIs. 

Configuring Web Servers
-----------------------

The following steps are required to configure a web server with the Waffle Servlet Security Filter. These instructions work for Tomcat, Jetty, WebSphere and possibly others.

Package Waffle JARs, including `waffle-jna-1.8.1.jar`, `guava-19.0.jar`, `jna-4.2.2.jar`, `jna-platform-4.2.2.jar` and `slf4j-1.7.21.jar` in the application's `lib` directory or copy them to your web server's lib. 

Add a security filter to `WEB-INF\web.xml` of your application. 

``` xml
<filter>
  <filter-name>SecurityFilter</filter-name>
  <filter-class>waffle.servlet.NegotiateSecurityFilter</filter-class>
</filter>
<filter-mapping>
  <filter-name>SecurityFilter</filter-name>
  <url-pattern>/*</url-pattern>
</filter-mapping>
```

Filter Options
--------------

The filter can be configured with the following `init-param` options. 

* principalFormat: Specifies the name format for the principal.
* roleFormat: Specifies the name format for the role.
* allowGuestLogin: Allow guest login. When true and the system's Guest account is enabled, any invalid login succeeds as Guest. Note that while the default value of `allowGuestLogin` is true, it is recommended that you disable the system's Guest account to disallow Guest login. This option is provided for systems where you don't have administrative privileges. 
* authProvider: A class that implements `IWindowsAuthProvider` and has a parameterless constructor. 
* securityFilterProviders: A list of security filter providers. By default, both `BasicSecurityFilterProvider` and `NegotiateSecurityFilterProvider` will be loaded. 
* waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols: A list of security protocols supported by the `NegotiateSecurityFilterProvider`. Can be one of or a combination of Negotiate and NTLM. 
* waffle.servlet.spi.BasicSecurityFilterProvider/realm: The name of the Realm for BASIC authentication. 
* impersonate: Allow impersonation. When true the remote user will be impersonated. Note that there is no mapping between the Windows native threads, under which the impersonation takes place, and the Java threads. Thus you'll need to use Windows native APIs to perform impersonated actions. Any action done in Java will still be performed with the user account running the servlet container. 

Filter Configuration Example
----------------------------

``` xml
<filter>
  <filter-name>SecurityFilter</filter-name>
  <filter-class>waffle.servlet.NegotiateSecurityFilter</filter-class>   
  <init-param>
      <param-name>principalFormat</param-name>
      <param-value>fqn</param-value>
  </init-param>
  <init-param>
      <param-name>roleFormat</param-name>
      <param-value>both</param-value>
  </init-param>
  <init-param>
      <param-name>allowGuestLogin</param-name>
      <param-value>true</param-value>
  </init-param>
  <init-param>
      <param-name>impersonate</param-name>
      <param-value>true</param-value>
  </init-param>
  <init-param>
      <param-name>securityFilterProviders</param-name>
      <param-value>
          waffle.servlet.spi.BasicSecurityFilterProvider
          waffle.servlet.spi.NegotiateSecurityFilterProvider
      </param-value>
  </init-param>
  <init-param>
      <param-name>waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols</param-name>
      <param-value>
          Negotiate
          NTLM
      </param-value>
  </init-param>
  <init-param>    
      <param-name>waffle.servlet.spi.BasicSecurityFilterProvider/realm</param-name>
      <param-value>WaffleFilterDemo</param-value>
  </init-param>
</filter>
```

Waffle Security Filter Demo
---------------------------

A demo application can be found in the Waffle distribution in the `Samples\waffle-filter` directory. Copy the entire directory into Tomcat's or Jetty's webapps directory and navigate to http://localhost:8080/waffle-filter/.

You can use maven to build and deploy this demo application by following the steps below. These assume you have checked out the waffle source tree.

* Update your tomcat server configuration (tomcat/conf/tomcat-users.xml) to grant the roles required for a user to deploy an application into tomcat.

```
    <role rolename="tomcat"/>
    <user username="tomcat" password="tomcat" roles="tomcat,manager-gui,manager-script,manager-jmx,manager-status"/>
```

* Update your maven settings.xml file (.m2/settings.xml) to include a definition of your local tomcat server and the user/pwd used to deploy to that server.

```
    <servers>
    ...
        <server>
            <id>mylocalserver</id>
            <username>tomcat</username>
            <password>tomcat</password>
        </server>
```

* Start your tomcat server. You can launch a locally installed tomcat with remote debugging enabled on port 8000 using:

```
    apache-tomcat-6.0.44$ bin/catalina.sh jpda start
```

* Build and Deploy the demo application to the local tomcat 6 instance. In the waffle source tree, move to the directory: waffle/Source/JNA/waffle-demo-parent/waffle-filter, and execute the following:

```
   mvn clean package tomcat6:redeploy
```

   The app will be available at:

        http://localhost:8080/waffle-filter-demo/


Troubleshooting (Tomcat)
------------------------

Enable waffle logging. Add the following to `conf\logging.properties` in your Tomcat installation. 

``` 
waffle.servlet.NegotiateSecurityFilter.level = FINE
waffle.servlet.spi.SecurityFilterProviderCollection.level = FINE
waffle.servlet.spi.NegotiateSecurityFilterProvider.level = FINE
waffle.servlet.spi.BasicSecurityFilterProvider.level = FINE
```

Restart Tomcat and review `logs\Catalina*.log`. 

