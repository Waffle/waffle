WAFFLE - Windows Authentication Framework
=========================================

[![Build Status](https://travis-ci.org/dblock/waffle.svg?branch=master)](https://travis-ci.org/dblock/waffle)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/2775/badge.svg)](https://scan.coverity.com/projects/2775)
[![Coverage Status](https://coveralls.io/repos/hazendaz/waffle/badge.png)](https://coveralls.io/r/hazendaz/waffle)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.github.dblock.waffle/waffle-jna/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.dblock.waffle/waffle-jna)
[![Eclipse](http://img.shields.io/badge/license-Eclipse-blue.svg)](https://www.eclipse.org/legal/epl-v10.html)

![waffle](https://github.com/dblock/waffle/raw/master/waffle.jpg)

WAFFLE is a native Windows Authentication Framework consisting of two C# and Java libraries that perform functions related to Windows authentication, supporting Negotiate, NTLM and Kerberos. Waffle also includes libraries that enable drop-in Windows Single Sign On for popular Java web servers, when running on Windows. While Waffle makes it ridiculously easy to do Windows Authentication in Java, on Windows, Waffle does not work on *nix.

Unlike many other implementations Waffle on Windows does not require any server-side Kerberos keytab setup, it's a drop-in solution. You can see it in action in [this slightly blurry video](http://www.youtube.com/watch?v=LmTwbOh0hBU) produced for [TeamShatter.com](http://www.teamshatter.com/topics/general/team-shatter-exclusive/securing-java-applications-with-smart-cards-and-single-sign-on/). 

Essentials
----------

* [Download Version 1.7.4](https://github.com/dblock/waffle/releases/download/Waffle-1.7.4/Waffle.1.7.4.zip)
* [Waffle in Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.dblock.waffle%22)
* [Waffle Snapshots](https://oss.sonatype.org/content/repositories/snapshots/com/github/dblock/waffle/)
* [Get Waffle To Work in Tomcat, Jetty, WebSphere, etc.](Docs/ServletSingleSignOnSecurityFilter.md)
* [Need Help? Google Group](http://groups.google.com/group/waffle-users)
* [Troubleshooting](Docs/Troubleshooting.md)
* [Frequently Asked Questions](Docs/FAQ.md)
* [Older Versions on CodePlex](http://waffle.codeplex.com/).
* [PlatformSDK Security Group](https://groups.google.com/group/microsoft.public.platformsdk.security)

Legacy 1.7.x Branch
-------------------
Waffle legacy support.  From 1/3/2015 through 1/1/2016 we will continue to support the 1.7.x branch for any bug fixes to
legacy items.

Legacy is defined as...

* Spring 2
* Tomcat 5
* Java 6

Documentation
-------------

There're several semi-independent parts to Waffle. Choose the appropriate HowTo.

* Simple native interfaces in C# and Java to do all things Windows authentication. Useful if you're building a custom client that requires Windows authentication. See [Getting Started with WAFFLE API](https://github.com/dblock/waffle/blob/master/Docs/GettingStartedWithWaffleAPI.md)
* A generic Servlet Negotiate (NTLM and Kerberos) Security Filter that can be used with many web servers, including Tomcat, Jetty and WebSphere. See [HowTo](https://github.com/dblock/waffle/blob/master/Docs/ServletSingleSignOnSecurityFilter.md).
* A Tomcat Negotiate (NTLM and Kerberos) Authenticator Valve, built for the Tomcat Web Container. See [HowTo](https://github.com/dblock/waffle/blob/master/Docs/tomcat/TomcatSingleSignOnValve.md).
* A Tomcat Single Sign-On + Form Authentication Mixed Valve, built for the Tomcat Web Container and allowing users to choose whether to do form authentication (a username and password sent to the server from a form) or Windows SSO (NTLM or Kerberos). See [HowTo](https://github.com/dblock/waffle/blob/master/Docs/tomcat/TomcatMixedSingleSignOnAndFormAuthenticatorValve.md).
* A Spring-Security Negotiate (NTLM and Kerberos) Filter. See [HowTo](https://github.com/dblock/waffle/blob/master/Docs/spring/SpringSecuritySingleSignOnFilter.md).
* A Spring-Security Windows Authentication Manager. See [HowTo](https://github.com/dblock/waffle/blob/master/Docs/spring/SpringSecurityAuthenticationProvider.md).
* A JAAS Login Module, useful when extending a custom Java client that already implements JAAS to support Windows SSO. See [HowTo](https://github.com/dblock/waffle/blob/master/Docs/tomcat/TomcatWindowsLoginJAASAuthenticator.md).

Waffle was created and is sponsored by [Application Security Inc.](http://www.appsecinc.com/). For a long story, read the [Project History](https://github.com/dblock/waffle/blob/master/HISTORY.md). Also, feel free to use [this PowerPoint presentation](http://www.slideshare.net/dblockdotorg/waffle-at-nycjavasig) from NYJavaSIG to talk about Waffle.

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
* [Tomcat SPNEGO](http://tomcatspnego.codeplex.com/)
* [Quest Vintela Single-Sign-On](http://www.quest.com/single-sign-on-for-java/) (Commercial)
* [IOPlex Jespa](http://www.ioplex.com/) (Commercial)
* [Josso](http://www.josso.org/confluence/display/JOSSO1/JOSSO+-+Java+Open+Single+Sign-On+Project+Home)

Contributing
------------

* Fork the project.
* [Set Up a Development Environment](Docs/SettingUpDevelopmentEnvironment.md).
* Make your code changes. Don't forget tests.
* Update [CHANGELOG](CHANGELOG.md).
* Make pull requests. Bonus points for topic branches. 

Sonar Results
-------------

Current status of sonar results for Waffle.

![sonar-results](Docs/imgs/sonar-results.png)

License and Copyright
---------------------

Copyright (c) [Application Security Inc.](http://www.appsecinc.com), 2010-2015 and Contributors. 

This project is licensed under the [Eclipse Public License](https://github.com/dblock/waffle/blob/master/LICENSE).

Project maintained by [Daniel Doubrovkine](https://github.com/dblock) & [Jeremy Landis](https://github.com/hazendaz).
