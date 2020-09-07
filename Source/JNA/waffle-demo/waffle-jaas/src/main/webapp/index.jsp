<%--

    MIT License

    Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

--%>
<%
  if (request.getParameter("logoff") != null) {
    session.invalidate();
    response.sendRedirect("index.jsp");
    return;
  }
%>
<%@page import="java.security.*" %>
<%@page import="javax.security.auth.*" %>
<!DOCTYPE html>
<html lang="en" xml:lang="en">
 <head>
  <title>Protected Page for Examples</title>
  <% Subject subject = Subject.getSubject(AccessController.getContext()); %>
 </head>
 <body style="background-color:white;">
  <strong>Subject</strong> = <%= subject %>
  You are logged in as remote user <strong><%= request.getRemoteUser() %></strong> in session <strong><%= session.getId() %></strong>.
  <br><br>
  <%
	if (request.getUserPrincipal() != null) {
  %>
  Your user principal name is <strong><%= request.getUserPrincipal().getName() %></strong>.
  <br><br>
  <%
   } else {
  %>
   No user principal could be identified.
   <br><br>
  <%
  }
  %>
  <%
  String role = request.getParameter("role");
  if (role == null)
    role = "";
  if (role.length() > 0) {
    if (request.isUserInRole(role)) {
  %>
  You have been granted role <strong><%= role %></strong>.
  <br><br>
  <%
   } else {
  %>
  You have <em>not</em> been granted role <strong><%= role %></strong>.
  <br><br>
  <%
   }
  }
  %>
  To check whether your username has been granted a particular role, enter it here:
  <form method="GET" action='<%= response.encodeURL("index.jsp") %>'>
   <input type="text" name="role" value="<%= role %>">
  </form>
  <br><br> 
  You can logoff by clicking
  <a href='<%= response.encodeURL("index.jsp?logoff=true") %>'>here</a>.
  This should cause automatic re-logon with Waffle and a new session ID.
 </body>
</html>
