Waffle JAAS Setup Instructions Using Cargo Plugin
=================================================

Cargo Plugin currently configured for tomcat 8.5.x.

To deploy using cargo plugin to tomcat 8.5.x

- Build the application

    mvn clean package

- To run cargo

    mvn cargo:run

- The app will be available at:

    http://localhost:8080/waffle-jaas/

- Use 'admin' as logon without any password to test the jaas

TODO: JAAS needs logon setup so it will fail at the moment on cargo.
