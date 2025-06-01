Waffle Mixed Setup Instructions Using Cargo Plugin
==================================================

Cargo Plugin currently configured for tomcat 10.1.x.

To deploy using cargo plugin to tomcat 10.1.x

- Build the application

    mvn clean package

- To run cargo

    mvn cargo:run

- The app will be available at:

    http://localhost:8080/waffle-demo-mixed-jakarta/

- Use 'DOMAIN\\user' as logon with windows password to test the mixed
