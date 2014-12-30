How can I prevent the browser from showing a login popup on failed authentication?
==================================================================================

There's no way to do this, because in many cases the client shows the login dialog, not the server. 

Negotiate is a multi-step protocol, in which the client and the server exchange messages many times. SSO fails, and the client eventually decides to give up and pops up the message. If this is unexpected, go through the [troubleshooting steps](../Troubleshooting.md).

For legitimate scenarios where SSO is not possible, let users decide whether they want to do SSO or login with a form using a [mixed authenticator](../tomcat/TomcatMixedSingleSignOnAndFormAuthenticatorValve.md).
