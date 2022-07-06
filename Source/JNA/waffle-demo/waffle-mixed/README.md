Waffle Mixed Setup Instructions Using Cargo Plugin
==================================================

Cargo Plugin currently configured for tomcat 8.5.x.

To deploy using cargo plugin to tomcat 8.5.x

- Build the application

    mvn clean package

- To run cargo

    mvn cargo:run

- The app will be available at:

    http://localhost:8080/waffle-mixed/

- Use 'admin' as logon without any password to test the mixed

TODO: Need to figure out service loader portion as it will be in 2 classloaders and errors out
