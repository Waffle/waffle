Troubleshooting
===============

This is a step-by-step guide an Q&A on troubleshooting Negotiate authentication.

Browser Configuration
------------------------

Make sure the browser is configured to support Negotiage.  See: 
[Configuring Browsers (IE/Firefox)](ConfiguringBrowsers.md)



Troubleshooting Kerberos
------------------------

Typical configurations to check are:

1. The `application` is running as a `service`
1. The `service` is running as a `user` on the same domain as the `machine`
1. The `user` has privileges for Kerberos delegation

To check the current privileges, run:
```
setspn -L username
```

To add privileges for the current user, run
```
setspn -A PROTOCOL/machine:port username
```

Useful Troubleshooting Resources:
* [Enabling Kerberos Logging](https://support.microsoft.com/kb/262177/en-us)
* [Troubleshooting Kerberos Delegation](http://web.archive.org/web/20141226210813/https://www.microsoft.com/en-us/download/confirmation.aspx?id=4754) (Word Document, 1.5M, Microsoft Corporation, March 2004)



Troubleshooting NTLM
--------------------

* [Enabling NTLM Logging](https://blogs.technet.com/b/askds/archive/2009/10/08/ntlm-blocking-and-you-application-analysis-and-auditing-methodologies-in-windows-7.aspx)



Troubleshooting WAFFLE
----------------------

* See [Frequently Asked Questions](FAQ.md).


Still Need Help?
----------------

With new versions of Internet Explorer, Firefox or Chrome, use developer tools.

With older versions of Internet Explorer, trace the HTTP request/response.

1. Download and install [IEHttpHeaders](https://www.blunck.info/iehttpheaders.html).
2. Choose _Tools_, Display _IEHttpHeaders_.
3. Make one request that ends up in a popup or failure.
4. Copy the entire HTTP conversation.

Post the http conversation with your question to the [Waffle Users Google Group](https://groups.google.com/group/waffle-users).

