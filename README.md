WAFFLE - Windows Authentication Framework
=========================================

![waffle](https://github.com/dblock/waffle/raw/combined-structure/waffle.jpg)

WAFFLE - Windows Authentication Functional Framework (Light Edition) is a native C# and Java library that does everything Windows authentication (Negotiate, NTLM and Kerberos). It contains packages for .NET and Java and support for most popular web servers. The most popular use case is for those using Tomcat, Jetty or Websphere with an IIS front-end to do Windows authentication - Waffle allows you to completely get rid of IIS.

Essentials
----------

* [Download Version 1.4](https://github.com/downloads/dblock/waffle/Waffle.1.4.zip)
* [Need Help? Google Group](http://groups.google.com/group/waffle-users)
* [Frequently Asked Questions](https://github.com/dblock/waffle/wiki/Frequently-Asked-Questions)
* [Troubleshooting Negotiate](https://github.com/dblock/waffle/wiki/Troubleshooting-Negotiate)
* [Older Versions on CodePlex](http://waffle.codeplex.com/).

Documentation
-------------

There're many semi-independent parts to Waffle. Choose the appropriate documentation.

* Simple native interfaces in C# and Java to do all things Windows authentication. Useful if you're building a custom client that requires Windows authentication. See [Getting Started with WAFFLE API](https://github.com/dblock/waffle/blob/combined-structure/Docs/GettingStartedWithWaffleAPI.md)
* A generic Servlet Negotiate (NTLM and Kerberos) Security Filter that can be used with many web servers, including Tomcat, Jetty and WebSphere. See [documentation](https://github.com/dblock/waffle/blob/combined-structure/Docs/ServletSingleSignOnSecurityFilter.md).
* A Tomcat Negotiate (NTLM and Kerberos) Authenticator Valve, built for the Tomcat Web Container. See [documentation](https://github.com/dblock/waffle/blob/combined-structure/Docs/TomcatMixedSingleSignOnAndFormAuthenticatorValve.md).
* A Tomcat Single Sign-On + Form Authentication Mixed Valve, built for the Tomcat Web Container and allowing users to choose whether to do forms authentication or Windows SSO. See [Documentation](https://github.com/dblock/waffle/blob/combined-structure/Docs/TomcatMixedSingleSignOnAndFormAuthenticatorValve.md).
* A Spring-Security Negotiate (NTLM and Kerberos) Filter. See [Documentation](https://github.com/dblock/waffle/blob/combined-structure/Docs/SpringSecuritySingleSignOnFilter.md).
* A Spring-Security Windows Authentication Manager. See [Documentation](https://github.com/dblock/waffle/blob/combined-structure/Docs/SpringSecurityAuthenticationProvider.md).
* A JAAS Login Module, useful when extending a custom Java client that already implements JAAS to support Windows SSO. See  [Documentation](https://github.com/dblock/waffle/blob/combined-structure/Docs/TomcatWindowsLoginJAASAuthenticator.md).

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
