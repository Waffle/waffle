Waffle Form Setup Instructions Using Cargo Plugin
=================================================

Cargo Plugin currently configured for tomcat 9.0.x.

To deploy using cargo plugin to tomcat 9.0.x

- Build the application

    mvn clean package

- To run cargo

    mvn cargo:run

- The app will be available at:

    http://localhost:8080/waffle-demo-form/

- Use 'DOMAIN\\user' as logon with windows password to test the form
