set windowsAuthProviderImpl = CreateObject("Waffle.Windows.AuthProvider")
WScript.Echo typename(windowsAuthProviderImpl)

set user = windowsAuthProviderImpl.LogonUser("nycapt35k\buildapp", "password")
WScript.Echo typename(user)
WScript.Echo user.Fqn

' windowsAuthProviderImpl.LogonDomainUser "Administrator", "dblock-green", "password"
' windowsAuthProviderImpl.LogonDomainUserEx "Administrator", "dblock-green", "password", 3, 0

set account = windowsAuthProviderImpl.LookupAccount("nycapt35k\buildapp")
WScript.Echo typename(account)
WScript.Echo account.Fqn