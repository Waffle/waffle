# NTLM fails with an Apache / AJP front-end
----

## Question
This is Fiddler's HTTP trace of beginning one of my succesfull conversations:
[FiddlerLog](AJP_attach1.txt)

As I understand all authentication negotiations should be performed in requests 1), 2) and 3). What might cause IE8 sent in 6) request that NTLM Authorization header?

Sometimes, our AJAX requests (js, images, css) leads to login dialog popup (HTTP trace one of the situation is above).

I've checked Tomcat's log (below). Here two threads (-0.0.0.0-8009-5 and -0.0.0.0-8009-2) operates on behalf of the same connection id (10.77.252.232:-1). Is it possible that I got concurrency issue here?

```
2011-02-03 18:24:00,154 INFO  (-0.0.0.0-8009-2) [ru.massmo.common.security.authentication.waffle.NegotiateSecurityFilter] GET /t2wd/zkau/web/a5f28083/zul/img/wnd/wnd-corner.png, contentlength: 0
2011-02-03 18:24:00,154 INFO  (-0.0.0.0-8009-2) [waffle.servlet.spi.NegotiateSecurityFilterProvider] security package: NTLM, connection id: 10.77.252.232:-1
2011-02-03 18:24:00,154 INFO  (-0.0.0.0-8009-2) [waffle.servlet.spi.NegotiateSecurityFilterProvider] token buffer: 40 byte(s)
2011-02-03 18:24:00,154 INFO  (-0.0.0.0-8009-5) [ru.massmo.common.security.authentication.waffle.NegotiateSecurityFilter] GET /t2wd/zkau/web/a5f28083/zul/img/wnd/wnd-hl.png, contentlength: 0
2011-02-03 18:24:00,154 INFO  (-0.0.0.0-8009-5) [waffle.servlet.spi.NegotiateSecurityFilterProvider] security package: NTLM, connection id: 10.77.252.232:-1
2011-02-03 18:24:00,154 INFO  (-0.0.0.0-8009-5) [waffle.servlet.spi.NegotiateSecurityFilterProvider] token buffer: 40 byte(s)
2011-02-03 18:24:00,154 WARN  (-0.0.0.0-8009-2) [ru.massmo.common.security.authentication.waffle.NegotiateSecurityFilter] Cant login user by header: NTLM TlRMTVNTUAABAAAAB4IIogAAAAAAAAAAAAAAAAAAAAAGAbAdAAAADw==
com.sun.jna.platform.win32.Win32Exception: The token supplied to the function is invalid

	at waffle.windows.auth.impl.WindowsAuthProviderImpl.acceptSecurityToken(Unknown Source)
	at waffle.servlet.spi.NegotiateSecurityFilterProvider.doFilter(Unknown Source)
	at waffle.servlet.spi.SecurityFilterProviderCollection.doFilter(Unknown Source)
	at ru.massmo.common.security.authentication.waffle.NegotiateSecurityFilter.doFilter(NegotiateSecurityFilter.java:74)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:380)
	at org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.doFilter(AbstractAuthenticationProcessingFilter.java:187)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:380)
	at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:105)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:380)
	at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:79)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:380)
	at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:169)
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:237)
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:167)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
	at org.jboss.web.tomcat.filters.ReplyHeaderFilter.doFilter(ReplyHeaderFilter.java:96)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:235)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:191)
	at org.jboss.web.tomcat.security.SecurityAssociationValve.invoke(SecurityAssociationValve.java:183)
	at org.jboss.web.tomcat.security.JaccContextValve.invoke(JaccContextValve.java:95)
	at org.jboss.web.tomcat.security.SecurityContextEstablishmentValve.process(SecurityContextEstablishmentValve.java:126)
	at org.jboss.web.tomcat.security.SecurityContextEstablishmentValve.invoke(SecurityContextEstablishmentValve.java:70)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:127)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:102)
	at org.jboss.web.tomcat.service.jca.CachedConnectionValve.invoke(CachedConnectionValve.java:158)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:109)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:330)
	at org.apache.coyote.ajp.AjpAprProcessor.process(AjpAprProcessor.java:419)
	at org.apache.coyote.ajp.AjpAprProtocol$AjpConnectionHandler.process(AjpAprProtocol.java:402)
	at org.apache.tomcat.util.net.AprEndpoint$Worker.run(AprEndpoint.java:2036)
	at java.lang.Thread.run(Thread.java:619)
2011-02-03 18:24:00,154 DEBUG (-0.0.0.0-8009-2) [ru.massmo.common.security.authentication.waffle.NegotiateSecurityFilter] sendUnauthorized(close=true)
2011-02-03 18:24:00,154 DEBUG (-0.0.0.0-8009-2) [org.springframework.security.web.context.HttpSessionSecurityContextRepository] SecurityContext is empty or anonymous - context will not be stored in HttpSession. 
2011-02-03 18:24:00,154 DEBUG (-0.0.0.0-8009-2) [org.springframework.security.web.context.SecurityContextPersistenceFilter] SecurityContextHolder now cleared, as request processing completed
2011-02-03 18:24:00,154 WARN  (-0.0.0.0-8009-5) [ru.massmo.common.security.authentication.waffle.NegotiateSecurityFilter] Cant login user by header: NTLM TlRMTVNTUAABAAAAB4IIogAAAAAAAAAAAAAAAAAAAAAGAbAdAAAADw==
com.sun.jna.platform.win32.Win32Exception: The handle specified is invalid

	at waffle.windows.auth.impl.WindowsSecurityContextImpl.dispose(Unknown Source)
	at waffle.windows.auth.impl.WindowsAuthProviderImpl.acceptSecurityToken(Unknown Source)
	at waffle.servlet.spi.NegotiateSecurityFilterProvider.doFilter(Unknown Source)
	at waffle.servlet.spi.SecurityFilterProviderCollection.doFilter(Unknown Source)
	at ru.massmo.common.security.authentication.waffle.NegotiateSecurityFilter.doFilter(NegotiateSecurityFilter.java:74)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:380)
	at org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.doFilter(AbstractAuthenticationProcessingFilter.java:187)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:380)
	at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:105)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:380)
	at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:79)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:380)
	at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:169)
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:237)
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:167)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
	at org.jboss.web.tomcat.filters.ReplyHeaderFilter.doFilter(ReplyHeaderFilter.java:96)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:235)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:191)
	at org.jboss.web.tomcat.security.SecurityAssociationValve.invoke(SecurityAssociationValve.java:183)
	at org.jboss.web.tomcat.security.JaccContextValve.invoke(JaccContextValve.java:95)
	at org.jboss.web.tomcat.security.SecurityContextEstablishmentValve.process(SecurityContextEstablishmentValve.java:126)
	at org.jboss.web.tomcat.security.SecurityContextEstablishmentValve.invoke(SecurityContextEstablishmentValve.java:70)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:127)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:102)
	at org.jboss.web.tomcat.service.jca.CachedConnectionValve.invoke(CachedConnectionValve.java:158)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:109)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:330)
	at org.apache.coyote.ajp.AjpAprProcessor.process(AjpAprProcessor.java:419)
	at org.apache.coyote.ajp.AjpAprProtocol$AjpConnectionHandler.process(AjpAprProtocol.java:402)
	at org.apache.tomcat.util.net.AprEndpoint$Worker.run(AprEndpoint.java:2036)
	at java.lang.Thread.run(Thread.java:619)
2011-02-03 18:24:00,154 DEBUG (-0.0.0.0-8009-5) [ru.massmo.common.security.authentication.waffle.NegotiateSecurityFilter] sendUnauthorized(close=true)
2011-02-03 18:24:00,154 DEBUG (-0.0.0.0-8009-5) [org.springframework.security.web.context.HttpSessionSecurityContextRepository] SecurityContext is empty or anonymous - context will not be stored in HttpSession. 
2011-02-03 18:24:00,154 DEBUG (-0.0.0.0-8009-5) [org.springframework.security.web.context.SecurityContextPersistenceFilter] SecurityContextHolder now cleared, as request processing completed
```

