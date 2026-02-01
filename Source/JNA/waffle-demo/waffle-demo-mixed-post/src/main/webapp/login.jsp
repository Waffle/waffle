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
<form method="POST" name="loginform" action="index.jsp">
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
<input type="hidden" name="j_security_check"/>
</form>
<hr>
<form method="POST" name="loginform" action="index.jsp">
	<input type="hidden" name="j_negotiate_check"/>
	<input type="submit" value="Login w/ Current Windows Credentials" />
</form>
</body>
</html>
