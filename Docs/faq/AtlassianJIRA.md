# Can Waffle be used with Atlassian products, notably JIRA?

----

Yes. Follow the [Servlet Filter Configuration Instructions](https://github.com/dblock/waffle/blob/master/Docs/ServletSingleSignOnSecurityFilter.md). 

* Deploy libs into tomcat's lib or application's `WEB-INF/lib`.
* Update `WEB-INF/web.xml` with `filter` and `filter-mapping`. 
  * Place the 'filter' before the last filter of JIRA, ie. `JiraLastFilter`.
  * Place the 'filter-mapping' before the last filter-mapping of JIRA, i.e. `JiraLastFilter`.
* Update `seraph-config.xml` to use a custom authenticator, [RemoteUserJiraAuth](https://marketplace.atlassian.com/plugins/anguswarren.jira.RemoteUserJiraAuth). Modification may be needed.
  * [Source](https://github.com/AngusWarren/remoteuserauth)
  * [Modification](https://github.com/AngusWarren/remoteuserauth/pull/6/files)

## Can AD users change or reset their password?

No, although this can be done through active directory

## Can I log in as a non-AD user?

Yes, log out and click log back in, this should bring up the login portal to log in as another user.

## I'm not seeing the startup page or my gadgets are not displaying properly

Check the location of your filter-mapping and make sure it is exactly before the filter mapping `JiraLastFilter`