I.e this two different requests from the same client:

GET /t2wd/zkau/web/a5f28083/zul/img/wnd/wnd-corner.png
GET /t2wd/zkau/web/a5f28083/zul/img/wnd/wnd-hl.png
make race condition for WindowsAuthProviderImpl._continueContexts and Ntlm session?

## Answer

> It's possible that the protocol chosen is NTLM, in which case this is a connection-oriented authentication an not session-oriented one. So before 6) (say in 5)) the browser decided to open a new TCP/IP connection, which required a new handshake. A netmon trace would show that.

> This looks wrong. The connection id shouldn't be -1 - it means that the server couldn't figure out what port the connection is on. That's how WAFFLE distinguishes between connections, so this can't work. Are you looking at the code? Looks like request.getRemotePort() is not returning a number.

We are behind Apache. Found this in Apache-Tomcat's connector doc (http://tomcat.apache.org/connectors-doc/generic_howto/proxy.html):

The remote port number has been forgotten in the AJP13 protocol.

> So getRemotePort() will incorrectly return 0 or -1. As a workaround you can forward the remote port by setting JkEnvVar REMOTE_PORT and then either using request.getAttribute("REMOTE_PORT") instead of getRemotePort() or wrapping the request in a filter and overriding getRemotePort() with request.getAttribute("REMOTE_PORT"). Recent versions of Tomcat might automatically respect the forwarded attribute REMOTE_PORT.

Ok, we will try mentioned workaround.

Arhhh! Now I see that I got first request after commence on 61548 port and next request that should answer on challenge from first request on another 61549 port!

