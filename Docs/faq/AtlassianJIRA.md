# Can Waffle be used with Atlassian products, notably JIRA?

----

Yes. Follow the [Servlet Filter Configuration Instructions](https://github.com/dblock/waffle/blob/master/Docs/ServletSingleSignOnSecurityFilter.md). 

Tested with Jira 7.6.3

* Deploy libs into tomcat's lib or application's `WEB-INF/lib`.
  * slf4j-1.7.22.jar can be excluded as a newer version is bundled with JIRA.
* Update `WEB-INF/web.xml` with `filter` and `filter-mapping`. 
  * Place the 'filter' before the last filter of JIRA, ie. `JiraLastFilter`.
    ```xml
    <filter>
        <filter-name>SecurityFilter</filter-name>
        <filter-class>waffle.servlet.NegotiateSecurityFilter</filter-class>
        <init-param>
            <param-name>excludePatterns</param-name>
            <param-value>
                .*/rest/.*
                .*/secure/CreateIssue.*
                .*/secure/QuickCreateIssue.*
            </param-value>
        </init-param>
    </filter>
    ```
  * Place the 'filter-mapping' before the login filter-mapping of JIRA.
    ```xml
    <filter-mapping>
        <filter-name>SecurityFilter</filter-name>
        <url-pattern>/</url-pattern>
        <url-pattern>/*</url-pattern>
        <dispatcher>REQUEST</dispatcher>
        <dispatcher>FORWARD</dispatcher>
        <dispatcher>POST</dispatcher>
    </filter-mapping>
    ```
* Update `seraph-config.xml` to use a custom authenticator, [RemoteUserJiraAuth](https://marketplace.atlassian.com/plugins/anguswarren.jira.RemoteUserJiraAuth). Modification may be needed.
  * [Source](https://github.com/AngusWarren/remoteuserauth)
  * [Modification](https://github.com/AngusWarren/remoteuserauth/pull/6/files)

## Can AD users change or reset their password?

No, although this can be done through active directory

## Can I log in as a non-AD user?

Yes, log out and click log back in, this should bring up the login portal to log in as another user.

## I'm not seeing the startup page or my gadgets are not displaying properly

Check the location of your filter-mapping and make sure it is exactly before the filter mapping `JiraLastFilter`
