<%--

    SPDX-License-Identifier: MIT
    See LICENSE file for details.

    Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors

--%>
<!DOCTYPE html>
<html lang="en">
 <head>
  <title>Login</title>
 </head>
 <body>
  <form method="POST" action="j_spring_security_check" accept-charset="utf-8">
   <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
   <table style="vertical-align: middle;">
    <caption>Waffle Security Logon</caption>
    <tr>
     <th scope="row">Username:</th>
     <td><input type="text" name="j_username" /></td>
    </tr>
    <tr>
     <th scope="row">Password:</th>
     <td><input type="password" name="j_password" /></td>
    </tr>
    <tr>
     <th scope="rowgroup"><input type="submit" value="Login" /></th>
    </tr>
   </table>
  </form>
 </body>
</html>
