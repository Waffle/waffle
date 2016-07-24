Waffle Filter Setup Instructions Using Tomcat Plugins
=====================================================

Plugins currently only available for tomcat 6 and 7.

To deploy to a local running tomcat 6 or 7 instance, make the following changes

- Add a server block to .m2/settings.xml

``` xml
<servers>
    <server>
        <id>mylocalserver</id>
        <username>tomcat</username>
        <password>tomcat</password>
    </server>
</servers>
```

- Add user/perms in tomcat/conf/tomcat-users.xml

``` xml
<role rolename="tomcat">
<user username="tomcat" password="tomcat" roles="tomcat,manager-gui,manager-script,manager-jmx,manager-status" />
```

- Start your tomcat server. You can launch a locally installed tomcat with remote debugging enabled on port 8000 using one of these

    apache-tomcat-6.0.45$ bin/catalina.sh jpda start
    apache-tomcat-7.0.70$ bin/catalina.sh jpda start

- Build and Deploy the demo application to the local tomcat 6 instance using one of these

    mvn clean package tomcat6:redeploy
    mvn clean package tomcat7:redeploy

- The app will be available at:

    http://localhost:8080/waffle-filter-demo/
