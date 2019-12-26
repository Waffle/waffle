# Invalid Authorization Header Problems calling OAUTH2 covered Resources using the NegotiateSecurityFilter     

I need to support CORS handled outside of Tomcat, serve static content, provide SSO Administration Access and API access using OAUTH2 Basic and Bearer Authentication on the same Tomcat Server

1 need a combination of 
1. NTLM Basic Authentication
2. OAUTH2 client_credentials grant type
3. Container SSO to restrict access to any contexts not handled by the SSO Security Filter
4. OAUTH2 API Access using Bearer Authorization


## Tomcat

OAUTH2 uses the Basic Authentication for the client_credentials grant_type as does NTLM Challenge Response. 

To work through pretty complex requirement like this you could use a combination of the Tomcat Single-SignOn Authenticator Valve and the NegotiateSecurityFilter

1. Configure context.xml with [NegotiateAuthenticator](../tomcat/TomcatSingleSignOnValve.md)
1. Configure web.xml and add
   1. a Filter Mapping to cover requests to your api and oauth2 server using the following parameters
      1. excludePatterns that include client_credentils grant type Basic Authentication Token Requests and Bearer Authorization Requests   
   1. 2 security-constraint to your CATALINA_BASE\conf\web.xml to exclude all request to your api or oauth server and cover all other request other than OPTIONS methods 
         
### What is the net affect of the above configuration?      
1. Omits the OPTIONS method in your security-constraint allowing CORS requests for static content
1. Covers all requests for all, but the OPTIONS, HTTP Methods for any context or resource that is not being handled by your NegotiateSecurityFilter
1. Lest your use Windows NTLM Basic Authentication request using the waffle.servlet.spi.BasicSecurityFilterProvider configured in the NegotiateSecurityFilter 
1. exclude the URL Pattern that is to your OAUTH2 Server with a Basic Authentication that is NOT an NTLM Basic Authentication
1. continue the FilterChain for any CORS preflight requests
 
   
### Example Servlet and Context Configurations
#### Filter Mapping
Guarding API requests and OAUTH2 Server Request
``` xml
<filter-mapping>
  <filter-name>SecurityFilter</filter-name>
  <url-pattern>/api/*</url-pattern>
  <url-pattern>.*/oauth2-server/*</url-pattern>
</filter-mapping>
<filter>
```

#### Filter
1. excludeCorsPreflight - lets the FilterChain continue to a CORS filter that will handle the CORS preflight request
1. excludeBearerAuthorization - Let Bearer Authorization through to the OAUTH2 covered resource
1. exclude requests to your Basic Authentication endpoint 
``` xml
<filter>
  <filter-name>SecurityFilter</filter-name>
  <filter-class>waffle.servlet.NegotiateSecurityFilter</filter-class>   
  <init-param>
      <param-name>principalFormat</param-name>
      <param-value>fqn</param-value>
  </init-param>
  <init-param>
      <param-name>roleFormat</param-name>
      <param-value>fqn</param-value>
  </init-param>
  <init-param>
      <param-name>allowGuestLogin</param-name>
      <param-value>false</param-value>
  </init-param>
  <init-param>
      <param-name>impersonate</param-name>
      <param-value>false</param-value>
  </init-param>
  <init-param>
        <!-- API requests to OAUTH2 covered resrource -->
      <param-name>excludeBearerAuthorization</param-name>
      <param-value>true</param-value>
  </init-param>
  <init-param>
      <!-- do not Authenticate any CORS preflight requests -->
      <param-name>excludeCorsPreflight</param-name>
      <param-value>true</param-value>
  </init-param>
  <!-- 
    do not Authenticate the OAUTH2 Basic Authorization requests which are for your OAUTH2 Server  
  -->    
  <init-param>
      <param-name>excludePatterns</param-name>
      <param-value>
        .*/oauth2-server/token
      </param-value>
  </init-param>
  <init-param>
      <param-name>securityFilterProviders</param-name>
      <param-value>
          waffle.servlet.spi.BasicSecurityFilterProvider
          waffle.servlet.spi.NegotiateSecurityFilterProvider
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
```

#### Container CATALINA_BASE/context.xml
1. let resource requests to api and oauth2 servlet be handled by the SecurityFilter
1. Handle all resource requests other than OPTIONS requests e.g. fetch to static files
``` xml
<security-constraint>
    <display-name>let api through and be handled by the Negotiate Security Filter</display-name>
    <web-resource-collection>
        <url-pattern>/api/*</url-pattern>
        <url-pattern>/oauth2-server/*</url-pattern>
    </web-resource-collection>
</security-constraint>
<security-constraint>
    <display-name>context.xml has Waffle for SSO for everything other than /api/*</display-name>
    <web-resource-collection>
        <url-pattern>/*</url-pattern>
        <http-method-omission>OPTIONS</http-method-omission>
    </web-resource-collection>
     <auth-constraint>
        <role-name>everyone</role-name>
      </auth-constraint>
</security-constraint>
```

### The net affect of this Configuration
1. Authorization: Bearer header, the request goes to api which checks the token with the OAUTH2 Server
2. CORS preflight checks with OPTIONS go on to the CORS Filter
3. GET /myservlet/api/oauth-covered-resource with no Bearer and not in excludePatterns gets Negotiate/NTLM
4. GET /myservlet/api/oauth-covered-servlet Authorization: Basic and in excludePatterns goes to OAUTH client_credentials endpoint @see 2.3.1 of [client_password for Client Authentication](https://tools.ietf.org/html/rfc6749#section-2.3)



