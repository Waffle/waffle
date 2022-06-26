Waffle Filter Setup Instructions Using Tomcat Plugins
=====================================================

Plugins currently configured for tomcat 8.5.x.  To use tomcat 9.0.x, switch out tomcat integration.

To deploy using cargo plugin to tomcat 8.5.x

- Build the application

    mvn clean package

- To start cargo

    mvn cargo:start

- To stop cargo

    mvn cargo:stop

- The app will be available at:

    http://localhost:18080/waffle-filter-demo/
