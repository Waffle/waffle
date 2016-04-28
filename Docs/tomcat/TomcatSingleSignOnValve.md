Tomcat Single-SignOn Authenticator Valve
========================================

The Waffle Tomcat Negotiate Authenticator implements the Negotiate protocol with Kerberos and NTLM single sign-on support. This allows users to browse to a Windows intranet site without having to re-enter credentials. The authenticator integrates with Tomcat Realms and therefore allows you to protect select areas of the website. 

Configuring Tomcat
------------------

The following steps are required to configure Tomcat with Waffle authenticator. 

Package Waffle JARs, including `waffle-jna-1.8.1.jar`, `guava-19.0.jar`, `jna-4.2.2.jar`, `jna-platform-4.2.2.jar`, `slf4j-1.7.21.jar` and `waffle-tomcat-6.jar` in the application's lib directory or copy them to Tomcat's lib.

Add a valve and a realm to the application context. For an application, modify `META-INF\context.xml`. 

``` xml
<Context>
  <Valve className="waffle.apache.NegotiateAuthenticator" />
  <Realm className="waffle.apache.WindowsRealm" />
</Context>
```

Configure security roles in `WEB-INF\web.xml`. The Waffle authenticator adds all user's security groups, including nested and domain groups, as roles during authentication. 

``` xml
<security-role>
  <role-name>BUILTIN\Users</role-name>
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
    <role-name>BUILTIN\Users</role-name>
  </auth-constraint>
</security-constraint>
```

Troubleshooting
---------------

Enable Waffle logging. Add the following to `conf\logging.properties` in your Tomcat installation. 

```
waffle.apache.NegotiateAuthenticator.level = FINE
```

Restart Tomcat and review `logs\Catalina*.log`. 

Waffle Authenticator Demo
-------------------------

A demo application can be found in the Waffle distribution in the `Samples\waffle-negotiate` directory. Copy the entire directory into Tomcat's `webapps` directory and navigate to `http://localhost:8080/waffle-negotiate/`. 

Valve Options
-------------

The following options are supported by the Valve. 

``` xml
<Context>
  <Valve className="waffle.apache.NegotiateAuthenticator" principalFormat="fqn" roleFormat="both" />
</Context>
```

* principalFormat: Specifies the name format for the principal.
* roleFormat: Specifies the name format for the role.
* allowGuestLogin Allow guest login. When true and the system's Guest account is enabled, any invalid login succeeds as Guest. 
* protocols: authentication protocol(s), comma separated, default is "Negotiate,NTLM"

Note: While the default value of `allowGuestLogin` is true, it is recommended that you disable the system's "Guest" account to disallow Guest login. This option is provided for systems where you don't have administrative privileges. 

The following principal/group formats are supported. 

* fqn: Fully qualified names, such as `domain\username`. When unavailable, a SID is used. This is the default. 
* sid: SID in the S- format. 
* both: Both a fully qualified name and a SID in the S- format. The fully qualified name is placed in the list first. Tomcat assumes that the first entry of this list is a username. 
* none Do not include a principal name. Permitted only for `roleFormat`.
