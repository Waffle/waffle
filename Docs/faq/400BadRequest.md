# HTTP/1.1 400 Bad Request

## Question

I'm using the waffle.servlet.NegotiateSecurityFilter with the following configuration.
```xml
<filter>
    <filter-name>SecurityFilter</filter-name>
    <filter-class>waffle.servlet.NegotiateSecurityFilter</filter-class>
    <init-param>
        <param-name>allowGuestLogin</param-name>
        <param-value>false</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>SecurityFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
In my test scenarios everything worked as expected.

Now I'm using it in a different environment and I'm having some problems. Two different browsers access my application. Client A is authenticated successfully but Client B is not authenticated (Internet Explorer cannot display the webpage"). Because of some restrictions it's not possible to disable the "friendly" http error messages in IE but I suspect that it is HTTP 401.

Client A and Client B are both using Windows XP SP2 with IE6 in the same domain and same logon server.

The logfile shows for Client B (the one which is not working) just the line "authorization required" and nothing more.

The Browser shows for Client A "Local Intranet" and for Client B "Internet". If I deactivate the NegotiateSecurityFilter and the clients logon manually, both Browsers show "Local Intranet".

The next step was, that I reactivated the old jcifs.http.NtlmHttpFilter and the SSO worked sucessfully for both Clients.

Then I changed the waffle filter configuration to the following (first NTLM, second Negotiate):

```xml
<filter>
    <filter-name>SecurityFilter</filter-name>
    <filter-class>waffle.servlet.NegotiateSecurityFilter</filter-class>
    <init-param>
        <param-name>allowGuestLogin</param-name>
        <param-value>false</param-value>
    </init-param>
    <init-param>
        <param-name>waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols</param-name>
        <param-value>
        NTLM
            Negotiate
        </param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>SecurityFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

This configuration works fine for Client A and B. Both clients are sucessfully authenticated using waffle.

Any idea what's going on here? Is this configuration the better default configuration?

# Answer
> It looks like your client B won't do Kerberos, but is happy to do NTLM. This is confirmed with your NtlmHttpFilter that only does that. Client B gets Authorization: Negotiate (and others), then tries to do something with Kerberos, fails and doesn't fall back to NTLM.

> First you should verify the above by looking at the HTTP trace (try IEHttpHeaders). Your client B will make a single request, then do nothing about it.

> I would then check that client B has the exact same security settings. Specifically Tools->Internet Options->Advanced->Security, start with "Enable Integrated Windows Authentication". Then all the other settings.

> Your client B thinks that the server is not in the intranet. What's the URL? Maybe it has saved it as being in the internet zone, try re-adding this server to the Intranet zone and maybe resetting the security zones altogether.

> Then, try wfetch, see http://support.microsoft.com/default.aspx?scid=kb;en-us;284285. It should hopefuly tell you what the client side problem with Kerberos is.

Currently I'm experimenting with Kerberos authentication (value "Negotiate" for the parameter "waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols"). I found this article http://support.microsoft.com/kb/327825/en-us "Problems with Kerberos authentication when users belong to many groups" and I assume it may be the reason for my old problem (Client A works with Kerberos, Client B doesn't). In Tomcat the default maximum size of the request and response HTTP header is 4096 (4 KB). If the Kerberos ticket is larger, it doesn't fit in the header and the authentication fails. So, to workaround this it's necessary the modify the server.xml in Tomcat and add a "maxHttpHeaderSize" attribute to the Connector (e.g. value 32768 for 32 KB or 16384 for 16 KB).

```xml
<Connector port="8080" protocol="HTTP/1.1" 
            connectionTimeout="20000" 
            redirectPort="8443" 
            maxHttpHeaderSize="32768" />
```

I just had a case where the Kerberos ticket was 4349 bytes and the authentication failed. The logfile of the Apache Tomcat server ends with the following line:

waffle.servlet.NegotiateSecurityFilter - authorization required

The ieHttpHeaders console on the client in IE shows:

```
HTTP/1.1 400 Bad Request
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked
Date: Wed, 02 Mar 2011 16:01:12 GMT
Connection: close
```

The solution is to increase the maxHttpHeaderSize for the Connector, as I wrote before.

I haven't looked at the NegotiateSecurityFilter source code, but maybe it's possible to show a better error message for this case.
