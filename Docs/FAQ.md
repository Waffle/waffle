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
* [Can I provide a custom cache implementation?](faq/CustomCache.md)

Troubleshooting Stories
-----------------------

* [UnsatisfiedLinkerError jnadispatch](faq/UnsatisfiedLinkerErrorjnadispatch.md): solved by placing JNA jars in the common classloader.
* [Browser shows BASIC authentication popup](faq/BasicPopup.md): solved by re-ordering authenticators.
* [ClassNotFoundException on Tomcat](faq/ClassNotFoundTomcat.md): solved by putting `waffle-tomcat[tomcat version].jar` in `tomcat/lib`.
* [ClassNotFoundException on JBoss](faq/ClassNotFoundJBoss.md): solved by putting JARs in `application.ear/APP-INF/lib`.
* [Popup asking for username/password](https://waffle.codeplex.com/Thread/View.aspx?ThreadId=227969): solved by forcing NTLM, Kerberos not working.
* [Negotiate tries, but keeps failing with 401](faq/NegotiateFailsWith401.md): solved by creating a proper SPN with `setspn`.
* [Issues specifying AD groups with Spring-security](faq/ADGroupsSpringSecurity.md): solved by using the fully qualified user/group name.
* [Tomcat Manager not working under SSO (External Link)](http://code.dblock.org/ShowPost.aspx?id=147): solved by editing `401.jsp`.
* [Password prompt instead of SSO](faq/PassPromptInsteadOfSSO.md): solved by running Tomcat as `LocalSystem`.
* [Struts application not accepting multipart/form-data](faq/NotAcceptingMultipartData.md): solved by removing a legacy security constraint.
* [Server returns 401 Access Denied with the AP_ERR_MODIFIED error code](faq/AP_ERR_MODIFIED.md): solved by running server as a service with a domain account.
* [Failed to create temporary file for jnidispatch library](faq/TempFileFailed): `java.io.IOException`: solved by recreating Tomcat temp dir.
* com.sun.jna.platform.win32.Win32Exception: the logon attempt failed: solved by enabling Kerberos logging and [KB957097](https://support.microsoft.com/kb/957097).
* [Cannot find where to enable WAFFLE logging in JBoss](faq/JBossLogging.md): solved by locating application's `log4j.xml`.
* [NTLM fails with an Apache / AJP front-end](faq/AJP.md): solved by properly forwarding port number and re-enabling `keep-alive` in Apache `mod_ssl`.
* [HTTP/1.1 400 Bad Request](faq/BadRequest.md): Kerberos ticket was longer than 4K, solved by increasing `maxHttpHeaderSize`.
* [Negotiate fails with a load-balancer](faq/LoadBalancer.md): needs some DNS work and a proper SPN.
* [java.lang.IllegalStateException](faq/SessionTimeouts.md): Cannot create a session after the response has been committed error with Spring Security: resolved by disabling `SessionFixationProtectionFilter`.
* [Waffle returns service user as remote user](faq/ServiceUserAsRemoteUser.md): fixed by un-saving a user name and password on a local computer.
* [Issues with servlet filter on multiple Tomcat 7 Instances Sharing WAFFLE binaries](https://groups.google.com/forum/?fromgroups#!topic/waffle-users/4_K_O7BCn-c): solved by putting filter-mapping in the application's web.xml, also answered by [Tomcat bug 51754](https://issues.apache.org/bugzilla/show_bug.cgi?id=51754#c1).
* [Waffle returns outdated nonexistent user name after the user name was changed on domain](faq/ClearLSACacheToAvoidOutdatedPrincipalNames.md): solved by clearing the server LSA cache through the Windows registry according to MS Kbase article.
* [Status 401 (error code 80090308) when using .NET client and HTTP 1.0 protocol](https://groups.google.com/d/msg/waffle-users/Nisu-m19_nI/HLgaNhfBEw4J): solved by using default protocol version in .NET HttpWebRequest
* [Disable waffle debug log information in Tomcat server] (https://github.com/Waffle/waffle/issues/548) : solved by placing the logback configuration file in tomcat server library folder where waffle jar files are available.
* [Invalid Authorization Header Problems calling OAUTH2 covered Resources using the NegotiateSecurityFilter](faq/oauth2CORSBasicAndSSO.md) : Solved using a combination of [TomcatSingleSignOnValve](tomcat/TomcatSingleSignOnValve.md) and [NegotiateSecurityFilter](ServletSingleSignOnSecurityFilter.md)

Troubleshooting Help
----------------------

* See [Troubleshooting](https://github.com/dblock/waffle/blob/master/Docs/Troubleshooting.md)
