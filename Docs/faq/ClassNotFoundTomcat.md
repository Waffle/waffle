# ClassNotFoundException on Tomcat

## Question

As a new JSP / Tomcat developer, I'm looking for some suggestions on how to troubleshoot this issue:

At startup, when deploying my test app, Tomcat logs this:

```
SEVERE: Begin event threw exception
java.lang.ClassNotFoundException: waffle.apache.NegotiateAuthenticator
    at java.net.URLClassLoader$1.run(URLClassLoader.java:200)
```

which throws a real monkey wrench in my plans to use Waffle and Windows authentication.

I develop and package my web app using Eclipse.  When I look in the WAR file (using 7zip), I can see the Waffle Jars are included in the WEB-INF\lib folder, so they should be getting deployed to Tomcat.  In Eclipse, I can expand the referenced libraries, waffle-jna.jar file and see "waffle.apache.NegotiateAuthenticator.class".    I use the same project and method to deploy jtds-1.2.5.jar, which works to retrieve and display data from a Microsoft SQL Server.   What am I missing?

Still getting the java.lang.ClassNotFoundException:  waffle.apache.NegotiateAuthenticator when Tomcat deploys the application.

I  moved all files with the exception of waffle-jna.jar to the tomcat\lib folder.  I updated my Eclipse project leaving only waffle-jna.jar and successfully built the WAR.

tomcat/lib contains ( commons.logging-1.1.1.jar   jna.jar  platform.jar  Waffle.Windows.AuthProvider.dll  Waffle.Windows.AuthProvider.msm  waffle-jacob.jar )

With Tomcat stopped,  I updated my classpath to null, my path to include only the tomcat/bin, tomcat/lib, and the Java SDK bin folders.

Startup Tomcat.  Other test app using JTDS works fine (to validate my environment changes).

I then uploaded my waffle tester app using the tomcat web interface, and get the ClassNotFoundException.

My \web\META-INF\context.xml file contains (copied the from instructions in the CHM helpfile):

```xml
<?xml version='1.0' encoding='utf-8'?>
<Context>
  <Valve className="waffle.apache.NegotiateAuthenticator" principalFormat="fqn" roleFormat="both" />
  <Realm className="waffle.apache.WindowsRealm" />
</Context>
```

Operating system is Windows Vista Ultimate ... and I'm logged in with admin privileges.   

The goal is to limit access to Windows users who are members of a specific role in our Active directory.

## Answer
I would start by putting those files manually in tomcat's lib and getting your configuration to work. Then you can move them into your application. You need jna.jar, platform.jar and commons-logging-1.1.1.jar as well, maybe you have an error above this one or an error in Tomcat's catalina.log. Depending on what you're using you may need to place those files in the parent classloader (tomcat/lib) anyway. Usually all but waffle-jna.jar go in the parent classloader (Tomcat).

You need waffle-jna.jar, not waffle-jacob.jar and you don't need Waffle.Windws.AuthProvider.*.