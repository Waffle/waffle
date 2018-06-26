# Negotiate tries, but keeps failing with 401
----

## Question
When I hit the waffle-filter demo from the localhost using http://localhost:8080/waffle-filter all works well.  When I try it from a different system using the domain name of the host negotiate fails.  I've tried disabling negotiate and using only NTLM which works.  Below is the ieHTTPHeaders output from a single failed request to waffle-filter with Negotiate enabled.  Any suggestions or help would be tremendously appreciated.

## Answer
It was an SPN issue.  I needed to create an SPN for the user under which tomcat was running:

```
> setspn -A HTTP/<server-fqdn> <user_tomcat_running_under>
```