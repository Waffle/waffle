Next Release
============

Misc
----

* [#3](https://github.com/dblock/waffle/pull/3): Converted commons-logging to slf4j logging - [@hazendaz](https://github.com/hazendaz).
  * Added slf4j with additional jars for jcl, jdk, log4j, nop, simple logging solutions.
  * Added jcl over slf4j jar for spring as it still uses commons-logging.
  * Added logback for native slf4j solution.
  * Added sample xml files for logback solution to all demos.
* [#3](https://github.com/dblock/waffle/pull/3): Upgraded thirdparty tomcat to 6.0.35 - [@hazendaz](https://github.com/hazendaz).
* [#2](https://github.com/dblock/waffle/pull/2): Upgraded thirdparty Guava to 12.0 - [@hazendaz](https://github.com/hazendaz).
* [#2](https://github.com/dblock/waffle/pull/2): Upgraded thirdparty JUnit to 4.10 - [@hazendaz](https://github.com/hazendaz).
* [#1](https://github.com/dblock/waffle/pull/1): Adjusted logging from info to debug to reduce noise level - [@mcfly83](https://github.com/mcfly83).

1.4 (6/21/2011) 
===============

First release off [Github](http://github.com/dblock/waffle).

Features
--------

* #8559: Added impersonation support on the Servlet security filter.
* #9353: Allow customization of `GrantedAuthority` string in Spring Security filter and authentication manager.
* #8493: Intermediate security contexts of unfinished Negotiate protocol instances expire after ten seconds.
* #9854: Added support for query strings with multiple parameters to `MixedAuthenticator`.
* #243081: Filter providers and protocols specified in configuration can be separated by any type of space.

Misc
----

* #11052: Upgraded thirdparty JNA to 3.3.0.
* #11053: Upgraded thirdparty WIX to 3.5.
* #9552: Upgraded thirdparty Tomcat to 6.0.29.
* #8493: Using Guava (Google collections), which requires a new *guava-r07.jar* in deployment of Java filters and applications.
* #9456: Added Serializable to `waffle.jaas.RolePrincipal`, `UserPrincipal`, `waffle.servlet.WindowsPrincipal` and `waffle.windows.Auth`.
* #9657: Added `authProvider` option to the `NegotiateSecurityFilter` filter options.
* #9895: Upgraded Jacob to 1.15M4 and JacobGen to 0.10.
* #10031: Removed `waffle.windows.auth.IWindowsSecurityContext` null initialize and added `targetName` to the remaining initialize interface method.

Bugs
----

* #9274: Guest `WindowsIdentity` leaks a handle when guest login disabled.
* #224546: Unable to deploy other Spring-security providers alongside Waffle. Spring Security Filter will now fall through to the remaining filter chain for unsupported security protocols.
* #8965: Anonymous login is not correctly recognized as guest on Windows 7.
* #229310: `NegotiateRequestWrapper.isUserInRole(SID)` broken. Specifying roleFormat as both and calling `isUserInRole` with a SID value always incorrectly returns false.
* #9615: *waffle-form*, *waffle-mixed* and *waffle-form* samples fail with 404 instead of 401; html files not packaged in the distribution.
* #9889: `WindowsComputerImpl` sometimes returned wrong number of groups.
* #9552: `NegotiateSecurityFilterProvider` leaks a handle with new logons.

1.3 (7/21/2010)
===============

Features
--------

* Ported Waffle to native Java with JNA 3.2.7, added *waffle-jna.jar*.
* Added a Negotiate (NTLM and Kerberos) Tomcat authenticator, `waffle.apache.NegotiateAuthenticator` in *waffle-jna.jar*.
* Added a JAAS Windows Login module, `waffle.jaas.WindowsLoginModule` in *waffle-jna.jar*.
* Added a Mixed (Negotiate and Form-Based) Tomcat security authenticator, `waffle.apache.MixedAuthenticator` in *waffle-jna.jar*.
* Added a Negotiate (NTLM and Kerberos) and Basic Servlet security filter, `waffle.servlet.NegotiateSecurityFilter` in *waffle-jna.jar*. Works with any servlet container, including Tomcat, Jetty and Websphere.
* Added a Spring-Security Negotiate (NTLM and Kerberos) and Basic Filter, `waffle.spring.NegotiateSecurityFilter` in *waffle-jna.jar*.
* Added a Spring-Security Authentication Manager, `waffle.spring.WindowsAuthenticationManager` in *waffle-jna.jar*.
* Added `IWindowsIdentity.IsGuest`.

Misc
----

* Project upgraded to Visual Studio 2008.

1.2 (3/1/2010)
==============

Initial open-source release under the Eclipse Public License.

