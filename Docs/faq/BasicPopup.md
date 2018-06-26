# Browser shows BASIC authentication popup
----

# Question

I'm calling the waffle-filter example in Firefox. In about:config I have added localhost to "network.negotiate-auth.trusted-uris". Now if I call the sample in Firefox `http://localhost:7070/waffle-filter/` the logfile shows:

```
25.07.2010 13:54:22 waffle.servlet.NegotiateSecurityFilter doFilter
INFO: GET /waffle-filter/, contentlength: -1
25.07.2010 13:54:22 waffle.servlet.NegotiateSecurityFilter doFilter
INFO: authorization required
```

And in the browser the popup is shown:

```
   Authentication Required. 
   A username and password are being requested by http://localhost:7070. The site says: "WaffleFilterDemo"
```

If I click "cancel" I'm sucessfully logged in and the logfile shows:

```
25.07.2010 13:56:02 waffle.servlet.NegotiateSecurityFilter doFilter
INFO: GET /waffle-filter/, contentlength: -1
25.07.2010 13:56:02 waffle.servlet.spi.NegotiateSecurityFilterProvider doFilter
INFO: security package: Negotiate, connection id: 127.0.0.1:58620
25.07.2010 13:56:02 waffle.servlet.spi.NegotiateSecurityFilterProvider doFilter
INFO: token buffer: 124 byte(s)
25.07.2010 13:56:02 waffle.servlet.spi.NegotiateSecurityFilterProvider doFilter
INFO: continue token: oYG6MIG3oAMKAQGhDAYKKwYBBAGCNwICCqKBoQSBnk5UTE1TU1AAAgAAAA4ADgA4AAAAFcKK4lzxnYYNO80lcCrIAQAAAABYAFgARgAAAAYBsB0AAAAPQwBBAFAAUgBJAEMAQQACAA4AQwBBAFAAUgBJAEMAQQABAA4AQwBBAFAAUgBJAEMAQQAEAA4AYwBhAHAAcgBpAGMAYQADAA4AYwBhAHAAcgBpAGMAYQAHAAgAAhiSWvArywEAAAAA
25.07.2010 13:56:02 waffle.servlet.spi.NegotiateSecurityFilterProvider doFilter
INFO: continue required: true
25.07.2010 13:56:02 waffle.servlet.NegotiateSecurityFilter doFilter
INFO: GET /waffle-filter/, contentlength: -1
25.07.2010 13:56:02 waffle.servlet.spi.NegotiateSecurityFilterProvider doFilter
INFO: security package: Negotiate, connection id: 127.0.0.1:58620
25.07.2010 13:56:02 waffle.servlet.spi.NegotiateSecurityFilterProvider doFilter
INFO: token buffer: 121 byte(s)
25.07.2010 13:56:02 waffle.servlet.spi.NegotiateSecurityFilterProvider doFilter
INFO: continue token: oRswGaADCgEAoxIEEAEAAABDh+CIwTbjqQAAAAA=
25.07.2010 13:56:02 waffle.servlet.spi.NegotiateSecurityFilterProvider doFilter
INFO: continue required: false
25.07.2010 13:56:02 waffle.servlet.NegotiateSecurityFilter doFilter
INFO: successfully logged in user: caprica\Soundlink
```

Any idea why I'm getting the popup in Firefox (IE is working)? My environment is Windows 7 without AD and Tomcat 6.0.28. Do I have to add some settings in Firefox?

## Answer

It's because firefox is interpreting WWW-Authenticate headers a little too literally and trying BASIC auth first. It's trying the authentication providers in order they appear, without having any preference in terms of what's "more secure" or "best". In the samples the configuration says the following:

```xml
<init-param>
 <param-name>securityFilterProviders</param-name>
 <param-value>
  waffle.servlet.spi.BasicSecurityFilterProvider
  waffle.servlet.spi.NegotiateSecurityFilterProvider
 </param-value>
</init-param>
```

This means BASIC auth first, then Negotiate. Flipping the order fixes the Firefox problem. In your web-inf\web.xml I would remove all the init-params that you're not using (the default is the "better" order where Negotiate is first). I'll change the sample configuration as well to avoid confusion, thank you for pointing this out.