```
2011-02-04 16:51:53,048 DEBUG (  TP-Processor3) [rityFilterEntryPoint] [waffle.spring.NegotiateEntryPoint] commence
2011-02-04 16:51:53,050 DEBUG (  TP-Processor3) [ityContextRepository] SecurityContext is empty or anonymous - context will not be stored in HttpSession. 
2011-02-04 16:51:53,050 DEBUG (  TP-Processor3) [extPersistenceFilter] SecurityContextHolder now cleared, as request processing completed
2011-02-04 16:51:53,070 DEBUG (  TP-Processor3) [web.FilterChainProxy] Converted URL to lowercase, from: '/index.jsp'; to: '/index.jsp'
2011-02-04 16:51:53,070 DEBUG (  TP-Processor3) [web.FilterChainProxy] Candidate is: '/index.jsp'; pattern is /**; matched=true
2011-02-04 16:51:53,071 DEBUG (  TP-Processor3) [web.FilterChainProxy] /index.jsp at position 1 of 8 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
2011-02-04 16:51:53,071 DEBUG (  TP-Processor3) [ityContextRepository] HttpSession returned null object for SPRING_SECURITY_CONTEXT
2011-02-04 16:51:53,071 DEBUG (  TP-Processor3) [ityContextRepository] No SecurityContext was available from the HttpSession: org.apache.catalina.session.StandardSessionFacade@55d7fc31. A new one will be created.
2011-02-04 16:51:53,071 DEBUG (  TP-Processor3) [web.FilterChainProxy] /index.jsp at position 2 of 8 in additional filter chain; firing Filter: 'NegotiateSecurityFilter'
2011-02-04 16:51:53,071 INFO  (  TP-Processor3) [otiateSecurityFilter] GET /waffle-spring-filter/, contentlength: 0
2011-02-04 16:51:53,073 DEBUG (  TP-Processor3) [curityFilterProvider] security package: NTLM, connection id: 10.77.252.227:61548
2011-02-04 16:51:53,075 DEBUG (  TP-Processor3) [curityFilterProvider] token buffer: 57 byte(s)
2011-02-04 16:51:53,221 DEBUG (  TP-Processor3) [curityFilterProvider] continue token: TlRMT....
2011-02-04 16:51:53,222 DEBUG (  TP-Processor3) [curityFilterProvider] continue required: true
2011-02-04 16:51:53,222 DEBUG (  TP-Processor3) [ityContextRepository] SecurityContext is empty or anonymous - context will not be stored in HttpSession. 
2011-02-04 16:51:53,222 DEBUG (  TP-Processor3) [extPersistenceFilter] SecurityContextHolder now cleared, as request processing completed
2011-02-04 16:51:53,235 DEBUG (  TP-Processor3) [web.FilterChainProxy] Converted URL to lowercase, from: '/index.jsp'; to: '/index.jsp'
2011-02-04 16:51:53,235 DEBUG (  TP-Processor3) [web.FilterChainProxy] Candidate is: '/index.jsp'; pattern is /**; matched=true
2011-02-04 16:51:53,235 DEBUG (  TP-Processor3) [web.FilterChainProxy] /index.jsp at position 1 of 8 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
2011-02-04 16:51:53,235 DEBUG (  TP-Processor3) [ityContextRepository] HttpSession returned null object for SPRING_SECURITY_CONTEXT
2011-02-04 16:51:53,236 DEBUG (  TP-Processor3) [ityContextRepository] No SecurityContext was available from the HttpSession: org.apache.catalina.session.StandardSessionFacade@55d7fc31. A new one will be created.
2011-02-04 16:51:53,236 DEBUG (  TP-Processor3) [web.FilterChainProxy] /index.jsp at position 2 of 8 in additional filter chain; firing Filter: 'NegotiateSecurityFilter'
2011-02-04 16:51:53,236 INFO  (  TP-Processor3) [otiateSecurityFilter] GET /waffle-spring-filter/, contentlength: 0
2011-02-04 16:51:53,236 DEBUG (  TP-Processor3) [curityFilterProvider] security package: NTLM, connection id: 10.77.252.227:61549
2011-02-04 16:51:53,236 DEBUG (  TP-Processor3) [curityFilterProvider] token buffer: 88 byte(s)
2011-02-04 16:51:53,281 WARN  (  TP-Processor3) [otiateSecurityFilter] error logging in user: The token supplied to the function is invalid

2011-02-04 16:51:53,281 DEBUG (  TP-Processor3) [ityContextRepository] SecurityContext is empty or anonymous - context will not be stored in HttpSession. 
2011-02-04 16:51:53,282 DEBUG (  TP-Processor3) [extPersistenceFilter] SecurityContextHolder now cleared, as request processing completed
```

So user never passes authentication!


> This has to remain on a keep-alive connection, that's just what the protocol requires. The server should be saying Connection: Keep-alive in the right places and Connection: close in the others. I don't know how to tell AJP to respect that. Why are you using AJP anyway?

Problem was in that our Apache closed all requests. Described here: http://httpd.apache.org/docs/2.0/ssl/ssl_faq.html, "Why do I get I/O errors when connecting via HTTPS to an Apache+mod_ssl server with Microsoft Internet Explorer (MSIE)?"

There is a default Apache settings to better support IE<=5.5 that disables keepalives in SSL for all MSIE browsers. It is there for years and usually doesn't break anything. Removing it has elimenated the problem.

```
SetEnvIf User-Agent ".*MSIE.*" \
nokeepalive ssl-unclean-shutdown \
downgrade-1.0 force-response-1.0
```
