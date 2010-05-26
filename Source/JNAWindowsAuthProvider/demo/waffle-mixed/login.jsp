<html>
<head>
<title>Login</title>
</head>
</html>
<body>
<form method="POST" name="loginform" action="index.jsp?j_security_check">
<table style="vertical-align: middle;">
	<tr>
		<td>Username:</td>
		<td><input type="text" name="j_username" /></td>
	</tr>
	<tr>
		<td>Password:</td>
		<td><input type="password" name="j_password" /></td>
	</tr>
	<tr>
		<td><input type="submit" value="Login" /></td>
	</tr>
</table>
</form>
<hr>
<form method="POST" name="loginform" action="index.jsp?j_negotiate_check">
	<input type="submit" value="Login (Negotiate)" />
	<!-- bug: http://waffle.codeplex.com/WorkItem/View.aspx?WorkItemId=8544 -->
	<input type="hidden" name="dummy" value="dummy" />
</form>
</body>
