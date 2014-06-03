Tomcat Mixed Single-SignOn and Form Authenticator Valve
=======================================================

The Waffle Tomcat Mixed Authenticator implements both the Negotiate protocol with Kerberos and NTLM single sign-on support and form-based authentication. Users can choose whether to login with current Windows credentials or with another username and password entered in a form. 

Configuring Tomcat
------------------

The following steps are required to configure Tomcat with Waffle Mixed Authenticator. 

Package Waffle JARs, including `waffle-jna.jar`, `guava-13.0.1.jar`, `jna-3.5.0.jar`, `platform-3.5.0.jar`, `slf4j*.jar` and `waffle-tomcat[tomcat version].jar` in the application's `lib` directory or copy them to your web server's lib. If you are using Eclipse, you can see which files tomcat is importing by going to Java Recources: `src / Libraries / Apache Tomcat vx.x`. If you've placed it in the tomcat directory and still don't see it, restart Eclipse.

Add a valve and a realm to the application context. For an application, modify `META-INF\context.xml`. 

``` xml
<Context>
  <Valve className="waffle.apache.MixedAuthenticator" />
  <Realm className="waffle.apache.WindowsRealm" />
</Context>
```

Configure security roles in `WEB-INF\web.xml`. The Waffle Mixed Authenticator adds all user's security groups (including nested and domain groups) as roles during authentication. 

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

Add a second security constraint that leaves the login page unprotected in `WEB-INF\web.xml`. 

``` xml
<security-constraint>
  <display-name>Login Page</display-name>
  <web-resource-collection>
    <web-resource-name>Unprotected Login Page</web-resource-name>
    <url-pattern>/login.jsp</url-pattern>
  </web-resource-collection>
</security-constraint>
```

Configure the location of the login and error pages for form authentication in `WEB-INF\web.xml`. 

``` xml
<login-config>
  <form-login-config>
    <form-login-page>/login.jsp</form-login-page>  
    <form-error-page>/error.html</form-error-page>  
  </form-login-config>
</login-config>
```

Create a login page based on the following code. There're two requirements for the login form. The form-based authentication must post to any valid location with the `j_security_check` parameter. The destination page will be loaded after a successful login. The single sign-on form must similarly post to any valid location with the `j_negotiate_check` parameter in the query string. 

``` html
<form method="POST" name="loginform" action="index.jsp?j_security_check">
<table style="vertical-align: middle;">
    <tr>
        <td>Username:</td>
        <td><input type="text" name="j_username" /></td>
    </tr>
    <tr>
        <td>Password:</td>
        <td><input type="password" name="j_password" /></td>
    </tr>
    <tr>
        <td><input type="submit" value="Login" /></td>
    </tr>
</table>
</form>
<hr>
<form method="POST" name="loginform" action="index.jsp?j_negotiate_check">
    <input type="submit" name="loginbutton" value="Login (Negotiate)" />
</form>
```

Troubleshooting
---------------

Add the following to `conf\logging.properties` in your Tomcat installation. 

```
waffle.apache.MixedAuthenticator.level = FINE
```

Restart Tomcat and review `logs\Catalina*.log`. 

Waffle Mixed Authenticator Demo
-------------------------------

A demo application can be found in the Waffle distribution in the `Samples\waffle-mixed` directory. Copy the entire directory into Tomcat's webapps directory and navigate to http://localhost:8080/waffle-mixed/. Pick your method of login. 

Valve Options
-------------

The following options are supported by the Valve. 

``` xml
<Context>
  <Valve className="waffle.apache.MixedAuthenticator" principalFormat="fqn" roleFormat="both" />
</Context>
```

* principalFormat: Specifies the name format for the principal.
* roleFormat: Specifies the name format for the role.
* allowGuestLogin: Allow guest login. When true and the system's Guest account is enabled, any invalid login succeeds as Guest. Note that while the default value of allowGuestLogin is true, it is recommended that you disable the system's Guest account to disallow Guest login. This option is provided for systems where you don't have administrative privileges. 
* protocols: authentication protocol(s), comma separated, default is "Negotiate,NTLM"

The following principal/group formats are supported. 

* fqn: Fully qualified names, such as `domain\username`. When unavailable, a SID is used. This is the default. 
* sid: SID in the S- format. 
* both: Both a fully qualified name and a SID in the S- format. The fully qualified name is placed in the list first. Tomcat assumes that the first entry of this list is a username.
* none: Do not include a principal name. Permitted only for `roleFormat`.
