<%--

    Waffle (https://github.com/Waffle/waffle)

    Copyright (c) 2010-2020 Application Security, Inc.

    All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
    Public License v1.0 which accompanies this distribution, and is available at
    https://www.eclipse.org/legal/epl-v10.html.

    Contributors: Application Security, Inc.

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
