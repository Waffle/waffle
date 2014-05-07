Can WAFFLE be used on the client side?
======================================

Yes.

You can use WAFFLE to implement HTTP client-side to perform authentication against NTLMv2/SPNEGO secured services.

You don't actually need Waffle for client-side, just [JNA](https://github.com/twall/jna). Your code needs to invoke the Windows SSPI to obtain a value for the *WWW-Authenticate* header to be injected into the HTTP request, read the *Authorization* header from the HTTP response and possibly repeat that process several times.

In Java, a good place to start is [NegotiateSecurityFilterTests.testNegotiate](https://github.com/dblock/waffle/blob/master/Source/JNA/waffle-tests/src/test/java/waffle/servlet/NegotiateSecurityFilterTests.java#L133), that implements both the client and the server side.

In C#, a good place to start is the [WindowsAuthProviderUnitTests.testNegotiate](https://github.com/dblock/waffle/blob/master/Source/WindowsAuthProviderUnitTests/WindowsAuthProviderUnitTests.cs#L157).

There's a full example of both client and server in [github.com/dblock/ssoexample-waffle](https://github.com/dblock/ssoexample-waffle).

Read [this blog post](http://code.dblock.org/jna-acquirecredentialshandle-initializesecuritycontext-and-acceptsecuritycontext-establishing-an-authenticated-connection) if you want to understand what all these APIs do.



