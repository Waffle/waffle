WAFFLE - Windows Authentication Framework
=========================================

[![Build Status](https://travis-ci.org/Waffle/waffle.svg?branch=master)](https://travis-ci.org/Waffle/waffle)
[![Build status](https://ci.appveyor.com/api/projects/status/8o53n6o359r7s6up?svg=true)](https://ci.appveyor.com/project/hazendaz/waffle)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/2775/badge.svg)](https://scan.coverity.com/projects/2775)
[![Coverage Status](https://coveralls.io/repos/hazendaz/waffle/badge.svg)](https://coveralls.io/r/hazendaz/waffle)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.github.waffle/waffle-jna/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.waffle/waffle-jna)
[![releases](http://github-release-version.herokuapp.com/github/Waffle/waffle/release.svg?style=flat)](https://github.com/Waffle/waffle/releases/tag/waffle-2.0.0)
[![Eclipse](https://img.shields.io/badge/license-Eclipse-blue.svg)](https://www.eclipse.org/legal/epl-v10.html)
[![Project Stats](https://www.openhub.net/p/waffle/widgets/project_thin_badge.gif)](https://www.openhub.net/p/waffle)
[![Github All Releases](https://img.shields.io/github/downloads/Waffle/waffle/total.svg)]()

![waffle](https://github.com/Waffle/waffle/raw/master/waffle.jpg)

WAFFLE is a native Windows Authentication Framework consisting of two C# and Java libraries that perform functions related to Windows authentication, supporting Negotiate, NTLM and Kerberos. Waffle also includes libraries that enable drop-in Windows Single Sign On for popular Java web servers, when running on Windows. While Waffle makes it ridiculously easy to do Windows Authentication in Java, on Windows, Waffle does not work on *nix(UNIX-like).

Unlike many other implementations Waffle on Windows does not require any server-side Kerberos keytab setup, it's a drop-in solution. You can see it in action in [this slightly blurry video](https://www.youtube.com/watch?v=LmTwbOh0hBU) produced for [TeamShatter.com](http://www.teamshatter.com/topics/general/team-shatter-exclusive/securing-java-applications-with-smart-cards-and-single-sign-on/). 

Sites
-----

* [Site Page](https://waffle.github.io/waffle/)
* [sonarqube-java](https://sonarqube.com/dashboard?id=com.github.waffle%3Awaffle-parent)
* [sonarqube-.net](https://sonarqube.com/dashboard/index?id=waffle)

Essentials
----------

* [Download Version 1.9.0](https://github.com/Waffle/waffle/releases/download/waffle-1.9.0/Waffle.1.9.0.zip)
* [Waffle in Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.waffle%22)
* [Waffle Snapshots](https://oss.sonatype.org/content/repositories/snapshots/com/github/waffle/)
* [Get Waffle To Work in Tomcat, Jetty, WebSphere, etc.](Docs/ServletSingleSignOnSecurityFilter.md)
* [Need Help? Google Group](https://groups.google.com/group/waffle-users)
* [Troubleshooting](Docs/Troubleshooting.md)
* [Frequently Asked Questions](Docs/FAQ.md)
* [Older Versions on CodePlex](https://waffle.codeplex.com/).
* [PlatformSDK Security Group](https://groups.google.com/group/microsoft.public.platformsdk.security)

Documentation
-------------

There're several semi-independent parts to Waffle. Choose the appropriate HowTo.

* Simple native interfaces in C# and Java to do all things Windows authentication. Useful if you're building a custom client that requires Windows authentication. See [Getting Started with WAFFLE API](https://github.com/Waffle/waffle/blob/master/Docs/GettingStartedWithWaffleAPI.md)
* A generic Servlet Negotiate (NTLM and Kerberos) Security Filter that can be used with many web servers, including Tomcat, Jetty and WebSphere. See [HowTo](https://github.com/Waffle/waffle/blob/master/Docs/ServletSingleSignOnSecurityFilter.md).
* A Tomcat Negotiate (NTLM and Kerberos) Authenticator Valve, built for the Tomcat Web Container. See [HowTo](https://github.com/Waffle/waffle/blob/master/Docs/tomcat/TomcatSingleSignOnValve.md).
* A Tomcat Single Sign-On + Form Authentication Mixed Valve, built for the Tomcat Web Container and allowing users to choose whether to do form authentication (a username and password sent to the server from a form) or Windows SSO (NTLM or Kerberos). See [HowTo](https://github.com/Waffle/waffle/blob/master/Docs/tomcat/TomcatMixedSingleSignOnAndFormAuthenticatorValve.md).
* A Spring-Security Negotiate (NTLM and Kerberos) Filter. See [HowTo](https://github.com/Waffle/waffle/blob/master/Docs/spring/SpringSecuritySingleSignOnFilter.md).
* A Spring-Security Windows Authentication Manager. See [HowTo](https://github.com/Waffle/waffle/blob/master/Docs/spring/SpringSecurityAuthenticationProvider.md).
* A JAAS Login Module, useful when extending a custom Java client that already implements JAAS to support Windows SSO. See [HowTo](https://github.com/Waffle/waffle/blob/master/Docs/tomcat/TomcatWindowsLoginJAASAuthenticator.md).
* A WildFly Security Domain implementation, offering support for local Windows and Active Directory users authentication when deploying web apps on WildFly servers. See [HowTo](https://github.com/Waffle/waffle/blob/master/Docs/wildfly/WildFlySecurityDomain.md).

Waffle was created and is sponsored by [Application Security Inc.](https://www.trustwave.com/Company/AppSecInc-is-now-Trustwave/). For a long story, read the [Project History](https://github.com/Waffle/waffle/blob/master/HISTORY.md). Also, feel free to use [this PowerPoint presentation](http://www.slideshare.net/dblockdotorg/waffle-at-nycjavasig) from NYJavaSIG to talk about Waffle.

Features
--------

* Account lookup locally and in Active Directory via Win32 API with zero configuration.
* Enumerating Active Directory domains and domain information.
* Returns computer domain / workgroup join information.
* Supports logon for local and domain users returning consistent fully qualified names, identity (SIDs), local and domain groups, including nested.
* Supports all functions required for implementing server-side single-signon with Negotiate and NTLM and various implementations for Java web servers.
* Supports Windows Identity impersonation.
* Includes a Windows Installer Merge Module for distribution of C# binaries.

Related and Similar Products
----------------------------

* [Cross-Platform SPNEGO](http://spnego.sourceforge.net/)
* [Tomcat SPNEGO](https://tomcatspnego.codeplex.com/)
* [Quest Vintela Single-Sign-On](http://www.quest.com/single-sign-on-for-java/) (Commercial)
* [IOPlex Jespa](https://www.ioplex.com/) (Commercial)
* [Josso](http://www.josso.org/) (Commercial)

Contributing
------------

* Fork the project.
* [Set Up a Development Environment](Docs/SettingUpDevelopmentEnvironment.md).
* Make your code changes. Don't forget tests.
* Update [CHANGELOG](CHANGELOG.md).
* Make pull requests. Bonus points for topic branches. 

License and Copyright
---------------------

Copyright (c) [Application Security Inc.](https://www.trustwave.com/Company/AppSecInc-is-now-Trustwave/), 2010-2017 and Contributors. 

This project is licensed under the [Eclipse Public License](https://github.com/Waffle/waffle/blob/master/LICENSE).

Project maintained by [Daniel Doubrovkine](https://github.com/dblock) & [Jeremy Landis](https://github.com/hazendaz).
