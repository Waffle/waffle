Waffle Negotiate Setup Instructions Using Cargo Plugin
======================================================

Cargo Plugin currently configured for tomcat 9.0.x.

To deploy using cargo plugin to tomcat 9.0.x

- Build the application

    mvn clean package

- To run cargo

    mvn cargo:run

- The app will be available at:

    http://localhost:8080/waffle-demo-negotiate/

- Use 'admin' as logon without any password to test the negotiate
