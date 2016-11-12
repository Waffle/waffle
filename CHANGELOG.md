1.8.2 (?/??/16)
================
* Lots of documentation updates from the community (many thanks!)
* [#397](https://github.com/dblock/waffle/pull/397): WindowsLoginModule missing roles in Principal. [@devnullpointer](https://github.com/devnullpointer)
* [#338](https://github.com/dblock/waffle/pull/338): Don't allow SPNEGO NegTokenArg to start re-authentication process [@AriSuutariST](https://github.com/AriSuutariST). 
* [#342](https://github.com/dblock/waffle/pull/342): Add tomcat 8.5.x support [@hazendaz](https://github.com/hazendaz). 
* [#357](https://github.com/dblock/waffle/pull/357): Fix security token handle leak in Tomcat. Issue [#355](https://github.com/dblock/waffle/issues/355)
* [#382](https://github.com/dblock/waffle/pull/382): Bug fix in DelegatingNegotiateSecurityFilter when no custom authentication provider was declared [@Unaor]

1.8.1 (2/10/16)
================

* Official notification dropping long-term support on 1.7.x branch
* Rework .net build to be mostly automatic using nuget
* Change .net target to more modern .net 4.0 framework
* [#309](https://github.com/dblock/waffle/pull/309): Added impersonation support on spring-security filters [@sergey-podolsky](https://github.com/sergey-podolsky).
* [#296](https://github.com/dblock/waffle/pull/296): Added Tomcat 9 support.
* [#268](https://github.com/dblock/waffle/pull/301): Cannot log in automatically on machine where Tomcat service is running
* [#274](https://github.com/dblock/waffle/pull/274): Update WindowsSecurityContextImpl.java to handle SEC_E_BUFFER_TOO_SMALL
* [#128](https://github.com/dblock/waffle/pull/128): Update WindowsSecurityContext.cs to handle SEC_E_BUFFER_TOO_SMALL
* [#310](https://github.com/dblock/waffle/pull/310): Add equals and hashCode to WindowsPrincipal

1.8.0 (09/10/15)
================
*** Java Requirement now 1.7 ***

* Introduction of diamond operator and try with resources firmly requiring java 7.
* [#187](https://github.com/dblock/waffle/pull/187): Removed Spring 2 and Tomcat 5 support.
* [#226](https://github.com/dblock/waffle/pull/226): Moving base to java 1.7
* [#239](https://github.com/dblock/waffle/pull/239): Fix handle leak in LSASS.exe process.

1.7.5 (11/7/15)
===============
* Backport [#239](https://github.com/dblock/waffle/pull/239): Fix handle leak in LSASS.exe process.

1.7.4 (05/12/15)
================
* [#188](https://github.com/dblock/waffle/issues/188): Added support for service provider to authorize the principal.
* [#192](https://github.com/dblock/waffle/pull/192): Fix: Tomcat 8 MixedAuthenticator uses LoginConfig out of context.
* [#206](https://github.com/dblock/waffle/pull/206): Fix issue [#203](https://github.com/dblock/waffle/issues/203)
  ** Tomcat negotiate filters reporting Win32Error 500 status error instead of 401.
  ** Related to issue [#107](https://github.com/dblock/waffle/issues/107)
* [#207](https://github.com/dblock/waffle/pull/207): Further refinement of test dependencies and now requires java 7 to compile library.
  ** At this point, still supports java 6 runtimes.

* Github gh-pages now built via mvn site plugin.
* We use sfl4j, so use jcl-over-slf4j instead of allowing spring to bring in commons-logging.

1.7.3 (12/21/2014)
===================
* Corrected javadoc issues in shiro package to ensure javadocs build.
* Make some package methods private in shiro package.

1.7.2 (Not Released)
====================
* Ensure waffle dependencies referenced in poms are against vulnerability free releases.
* Rework java build to conform with maven standard practices.
* Enhance distribution to build zip thus allowing maven central deployment.
* Discovered issues with classpath / javadoc, release aborted upon push to maven central.

Developer note
--------------
* [#164](https://github.com/dblock/waffle/issues/164): Added unit test in waffle-tests using catch-exception test library to verify the condition caught is actually expected.

1.7.1 (11/30/2014 - waffle-jna only)
====================================
* [#164](https://github.com/dblock/waffle/issues/164): Added try/catch to authorization header base64 decode in cases of invalid or unsupported authentication header.
  ** Throws runtimeException "Invalid authorization header."
* [#168](https://github.com/dblock/waffle/pull/168): Exception stack trace on invalid credentials.
  ** Change in waffle 1.7 per sonar to trap only thrown errors resulted in a regression where user enters invalid
     creditionals and expected behaviour is to ask again but instead a stack trace was thrown.  Special thanks to
     @gstanchev for finding and helping resolve this issue.
* Drop legacy base64 usage previously deprecated.  We use guava for this now.
* Small number of array object creations cleanup.

1.7 (9/25/2014)
===============

Notable Feature Changes
-----------------------
* Full Mavenized Build
* All demos now mavenized
* Support for Tomcat 8
* Support for Spring 4 & Spring-security 4
* Enhanced logging
* Tomcat Protocol parameter for valves to allow default Negotiate / NTLM or selective setup
* Restructured project for full maven support and clearer intent
* Upgraded .NET build to Visual Studio 10, .NET Framework 4
* Enforce Java code formatting (space based) through maven plugin
* Enforce License information in Java code through maven plugin
* Deprecated Base64 internal usage in favor of using Guava BaseEncoding Base64.
* Mocking Testing of third party implementations for cleaner intent.

Changes
--------
* [#140](https://github.com/dblock/waffle/pull/140): Mocking Unit Tests - [@hazendaz](https://github.com/hazendaz).
  * Mock implementations used in unit tests for various features such as tomcat/shiro in order to make it clear to intention of waffle tests.
* [#136](https://github.com/dblock/waffle/pull/136): Enable user logging when using filter [@tbenbrahim](https://github.com/tbenbrahim).
  * Added toString to WindowsPrincipal to enable logging of authenticated user when using the servlet filter, using the waffle.servlet.NegotiateSecurityFilter.PRINCIPAL session attribute.
* [#120](https://github.com/dblock/waffle/pull/120): Application Security License - [@hazendaz](https://github.com/hazendaz).
  * Using License Maven Plugin to ensure license is up to date on java files
  * All donated code to library now has proper license
  * License controlled through license.txt under waffle-parent
* [#119](https://github.com/dblock/waffle/pull/119): Format Enahancement - [@hazendaz](https://github.com/hazendaz).
  * Using Java Format Maven Plugin to ensure formatting of code consistent
  * Now using spaces rather than tabs.
* [#108](https://github.com/dblock/waffle/pull/108): Spring 4 - [@hazendaz](https://github.com/hazendaz).
  * Spring 4 / Spring Security 4 support
  * Early release [no changes over spring 3]
* [#101](https://github.com/dblock/waffle/pull/101): Enhance Logging - [@hazendaz](https://github.com/hazendaz).
  * Use full feature {} of logging and stop concatenating strings.
* [#97](https://github.com/dblock/waffle/pull/97): Added protocols parameter on Tomcat valves - [@hasalex](https://github.com/hasalex).
  * Attribute protocols on the valve in order to limit the authentication to one or some protocols
* [#93](https://github.com/dblock/waffle/pull/93): Updated Documentation - [@hazendaz](https://github.com/hazendaz).
  * First cut at updating documentation to reflect maven.
* [#92](https://github.com/dblock/waffle/pull/92): Pom Corrections - [@hazendaz](https://github.com/hazendaz).
  * Oops! #91 attempted to remove .settings but actually added them back, removing again.
* [#91](https://github.com/dblock/waffle/pull/91): Drop eclipse settings - [@ryantxu](https://github.com/ryantxu).
  * More maven cleanup work, removed .settings, .classpath, and .project files from build as maven creates these.
  * Additional benefit here is that this is easily built using many various IDE's tanks to maven.
* [#90](https://github.com/dblock/waffle/pull/90): Pom Corrections - [@hazendaz](https://github.com/hazendaz).
  * Corrected missed change #87 on rename of build in multi module pom
  * Fixed issue with incorrect objenesis version being picked up by maven resolution
  * Reworked parent POM for use with users without their own nexus repo
  * Fixed to work properly with GIT so jars show all necessary manifest information
* [#88](https://github.com/dblock/waffle/pull/88): Full Mavenization - Part 2 - [@hazendaz](https://github.com/hazendaz).
  * Using standard maven layout now.
  * Fixed one test case that was case sensitive
  * Added default to case statements with break.
* [#87](https://github.com/dblock/waffle/pull/87): Renamed 'demo' & 'build' - [@hazendaz](https://github.com/hazendaz).
  * Renamed these modules to reflect their true nature
* [#86](https://github.com/dblock/waffle/pull/86): Full Mavenization - Part 1 - [@hazendaz](https://github.com/hazendaz).
  * Building on maven beginnings of project for making this a maven only build
  * Removed ant/ivy configuration
  * Known issue in built files due to not using standard maven layout, expect to fix later
  * Cleanup git ignores for removed ivy items
  * Corrected issue with mockito pulling in old hamcrest
  * Reworked demo to be more maven like in layout
  * Added more settings for tomcat8
  * Jetty skips javadocs due to no public classes
* [#84](https://github.com/dblock/waffle/pull/84): Added a better embedded Jetty example - [@juliangamble](https://github.com/juliangamble).
  * See 'Adding a better embedded Jetty example PR #81' for more details
* [#83](https://github.com/dblock/waffle/pull/83): Added fluido skin - [@hazendaz](https://github.com/hazendaz).
  * Provides maven site generation using twitter bootstrap for nice look and feel
* [#82](https://github.com/dblock/waffle/pull/82): Tomcat 8 Support (BETA) - [@hazendaz](https://github.com/hazendaz).
  * BETA Tomcat 8 support
* [#78](https://github.com/dblock/waffle/pull/78): POM Updates - [@hazendaz](https://github.com/hazendaz).
  * Now supporting tomcat 6.0.39 / 7.0.52
  * Updated versions throughout
* [#76](https://github.com/dblock/waffle/pull/76): Add [SPNEGO NegTokenArg](http://msdn.microsoft.com/en-us/library/ms995330.aspx) support - [@AriSuutariST](https://github.com/AriSuutariST).
* Fixed `WindowsComputerImpl.Groups` returning an empty local groups set - [@dblock](https://github.com/dblock).
* [#114](https://github.com/dblock/waffle/issues/114): Fixed `Waffle.Windows.AuthProvider.WindowsSecurityContext` and `WindowsAuthProviderImpl` to loop and allocate memory on `SEC_E_INSUFFICIENT_MEMORY` beyond `Secur32.MAX_TOKEN_SIZE` in `InitializeSecurityContext` and `AcceptSecurityContext` - [@kentcb](https://github.com/kentcb).

1.6 (12/24/2013)
================

Features
--------

* [#64](https://github.com/dblock/waffle/pull/64): Upgraded to JNA 4.0 - [@ryantxu](https://github.com/ryantxu).
* [#40](https://github.com/dblock/waffle/pull/40): Added [SPNEGO negotiation](http://msdn.microsoft.com/en-us/library/ms995330.aspx) support - [@AriSuutariST](https://github.com/AriSuutariST).
* [#48](https://github.com/dblock/waffle/pull/48): Added username/password authentication support for [Apache Shiro](http://shiro.apache.org/) - [@davidmc24](https://github.com/davidmc24).
* [#51](https://github.com/dblock/waffle/pull/51): Added negotiate authentication support for [Apache Shiro](http://shiro.apache.org/) - [@bhamail](https://github.com/bhamail).

Bugs
----

* [#58](https://github.com/dblock/waffle/pull/58): Fix: error in `InitializeSecurityContext: The buffers supplied to a function was too small.` when a user belongs to many groups - [@dblock](https://github.com/dblock).

Development
-----------

* [#42](https://github.com/dblock/waffle/pull/42): Replaced [GroboUtils](http://groboutils.sourceforge.net/) with [ContiPerf](http://databene.org/contiperf.html) in the Java load tests to remove use of the "Opensymphony Release" repository - [@davidmc24](https://github.com/davidmc24).
* [#42](https://github.com/dblock/waffle/pull/42): Enhanced the Ant build to allow specifying `-DskipTests=true` to skip running the tests to allow compilation on non-Windows platforms - [@davidmc24](https://github.com/davidmc24).
* [#42](https://github.com/dblock/waffle/pull/42): Extracted a new "waffle-tests" component out of the existing "waffle-jna" component to remove compile-scope dependency on [mockito](http://code.google.com/p/mockito/) - [@davidmc24](https://github.com/davidmc24).
* [#42](https://github.com/dblock/waffle/pull/42): Added [Maven](http://maven.apache.org/) POMs for the Java components - [@davidmc24](https://github.com/davidmc24).
* Added ContiPerf 2.2.0.
* [#44](https://github.com/dblock/waffle/pull/44): Add pom.xml files to create a .war and deploy demo filter web app to a local Tomcat server - [@bhamail](https://github.com/bhamail).

1.5 (10/19/2012)
================

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
  * slf4j 1.7.2
  * logback 1.0.7
  * Use jcl over slf4j for Spring, as it uses `commons-logging`.
* Jacob-based COM interfaces and implementation have been removed - [@dblock](https://github.com/dblock).
* [#1](https://github.com/dblock/waffle/pull/1): Adjusted logging from info to debug to reduce noise level - [@mcfly83](https://github.com/mcfly83).
* [#17](https://github.com/dblock/waffle/pull/17): JAR manifest information includes specification and implementation details, such as GIT revision - [@ryantxu](https://github.com/ryantxu).
* [#23](https://github.com/dblock/waffle/pull/23) Added `waffle.util.WaffleInfo` which collects system information useful for debugging - [@ryantxu](https://github.com/ryantxu).
* [#28](https://github.com/dblock/waffle/pull/28) Added `waffle-jetty` project.  This lets developers run Waffle directly within the IDE - [@ryantxu](https://github.com/ryantxu).
* [#33](https://github.com/dblock/waffle/pull/33): Added support for servlet3 programmatic login - [@amergey](https://github.com/amergey).
* Upgraded Wix to version 3.7 - [@dblock](https://github.com/dblock).

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
  * tomcat 5.5.36
  * tomcat 6.0.35
  * tomcat 7.0.32
  * guava 13.0.1
  * spring 3.1.2
  * spring 2.5.6.SEC03
  * spring-security 2.0.7
  * spring-security 3.1.2
  * junit 4.10
  * emma 2.1.5320
* Reworked development version to use ivy - [@hazendaz](https://github.com/hazendaz).
  * Removed all third party included jars.
  * Retained tomcat 5.5.36 due to ivy/maven only having 5.5.23 available.
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

