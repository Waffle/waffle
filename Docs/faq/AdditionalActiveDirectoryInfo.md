How can i retrieve additional information, such as user's position in the company, from active directory?
===========================================================================================================

You cannot do this with Waffle directly.

On Windows this can be done by querying Active Directory with [ADSI](http://msdn.microsoft.com/en-us/library/windows/desktop/aa772170.aspx). This involves locating the user's record by the SID obtained from the logon and fetching any additional information.

This has been discussed [here](http://waffle.codeplex.com/workitem/10034) and an implementation using com4j is available in [this gist](https://gist.github.com/3004083) and [this CodeProject article](http://www.codeproject.com/Articles/572278/Java-Retrieving-users-information-such-as-email-an).

