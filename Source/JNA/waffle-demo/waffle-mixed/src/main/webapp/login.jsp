<%--

    Waffle (https://github.com/Waffle/waffle)

    Copyright (c) 2010-2020 Application Security, Inc.

    All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
    Public License v1.0 which accompanies this distribution, and is available at
    https://www.eclipse.org/legal/epl-v10.html.

    Contributors: Application Security, Inc.

--%>
<!DOCTYPE html>
<html lang="en" xml:lang="en">
<head>
<title>Login</title>
</head>
</html>
<body>
<form method="POST" name="loginform" action="index.jsp?j_security_check">
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
<hr>
<form method="POST" name="loginform" action="index.jsp?j_negotiate_check">
	<input type="submit" value="Login w/ Current Windows Credentials" />
</form>
</body>
