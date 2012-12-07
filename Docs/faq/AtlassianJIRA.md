Can Waffle be used with Atlassian products, notably JIRA?
=========================================================

Yes, almost. Follow the [Servlet Filter Configuration Instructions](https://github.com/dblock/waffle/blob/master/Docs/ServletSingleSignOnSecurityFilter.md). 

* Deploy libs into tomcat's lib or application's `WEB-INF/lib`.
* Update `WEB-INF/web.xml` with `filter` and `filter-mapping`. Place before the last filter of JIRA, ie. `JiraLastFilter`.
* Update `seraph-config.xml` to use a custom authenticator, [RemoteUserJiraAuth](https://marketplace.atlassian.com/plugins/anguswarren.jira.RemoteUserJiraAuth).

Several JIRA features won't work, including the ability for super-admin to administer the application, reset and forgot password.
