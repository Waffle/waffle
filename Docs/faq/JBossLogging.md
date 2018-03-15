# Cannot find where to enable WAFFLE logging in JBoss
----

## Question
How to set logging level on JBoss?
Can anyone tell me how to control Waffle logging on JBoss? All I can find are log4j settings.

## Answer
> Isn't that the same as on any other server? I mean you modify log4j logging but in some central location? Does [this page](https://developer.jboss.org/wiki/logging) help?

Yep, got it, thank you! I found log4j.xml in my application's directories and added this category:
```xml
<category name="waffle">
    <priority value="debug"/>
</category>
```
Now my application log contains entries like this:

```
INFO  NegotiateSecurityFilter []: GET /myApp/secure/jsf/openProject.jsf, contentlength: -1
INFO  NegotiateSecurityFilter []: authorization required
INFO  NegotiateSecurityFilter []: GET /myApp/secure/jsf/openProject.jsf, contentlength: -1
INFO  NegotiateSecurityFilterProvider []: security package: Negotiate, connection id: 123.45.6.789:4320
INFO  NegotiateSecurityFilterProvider []: token buffer: 1453 byte(s)
INFO  NegotiateSecurityFilterProvider []: continue token: oX8wfaADCgEBoQsGCSqGS...
INFO  NegotiateSecurityFilterProvider []: continue required: true
```
