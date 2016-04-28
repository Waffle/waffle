Tomcat Windows Login JAAS Authenticator
=======================================

Waffle implements a standard Java Authentication and Authorization login module that can be notably used with a JAAS realm to authenticate Windows users in Tomcat. This enables you to add a BASIC, DIGEST or FORMS login to your application and authenticate against a Windows Active Directory using Windows local or domain groups as means of authorization. 

Configuring Tomcat
------------------

The following steps are required to configure Tomcat with Waffle authenticator. 

Package Waffle JARs, including `waffle-jna-1.8.1.jar`, `guava-19.0.jar`, `jna-4.2.2.jar`, `jna-platform-4.2.2.jar`, `slf4j-1.7.21.jar` and `waffle-jaas.jar` in the application's lib directory or copy them to Tomcat's lib.

Add a JAAS realm to the application context. Modify `META-INF\context.xml`.
 
``` xml
<Context>
 <Realm className="org.apache.catalina.realm.JAASRealm" 
    appName="Jaas" 
    userClassNames="waffle.jaas.UserPrincipal" 
    roleClassNames="waffle.jaas.RolePrincipal"
    useContextClassLoader="false" 
    debug="false" />
</Context>

Enable BASIC, DIGEST or FORMS authentication for this realm. Modify `WEB-INF\web.xml`. 

``` xml
<login-config>
    <auth-method>BASIC</auth-method>
    <realm-name>Jaas</realm-name>
</login-config>
```

Configure security roles in `WEB-INF\web.xml`. The Waffle login module adds all user's security groups (including nested and domain groups) as roles during authentication. 

``` xml
<security-role>
  <role-name>Everyone</role-name>
</security-role>
```

Restrict access to website resources. For example, to restrict the entire website to locally authenticated users add the following in `WEB-INF\web.xml`. 

``` xml
<security-constraint>
  <web-resource-collection>
    <web-resource-name>
      Demo Application
    </web-resource-name>
    <url-pattern>/*</url-pattern>
    <http-method>GET</http-method>
    <http-method>POST</http-method>
  </web-resource-collection>
  <auth-constraint>
    <role-name>Everyone</role-name>
  </auth-constraint>
</security-constraint>
```

Login.conf
----------

Create a login configuration file, `login.conf`. This configuration file specifies how to plug the Waffle Windows Login Module. 

```
Jaas {
    waffle.jaas.WindowsLoginModule sufficient debug=false;
};
```

The login.conf configuration file is passed to Java with `-Djava.security.auth.login.config=<path-to-file>/login.conf`. 

The following options are supported by the module. 

* debug: Set to "true" to enable debug mode. In debug mode the module will output information about successful logins, including group memberships. 
* principalFormat: Specifies the name format for the principal.
* roleFormat: Specifies the name format for the role.
* allowGuestLogin: Allow guest login. When true and the system's Guest account is enabled, any invalid login succeeds as Guest. 

Note: While the default value of `allowGuestLogin` is "true", it is recommended that you disable the system's Guest account to disallow Guest login. This option is provided for systems where you don't have administrative privileges. 
 
The following principal and role formats are available. 

* fqn: Fully qualified names, such as domain\group. When unavailable, a SID is used. This is the default. 
* sid: Group SID in the S- format. 
* both: Both a fully qualified name and a SID in the S- format. When a group name is not available, a SID is used. 
* none: Available for roleFormat only. Do not retrieve roles. 

Jaas.policy
-----------

Create JAAS policy configuration file, jaas.policy. This file specifies which identities are granted which permissions. 
 
```
grant Principal * * {
  permission java.security.AllPermission "/*";
};
```

The policy file is passed to Java with `-Djava.security.auth.policy=<path-to-file>/jaas.policy`. 

Starting Tomcat w/ Security Manager
-----------------------------------

You must start Tomcat with Security Manager enabled (`-security`) and configure it with a login configuration and policy. For example, the following will start Tomcat using the demo `login.conf` and `jaas.policy` from the Waffle samples. 

``` bat
@echo off
setlocal
set JAVA_OPTS=-Djava.security.auth.login.config="C:/Program Files/Tomcat/webapps/waffle-jaas/login.conf" -Djava.security.auth.policy="C:/Program Files/Tomcat/webapps/waffle-jaas/jaas.policy"
call bin/catalina.bat run -security
endlocal
```

Troubleshooting
---------------

Most issues are caused by an incorrect JAAS configuration. Enable JAAS logging by adding the following to `conf\logging.properties` in your Tomcat installation. 

```
org.apache.catalina.realm.level = FINE
```

Restart Tomcat and review `logs\Catalina*.log`. 

Waffle JAAS Demo
----------------

A demo application can be found in the Waffle distribution in the `Samples\waffle-jaas` directory. Copy the entire directory into Tomcat's webapps directory and navigate to http://localhost:8080/waffle-jaas/. 
