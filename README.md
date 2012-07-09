WAFFLE - Windows Authentication Framework
=========================================

![waffle](https://github.com/dblock/waffle/raw/combined-structure/waffle.jpg)

WAFFLE - Windows Authentication Functional Framework (Light Edition) is a native C# and Java library that does everything Windows authentication (Negotiate, NTLM and Kerberos).

Essentials
----------

* [Download Version 1.4](https://github.com/downloads/dblock/waffle/Waffle.1.4.zip)
* [Need Help? Google Group](http://groups.google.com/group/waffle-users)
* [Frequently Asked Questions](https://github.com/dblock/waffle/wiki/Frequently-Asked-Questions)
* [Troubleshooting Negotiate](https://github.com/dblock/waffle/wiki/Troubleshooting-Negotiate)
* [Older Versions on CodePlex](http://waffle.codeplex.com/).

Short Story
-----------

Most people will be interested in one of the following.

* Simple native interfaces in C# and Java to do all things Windows authentication.
* A generic Servlet Negotiate (NTLM and Kerberos) Security Filter - [Docs](https://github.com/dblock/waffle/blob/combined-structure/Docs/ServletSingleSignOnSecurityFilter.md) and an older [Tutorial](http://code.dblock.org/ShowPost.aspx?id=106).
* A Tomcat Negotiate (NTLM and Kerberos) Authenticator Valve - [Docs](https://github.com/dblock/waffle/blob/combined-structure/Docs/TomcatMixedSingleSignOnAndFormAuthenticatorValve.md) and an older [Tutorial](http://code.dblock.org/ShowPost.aspx?id=103).
* A Tomcat Single Sign-On + Form Authentication Mixed Valve - [Docs](https://github.com/dblock/waffle/blob/combined-structure/Docs/TomcatMixedSingleSignOnAndFormAuthenticatorValve.md) and an older [Tutorial](http://code.dblock.org/ShowPost.aspx?id=107).
* A Spring-Security Negotiate (NTLM and Kerberos) Filter - [Docs](https://github.com/dblock/waffle/blob/combined-structure/Docs/SpringSecuritySingleSignOnFilter.md) and an older [Totorial](http://code.dblock.org/ShowPost.aspx?id=114).
* A Spring-Security Windows Authentication Manager - [Docs](https://github.com/dblock/waffle/blob/combined-structure/Docs/SpringSecurityAuthenticationProvider.md)
* A JAAS Login Module - [Docs](https://github.com/dblock/waffle/blob/combined-structure/Docs/TomcatWindowsLoginJAASAuthenticator.md) and an older [Tutorial](http://code.dblock.org/ShowPost.aspx?id=105).
* If you're using Tomcat, Jetty or Websphere with an IIS front-end to do authentication only, Waffle will allow you to get rid of IIS.

Unlike many other implementations WAFFLE on Windows does not usually require any server-side Kerberos keytab setup, it's a drop-in solution. You can see it in action in [this slightly blurry video](http://www.youtube.com/watch?v=LmTwbOh0hBU) produced for [TeamShatter.com](http://www.teamshatter.com/topics/general/team-shatter-exclusive/securing-java-applications-with-smart-cards-and-single-sign-on/). 

Waffle was created and is sponsored by [Application Security Inc.](http://www.appsecinc.com/) For a long story, read the [Project History](https://github.com/dblock/waffle/blob/combined-structure/HISTORY.md). Also, feel free to use [this PowerPoint presentation](http://www.slideshare.net/dblockdotorg/waffle-at-nycjavasig) from NYJavaSIG.

Features
--------

* Account lookup locally and in Active Directory via Win32 API with zero configuration.
* Enumerating Active Directory domains and domain information.
* Returns computer domain / workgroup join information.
* Supports logon for local and domain users returning consistent fully qualified names, identity (SIDs), local and domain groups, including nested.
* Supports all functions required for implementing server-side single-signon with Negotiate and NTLM and various implementations for Java web servers.
* Supports Windows Identity impersonation.
* Includes a Windows Installer Merge Module for distribution of C# binaries.

Branches
--------

Waffle includes a servlet filter that works with any servlet container, including Tomcat, Jetty and Websphere. It also contains a native package for Tomcat 6 and Spring Security 3. There're branches for [Tomcat 5](https://github.com/dblock/waffle/tree/tomcat5), [Tomcat 7](https://github.com/dblock/waffle/tree/tomcat7) as well as [Spring Security 2](https://github.com/dblock/waffle/tree/spring-security-2) support.

Related and Simiar Products
---------------------------

* [Quest Vintella Single-Sign-On](http://www.quest.com/single-sign-on-for-java/) (Commercial)
* [IOPlex Jespa](http://www.ioplex.com/) (Commercial)
* [Josso](http://www.josso.org/confluence/display/JOSSO1/JOSSO+-+Java+Open+Single+Sign-On+Project+Home)
* [Tomcat SPNEGO](http://tomcatspnego.codeplex.com/)

License and Copyright
---------------------

Copyright (c) Application Security Inc. and Contributors.

This project is licensed under the [Eclipse Public License](https://github.com/dblock/waffle/blob/combined-structure/LICENSE).

