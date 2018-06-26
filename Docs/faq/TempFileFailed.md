# Failed to create temporary file for jnidispatch library
----

## Question
Failed to create temporary file for jnidispatch library: java.io.IOException: The system cannot find the path specified at com.sun.jna.Native.loadNativeLibraryFromJar(Native.java:751)

Win Vista SP1
```
>java -version
java version "1.6.0_23"Java(TM) SE Runtime Environment (build 1.6.0_23-b05)
Java HotSpot(TM) Client VM (build 19.0-b09, mixed mode, sharing)
apache-tomcat-6.0.30 
Attempt to access http://localhost:8080/waffle-filter deployed at Tomcat give me:
java.lang.Error: Failed to create temporary file for jnidispatch library: java.io.IOException: The system cannot find the path specified
	at com.sun.jna.Native.loadNativeLibraryFromJar(Native.java:751)
	at com.sun.jna.Native.loadNativeLibrary(Native.java:685)
	at com.sun.jna.Native.(Native.java:109)
	at com.sun.jna.NativeLong.(NativeLong.java:23)
	at waffle.windows.auth.impl.WindowsCredentialsHandleImpl.(Unknown Source)
	at waffle.windows.auth.impl.WindowsAuthProviderImpl.acceptSecurityToken(Unknown Source)
	at waffle.servlet.spi.NegotiateSecurityFilterProvider.doFilter(Unknown Source)
	at waffle.servlet.spi.SecurityFilterProviderCollection.doFilter(Unknown Source)
	at waffle.servlet.NegotiateSecurityFilter.doFilter(Unknown Source)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:233)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:191)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:127)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:102)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:109)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:298)
	at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:859)
	at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:588)
	at org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:489)
	at java.lang.Thread.run(Thread.java:662)
```

## Answer
> First, let me explain how JNA works. JNA uses libffi, a magical native implementation that translates Java -> native calls (or something like that). So underneath JNA extracts a native DLL into the temporary folder and then loads it. When the process exits it kills that DLL.

> So you have a problem on this server where JNA can't write into %TEMP%. Try running the server as Administrator to start (as a test or you might need to do that in the long run anyway). Then see if you can globally redefine java.io.tmpdir to point to a folder that's writable by a user under which your server is running.

Ouch! I've by mistake removed Tomcat's temp dir and that was the problem (discovered via sysinternals procmon)!
