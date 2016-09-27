Frequently Asked Questions (FAQ)
=================================


General FAQ
-----------

* [Can Waffle be used on the client side?](faq/ClientSide.md)
* [Does Waffle work on *nix, including Linux?](faq/DoesWaffleWorkOnLinux.md)
* [How can I retrieve additional information, such as user's position in the company, from Active Directory?](faq/AdditionalActiveDirectoryInfo.md)
* [Can Waffle be used with Atlassian JIRA?](faq/AtlassianJIRA.md)
* [Can Waffle be used with Apache Shiro?](faq/waffleShiro.md)
* [How can I prevent the browser from showing a login popup on failed authentication?](faq/ClientSideFailures.md)
* [How does the Waffle Servlet Security Filter work with CORS?](faq/CORS.md)

Troubleshooting Stories
-----------------------

* [UnsatisfiedLinkerError jnadispatch](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=220195): solved by placing JNA jars in the common classloader.
* [Browser shows BASIC authentication popup](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=221324): solved by re-ordering authenticators.
* [ClassNotFoundException on Tomcat](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=223416): solved by putting `waffle-tomcat[tomcat version].jar` in `tomcat/lib`.
* [ClassNotFoundException on JBoss](http://waffle.codeplex.com/discussions/244552): solved by putt JARs in `application.ear/APP-INF/lib`.
* [Popup asking for username/password](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=227969): solved by forcing NTLM, Kerberos not working.
* [Negotiate authentication returns 404 File Not Found](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=223212): solved by creating a missing `401.html`.
* [Negotiate tries, but keeps failing with 401](http://waffle.codeplex.com/discussions/254748): solved by creating a proper SPN with `setspn`.
* [Issues specifying AD groups with Spring-security](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=222735): solved by using the fully qualified user/group name.
* [Tomcat Manager not working under SSO](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=235759): solved by editing `401.jsp`, [external solution](http://code.dblock.org/ShowPost.aspx?id=147).
* [Password prompt instead of SSO](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=236554): solved by running Tomcat as `LocalSystem`.
* [Struts application not accepting multipart/form-data](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=236540): solved by removing a legacy security constraint.
* [Server returns 401 Access Denied with the AP_ERR_MODIFIED error code](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=243106): solved by running server as a service with a domain account.
* [Failed to create temporary file for jnidispatch library](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=243500): `java.io.IOException`: solved by recreating Tomcat temp dir.
* [com.sun.jna.platform.win32.Win32Exception](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=244126): the logon attempt failed: solved by enabling Kerberos logging and [KB957097](http://support.microsoft.com/kb/957097).
* [Cannot find where to enable WAFFLE logging in JBoss](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=244399&ANCHOR#Post560814): solved by locating application's `log4j.xml`.
* [NTLM fails with an Apache / AJP front-end](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=244329): solved by properly forwarding port number and re-enabling `keep-alive` in Apache `mod_ssl`.
* [IE6 NTLM fails with an Apache front-end with SSL](http://waffle.codeplex.com/discussions/267605): solved by enabling `keep-alive` in Apache `mod_ssl`.
* [java.lang.NoClassDefFoundError: org/apache/juli/logging/LogFactory with Jetty and JAAS](http://waffle.codeplex.com/Thread/View.aspx?ThreadId=214211): solved by specifying JAAS realms in Jetty configuration.
* [HTTP/1.1 400 Bad Request](http://waffle.codeplex.com/discussions/222438): Kerberos ticket was longer than 4K, solved by increasing `maxHttpHeaderSize`.
* [Negotiate fails with a load-balancer](http://waffle.codeplex.com/discussions/271250): needs some DNS work and a proper SPN.
* [java.lang.IllegalStateException](http://waffle.codeplex.com/discussions/288877): Cannot create a session after the response has been committed error with Spring Security: resolved by disabling `SessionFixationProtectionFilter`.
* [Waffle returns service user as remote user](http://waffle.codeplex.com/discussions/346411): fixed by un-saving a user name and password on a local computer.
* [Issues with servlet filter on multiple Tomcat 7 Instances Sharing WAFFLE binaries](https://groups.google.com/forum/?fromgroups#!topic/waffle-users/4_K_O7BCn-c): solved by putting filter-mapping in the application's web.xml, also answered by [Tomcat bug 51754](https://issues.apache.org/bugzilla/show_bug.cgi?id=51754#c1).
* [Waffle returns outdated nonexistent user name after the user name was changed on domain](faq/ClearLSACacheToAvoidOutdatedPrincipalNames.md): solved by clearing the server LSA cache through the Windows registry according to MS Kbase article.
* [Status 401 (error code 80090308) when using .NET client and HTTP 1.0 protocol](https://groups.google.com/d/msg/waffle-users/Nisu-m19_nI/HLgaNhfBEw4J): solved by using default protocol version in .NET HttpWebRequest


Troubleshooting Help
----------------------

* See [Troubelshooting](https://github.com/dblock/waffle/blob/master/Docs/Troubleshooting.md)
