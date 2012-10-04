1.5 (Next Release)
==================

This release unifies support for various Java platforms within a single package and significantly improves development infrastructure.

Features
--------

* Waffle now requires Java 1.6 or newer, uses generics where possible - [@hazendaz](https://github.com/hazendaz).
* Waffle now requires JNA 3.5.0 (currently private build) - [@dblock](https://github.com/dblock).
* Native Tomcat and Spring support has been split out of the `waffle-jna.jar` - [@dblock](https://github.com/dblock).
  * `waffle-spring-security2.jar`: Spring-security 2.
  * `waffle-spring-security3.jar`: Spring-security 3.
  * `waffle-tomcat-5.jar` : Tomcat 5 valves.
  * `waffle-tomcat-6.jar` : Tomcat 6 valves.
  * `waffle-tomcat-7.jar` : Tomcat 7 valves.
* Rewritten documentation in Markdown, the CHM documentation has been removed - [@dblock](https://github.com/dblock).
* [#3](https://github.com/dblock/waffle/pull/3): Replaced `commons-logging` with `slf4j` and `logback` - [@hazendaz](https://github.com/hazendaz).
  * slf4j 1.7.0
  * logback 1.0.7
  * Use jcl over slf4j for Spring, as it uses `commons-logging`.
* Jacob-based COM interfaces and implementation have been removed - [@dblock](https://github.com/dblock).
* [#1](https://github.com/dblock/waffle/pull/1): Adjusted logging from info to debug to reduce noise level - [@mcfly83](https://github.com/mcfly83).
* [#17](https://github.com/dblock/waffle/pull/17): JAR manifest information includes specification and implementation details, such as GIT revision - [@ryantxu](https://github.com/ryantxu).
* [#23] Added `waffle.util.WaffleInfo` which collects system information useful for debugging - [@ryantxu](https://github.com/ryantxu).
* [#28] Added `waffle-jetty` project.  This lets developers run Waffle directly within the IDE - [@ryantxu](https://github.com/ryantxu).
* [#33](https://github.com/dblock/waffle/pull/33): Add support Servlet3 programmatic login- [@amergey](https://github.com/amergey).


Interface Changes
-----------------

* Waffle `boolean` getters now use `is*` java standard - [@hazendaz](https://github.com/hazendaz).
  * `getContinue` is now `isContinue`
  * `getDebug` is now `isDebug`
  * `getAllowGuestLogin` is now `isAllowGuestLogin`
  * `getImpersonate` is now `isImpersonate`
* Fixed case of `RevertToSelf`, now `revertToSelf` - [@hazendaz](https://github.com/hazendaz).
* All array getters now return empty arrays rather than `null` - [@hazendaz](https://github.com/hazendaz).

Development
-----------
  
* Upgraded thirdparty dependencies, using Ivy - [@hazendaz](https://github.com/hazendaz).
  * tomcat 5.5.35
  * tomcat 6.0.35
  * tomcat 7.0.30
  * guava 13.0.1
  * spring 3.1.2
  * spring 2.5.6.SEC03
  * spring-security 2.0.7
  * spring-security 3.1.2
  * junit 4.10
  * emma 2.1.5320
* Reworked development version to use ivy - [@hazendaz](https://github.com/hazendaz).
  * Removed all third party included jars.
  * Retained tomcat 5.5.35 due to ivy/maven only having 5.5.23 available.
* [#24](https://github.com/dblock/waffle/pull/24): Use mockito for waffle-mock - [@ryantxu](https://github.com/ryantxu).

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

