Can Waffle be used with Atlassian products, notably JIRA?
=========================================================

Yes, almost. Follow the [Servlet Filter Configuration Instructions](https://github.com/dblock/waffle/blob/master/Docs/ServletSingleSignOnSecurityFilter.md). 

* Deploy libs into tomcat's lib or application's `WEB-INF/lib`.
* Update `WEB-INF/web.xml` with `filter` and `filter-mapping`. Place the 'filter' before the last filter of JIRA, ie. `JiraLastFilter`.
* Update `seraph-config.xml` to use a custom authenticator, [RemoteUserJiraAuth](https://marketplace.atlassian.com/plugins/anguswarren.jira.RemoteUserJiraAuth). Modification may be needed.

Several JIRA features won't work, including the ability for AD user to reset and/or change password. The ability for non-AD user to login to the portal.
