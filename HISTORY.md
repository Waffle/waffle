Waffle: The Long Story
======================

We, [AppSecInc.](http://www.appsecinc.com), had a pickle. 

Our application, written in Java and running exclusively on Windows, needed Windows authentication. We started with FORMS and had no idea how to do this. After logon we wanted to get the logged on user and his group memberships.

* We got the username and used it for permissions. This was wrong because the username can change. We should have used the SID, but that was harder to get.
* We enumerated local groups to find out group members. Then we used group names for permissions. This was wrong because group names change. We should have used the group SIDs.

From local users we extended this to support Active Directory. We made another fatal mistake. We attempted to enumerate domain groups via LDAP to find out those that the user is a member of. This was wrong because it ignores domain trusts. This was also very hard because groups can be nested (we never got it to work that way).
From Active directory Support we wanted to do single-sign-on. We couldn't do it, so we went back to the drawing board. 

Waffle is all of the above done right, open-source and free.

