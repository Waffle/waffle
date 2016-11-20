Symptoms
--------
User name was changed on a domain without changing the SID.
Waffle authenticates a domain user but returns his outdated nonexistent name from principal.getName().

Cause
-----
The local security authority (LSA) caches the mapping between the SID and the user name in a local cache on the domain member server. 
The cached user name is not synchronized with domain controllers.
All cache entries do time out but when a user contacts the server day by day they usually don't expire.

Workaround
----------
The cache can be disabled (temporarily) through the windows registry.
Locate the following subkey:
```
HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Lsa 
```
Add new DWORD value:
```
LsaLookupCacheMaxSize = 0
```

That's all. Don't restart the server just ask the user to visit secured web page one more time.
When the user is authenticated on the server and the Waffle reports new user name we can roll back registry changes.

Long story
---------- 
https://support.microsoft.com/kb/946358
