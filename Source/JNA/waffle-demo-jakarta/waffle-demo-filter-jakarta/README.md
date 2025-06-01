Waffle Filter Setup Instructions Using Tomcat Plugins
=====================================================

Plugins currently configured for tomcat 10.1.x.

To deploy using cargo plugin to tomcat 10.1.x

- Build the application

    mvn clean package

- To run cargo

    mvn cargo:run

- The app will be available at:

    http://localhost:8080/waffle-demo-filter-jakarta/

- Use 'admin' as logon without any password to test the filter
