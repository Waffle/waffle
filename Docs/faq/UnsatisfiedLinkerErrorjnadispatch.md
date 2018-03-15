# UnsatisfiedLinkerError jnadispatch
----

## Question

I'm having a problem trying to run JNA locally via JDeveloper (JDeveloper uses OC4j server locally to debug applications).  I still get the UnsatisfiedLinkError (see below).

Error Received

```
java.lang.UnsatisfiedLinkError: void com.sun.jna.Pointer._write(long, byte[], int, int)
    at com.sun.jna.Pointer._write(Native Method)
    at com.sun.jna.Pointer.write(Pointer.java:275)
    at com.sun.jna.Memory.write(Memory.java:301)
    at com.sun.jna.platform.win32.Sspi$SecBuffer.(Sspi.java:288)
    at com.sun.jna.platform.win32.Sspi$SecBuffer$ByReference.(Sspi.java:228)
    at com.sun.jna.platform.win32.Sspi$SecBufferDesc.(Sspi.java:339)
    at waffle.auth.impl.WindowsAuthProviderImpl.acceptSecurityToken(WindowsAuthProviderImpl.java)
```
## Answer

I think this just means that JNA jna.jar is not in the CLASSPATH. But maybe it's something with permissions... I am totally guessing.
