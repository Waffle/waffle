Tomcat JSP Demo
===============

The following sample is a handy page to test whether any of the authentication methods is working properly. Save the contents of this page as test.jsp and place it in your Tomcat's `webapps\ROOT` or any other application directory. 

The following page will display the current user with roles in a JSP page. 

``` jsp
<%@ page import="java.security.Principal,org.apache.catalina.realm.GenericPrincipal" %>
<html>
 <body>
  Hello, <%= request.getUserPrincipal().getName() %>
  <%
        final Principal userPrincipal = request.getUserPrincipal(); 
        GenericPrincipal genericPrincipal = (GenericPrincipal) userPrincipal; 
        final String[] roles = genericPrincipal.getRoles();
        out.println("<p>You have " + roles.length + " role(s).</p>");
        out.println("<ul>");
        for(String role : roles) {
                out.println("<li>" + role);
        }
        out.println("</ul>");
  %>
 </body>
</html>
```
