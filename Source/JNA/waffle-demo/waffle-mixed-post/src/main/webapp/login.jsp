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
<!DOCTYPE html>
<html lang="en" xml:lang="en">
<head>
<title>Login</title>
</head>
</html>
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
