# Password prompt instead of SSO
----

# Question

I am using waffle negotiate authenticator.

When I run tomcat on localhost. Silent authentication works fine.

When I try to access the application from another computer, I get prompted for user and password.

IE. when i try to access mycomputer:8080/mywebapp from mycomputer I can get in to the application and everything works fine

when I try to access the same link from myothercomputer I get the standard IE prompt saying: "Connecting to mycomputer.domain.com" and prompts me for user and password.

If this helps, when I hit cancel I get redirected to a 401 error page.

I have tried running tomcat with both a local user and a domain admin but I keep getting the same error.

# Answer
> Check browser settings and that the destination is in the intranet.
> Post HTTP trace logs.
> Post server-side logs.

I was using eclipse to start/stop tomcat.

Once I started Tomcat as a service rather than through eclipse everything worked fine