Waffle Filter Setup Instructions Using Tomcat Plugins
=====================================================

Plugins currently configured for tomcat 9.0.x.

To deploy using cargo plugin to tomcat 9.0.x

- Build the application

    mvn clean package

- To run cargo

    mvn cargo:run

- The app will be available at:

    http://localhost:8080/waffle-demo-filter/

- Use 'admin' as logon without any password to test the filter
