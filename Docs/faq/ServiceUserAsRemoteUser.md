# Waffle returns service user as remote user
----

# Question
I'm experiencing a strange problem when deploying the waffle-filter sample application on a tomcat server in my network.

When I call the application, it says

You are logged in as remote user GROUP\SrvSMTUser in session E6D293314AD2243A2B2E6AA643FF43BC.
You are impersonating user GROUP\S12220$.

This is strange, because the windows account I'm using is GROUP\R233. S12220 is the server name which serves the sample application and SrvSMTUser is a service account in my network (also used on the server).

But how come I'm identified as someone else? Do I need to change something in the configuration or am I missing something here?

(Tested in IE and Firefox.)

> How are you getting that impersonating user and how did you configure the filter? I think that you're not impersonating anybody, so it just returns the account under which you run. You did login as the remote user (GROUP\SrvSMTUser) it seems though.

> This post, http://code.dblock.org/waffle-single-sign-on-user-impersonation-in-tomcat, might be helpful.

The account under which I run locally is definitely R233. This is also true for all browser instances (checked with task manager).

The service account is used for remote desktop sessions on the server, but also locally for development of a different application.

Could it be, that some local cache is used for authentication (like Kerberos ticket cache)? Because for my colleague the sample application is working fine (identifying him with his local user account).

Regarding impersonation: Thanks for the link, but I'm not using this feature.

I did not change anything in the sample applications filter:

```xml
<filter>
    <filter-name>SecurityFilter</filter-name>
    <filter-class>waffle.servlet.NegotiateSecurityFilter</filter-class>   
    <init-param>
    	<param-name>principalFormat</param-name>
    	<param-value>fqn</param-value>
    </init-param>
    <init-param>
    	<param-name>roleFormat</param-name>
    	<param-value>both</param-value>
    </init-param>
    <init-param>
    	<param-name>allowGuestLogin</param-name>
    	<param-value>true</param-value>
    </init-param>
    <init-param>
    	<param-name>securityFilterProviders</param-name>
    	<param-value>
    		waffle.servlet.spi.NegotiateSecurityFilterProvider
    		waffle.servlet.spi.BasicSecurityFilterProvider
    	</param-value>
    </init-param>
    <init-param>
    	<param-name>waffle.servlet.spi.NegotiateSecurityFilterProvider/protocols</param-name>
    	<param-value>
    		Negotiate
    		NTLM
    	</param-value>
    </init-param>
    <init-param>
    	<param-name>waffle.servlet.spi.BasicSecurityFilterProvider/realm</param-name>
    	<param-value>WaffleFilterDemo</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <filter-name>SecurityFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```

I found a solution for this: http://objectmix.com/inetserver/287887-integrated-windows-authentication-authenticating-wrong-user.html#post1017853

Summary: It wasn't Waffle's fault at all. But I find Windows' behavior kind of strange in this case.