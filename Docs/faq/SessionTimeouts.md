# java.lang.IllegalStateException
----

## Question
I'm seeing a really strange problem in a Spring webapp (2.5) with Spring Security 2.0.6 and a back-ported Waffle 1.4.

The symptom is, that JSP pages render incomplete. The exception I'm seeing is this:

```
Nov 30, 2011 12:04:59 PM org.apache.jasper.runtime.JspFactoryImpl internalGetPageContext
SEVERE: Exception initializing page context
java.lang.IllegalStateException: Cannot create a session after the response has been committed
	at org.apache.catalina.connector.Request.doGetSession(Request.java:2381)
	at org.apache.catalina.connector.Request.getSession(Request.java:2098)
	at org.apache.catalina.connector.RequestFacade.getSession(RequestFacade.java:833)
	at javax.servlet.http.HttpServletRequestWrapper.getSession(HttpServletRequestWrapper.java:216)
	at org.apache.catalina.core.ApplicationHttpRequest.getSession(ApplicationHttpRequest.java:544)
	at javax.servlet.http.HttpServletRequestWrapper.getSession(HttpServletRequestWrapper.java:216)
	at org.apache.catalina.core.ApplicationHttpRequest.getSession(ApplicationHttpRequest.java:544)
	at org.apache.catalina.core.ApplicationHttpRequest.getSession(ApplicationHttpRequest.java:493)
	at javax.servlet.http.HttpServletRequestWrapper.getSession(HttpServletRequestWrapper.java:224)
	at javax.servlet.http.HttpServletRequestWrapper.getSession(HttpServletRequestWrapper.java:224)
	at org.apache.jasper.runtime.PageContextImpl._initialize(PageContextImpl.java:146)
	at org.apache.jasper.runtime.PageContextImpl.initialize(PageContextImpl.java:124)
	at org.apache.jasper.runtime.JspFactoryImpl.internalGetPageContext(JspFactoryImpl.java:107)
	at org.apache.jasper.runtime.JspFactoryImpl.getPageContext(JspFactoryImpl.java:63)
	at org.apache.jsp.WEB_002dINF.templates.ThreeColumnPage_right_jsp._jspService(ThreeColumnPage_right_jsp.java:58)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:70)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:717)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:377)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:313)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:260)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:717)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:290)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
	at org.apache.catalina.core.ApplicationDispatcher.invoke(ApplicationDispatcher.java:646)
	at org.apache.catalina.core.ApplicationDispatcher.doInclude(ApplicationDispatcher.java:551)
	at org.apache.catalina.core.ApplicationDispatcher.include(ApplicationDispatcher.java:488)
	at com.coremedia.objectserver.web.WebappResourceView.render(WebappResourceView.java:93)
	at com.coremedia.objectserver.web.WebappResourceView.render(WebappResourceView.java:130)
	at com.coremedia.objectserver.web.ViewUtils.render(ViewUtils.java:113)
	at com.coremedia.objectserver.web.ViewUtils.render(ViewUtils.java:76)
	at com.coremedia.objectserver.web.ViewUtils.render(ViewUtils.java:142)
	at de.tchibo.intra.webtier.taglibs.IncludeTag20.doEndTag(IncludeTag20.java:64)
	at org.apache.jsp.WEB_002dINF.templates.Startpage_jsp._jspx_meth_utils_005finclude_005f5(Startpage_jsp.java:345)
	at org.apache.jsp.WEB_002dINF.templates.Startpage_jsp._jspService(Startpage_jsp.java:129)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:70)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:717)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:377)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:313)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:260)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:717)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:290)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
	at org.apache.catalina.core.ApplicationDispatcher.invoke(ApplicationDispatcher.java:646)
	at org.apache.catalina.core.ApplicationDispatcher.processRequest(ApplicationDispatcher.java:436)
	at org.apache.catalina.core.ApplicationDispatcher.doForward(ApplicationDispatcher.java:374)
	at org.apache.catalina.core.ApplicationDispatcher.forward(ApplicationDispatcher.java:302)
	at com.coremedia.objectserver.web.WebappResourceView.render(WebappResourceView.java:95)
	at com.coremedia.objectserver.web.ViewUtils.render(ViewUtils.java:99)
	at com.coremedia.objectserver.web.ViewUtils.render(ViewUtils.java:67)
	at com.coremedia.objectserver.web.BeanView.render(BeanView.java:64)
	at com.coremedia.objectserver.web.BeanView.renderMergedOutputModel(BeanView.java:47)
	at org.springframework.web.servlet.view.AbstractView.render(AbstractView.java:257)
	at org.springframework.web.servlet.DispatcherServlet.render(DispatcherServlet.java:1183)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:902)
	at com.coremedia.objectserver.web.DispatcherServlet.doDispatch(DispatcherServlet.java:56)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:807)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:571)
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:501)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:617)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:717)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:290)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
	at org.springframework.security.util.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:394)
	at org.springframework.security.intercept.web.FilterSecurityInterceptor.invoke(FilterSecurityInterceptor.java:109)
	at org.springframework.security.intercept.web.FilterSecurityInterceptor.doFilter(FilterSecurityInterceptor.java:83)
	at org.springframework.security.util.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:406)
	at org.springframework.security.ui.SessionFixationProtectionFilter.doFilterHttp(SessionFixationProtectionFilter.java:67)
	at org.springframework.security.ui.SpringSecurityFilter.doFilter(SpringSecurityFilter.java:53)
	at org.springframework.security.util.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:406)
	at org.springframework.security.ui.ExceptionTranslationFilter.doFilterHttp(ExceptionTranslationFilter.java:101)
	at org.springframework.security.ui.SpringSecurityFilter.doFilter(SpringSecurityFilter.java:53)
	at org.springframework.security.util.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:406)
	at org.springframework.security.wrapper.SecurityContextHolderAwareRequestFilter.doFilterHttp(SecurityContextHolderAwareRequestFilter.java:91)
	at org.springframework.security.ui.SpringSecurityFilter.doFilter(SpringSecurityFilter.java:53)
	at org.springframework.security.util.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:406)
	at waffle.spring.NegotiateSecurityFilter.doFilter(NegotiateSecurityFilter.java:117)
	at org.springframework.security.util.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:406)
	at org.springframework.security.context.HttpSessionContextIntegrationFilter.doFilterHttp(HttpSessionContextIntegrationFilter.java:235)
	at org.springframework.security.ui.SpringSecurityFilter.doFilter(SpringSecurityFilter.java:53)
	at org.springframework.security.util.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:406)
	at org.springframework.security.util.FilterChainProxy.doFilter(FilterChainProxy.java:185)
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:236)
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:167)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:96)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:76)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:233)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:191)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:127)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:102)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:109)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:298)
	at org.apache.jk.server.JkCoyoteHandler.invoke(JkCoyoteHandler.java:190)
	at org.apache.jk.common.HandlerRequest.invoke(HandlerRequest.java:291)
	at org.apache.jk.common.ChannelSocket.invoke(ChannelSocket.java:769)
	at org.apache.jk.common.ChannelSocket.processConnection(ChannelSocket.java:698)
	at org.apache.jk.common.ChannelSocket$SocketConnection.runIt(ChannelSocket.java:891)
	at org.apache.tomcat.util.threads.ThreadPool$ControlRunnable.run(ThreadPool.java:690)
	at java.lang.Thread.run(Thread.java:662)
```

As far as I can make it out, one of the threads in the tomcat seems to invalidate the session in the middle of the JSP rendering. The exception then occurs on the next request.getSession(); Pages are then missing the right column or other parts.

Probably important to know: The application makes very heavy use of imported sub-JSP pages. A single page might have to render more than 100 JSP files. Imports also go several levels deep. This is for a very large and complex intranet page. Smaller pages do not exibit this error, which would indicate a sort of timeout happening.

Debugging by stepping through makes the error vanish. (Very annoying..)

Any ideas where to look would be most welcome.

## Answer
> I think that there's a postback to the server from the UI which is having a problem. I would look on the client and see what HTTP requests it's making and which specific one is failing. Then examine the headers on it. Finally, the server is likely spitting content before it has a chance to negotiate security.

We found a work-around: We had to deactivate the SessionFixationProtectionFilter from Spring Security (see docs) by setting a property in the <html> element:

```html
<security:html session-fixation-protection="none" ...
```

I have no idea why this works, though.