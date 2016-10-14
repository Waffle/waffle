WildFly Windows Login Using JAAS
================================

Waffle implements a standard Java Authentication and Authorization login module that can be used as a security domain to authenticate Windows users in WildFly. This enables you to add a BASIC, DIGEST or FORMS login to your application and authenticate against a Windows Active Directory using Windows local or domain groups as means of authorization. 

Configuring WildFly
-------------------

The following steps are required to configure WildFly with Waffle authenticator.

Include the Waffle JARs, `waffle-jna-1.8.1.jar`, `guava-19.0.jar`, `jna-4.2.2.jar`, `jna-platform-4.2.2.jar`, and `slf4j-1.7.21.jar` in your war's `WEB-INF\lib` directory. Alternatively you may place them in the `standalone/lib/ext` folder within the wildfly installation.

Create a security domain using the Waffle `WindowsLoginModule`. It is recommended you keep the principal and role formats to `fqn`. 
```xml
<security-domain name="MySecurityDomain" cache-type="default">
    <authentication>
        <login-module code="waffle.jaas.WindowsLoginModule" flag="required">
            <module-option name="debug" value="true"/>
            <module-option name="principalFormat" value="fqn"/>
            <module-option name="roleFormat" value="fqn"/>
        </login-module>
    </authentication>
</security-domain>
```

Configuring your webapp
-----------------------

Add the security domain you created above in your `jboss-web.xml` configuration.
```xml
<jboss-web>
    ...
    <security-domain>MySecurityDomain</security-domain>
</jboss-web>
```

In the `WEB-INF\web.xml` add the `NegotiateSecurityFilter` filter, and apply the filter to the section of your web app you wish to secure. Below is an example of the security filter being applied on the entire web app.
```xml
<filter>
	<filter-name>SecurityFilter</filter-name>
	<filter-class>waffle.servlet.NegotiateSecurityFilter</filter-class>
	<init-param>
		<param-name>authProvider</param-name>
		<param-value>waffle.windows.auth.impl.WindowsAuthProviderImpl</param-value>
	</init-param>
	<init-param>
		<param-name>allowGuestLogin</param-name>
		<param-value>false</param-value>
	</init-param>
	<init-param>
		<param-name>impersonate</param-name>
		<param-value>false</param-value>
	</init-param>
</filter>

<filter-mapping>
	<filter-name>SecurityFilter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>
```

Continuing with the changes to the `WEB-INF\web.xml` file, enable BASIC, DIGEST or FORMS authentication for this realm.

``` xml
<login-config>
	<auth-method>BASIC</auth-method>
	<realm-name>MySecurityDomain</realm-name>
</login-config>
```

You will also need to configure the security roles to be used by the web app. The Waffle login module adds all user's security groups (including nested and domain groups) as roles during authentication. 

``` xml
<security-role>
  <role-name>Everyone</role-name>
</security-role>
```

Restrict access to the web apps resources. For example, to restrict the entire website to locally authenticated users add the following in `WEB-INF\web.xml`. 

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

You will now be able to use the `isUserInRole` method within your web app, retricting features to authorized users only.
```java
final Principal principal = request.getUserPrincipal();

if (request.isUserInRole("DOMAIN\\Special")) {
    // Only users assigned to the group 'DOMAIN\special' will have access to this section;
} else {
	System.out.println("Sorry " + principal.getName() + " you don't have permission.");
```

Troubleshooting
---------------

Most issues are caused by an incorrect WildFly configuration. Enable JAAS logging by adding the following to `standalone\configuration\standalone.xml` in your WildFly installation.

```xml
<logger category="waffle.windows.auth">
	<level name="DEBUG"/>
</logger>
<logger category="waffle.jaas">
	<level name="DEBUG"/>
</logger>
```

Restart WildFly and review the `standalone\log\server.log`.
