THE SPRING FRAMEWORK, release 2.5.6 (October 2008)
--------------------------------------------------
http://www.springframework.org


1. INTRODUCTION

Spring is a modular Java/J2EE application framework, based on code published in "Expert One-on-One
J2EE Design and Development" by Rod Johnson (Wrox, 2002). Spring includes:

* Powerful JavaBeans-based configuration management, applying Inversion-of-Control principles. This makes
wiring up applications quick and easy. No more singletons littered throughout your codebase, no more arbitrary
properties files: one consistent and elegant approach everywhere. This core bean factory can be used in any
environment, from applets to J2EE containers.

* Generic abstraction layer for transaction management, allowing for pluggable transaction managers, and making
it easy to demarcate transactions without dealing with low-level issues. Generic strategies for JTA and a
single JDBC DataSource are included. In contrast to plain JTA or EJB CMT, Spring's transaction support is not
tied to J2EE environments.

* JDBC abstraction layer that offers a meaningful exception hierarchy (no more pulling vendor codes out of
SQLException), simplifies error handling, and greatly reduces the amount of code you'll need to write.
You'll never need to write another finally block to use JDBC again. The JDBC-oriented exceptions comply to
Spring's generic DAO exception hierarchy.

* Integration with JDO, JPA, Hibernate, TopLink, and iBATIS SQL Maps: in terms of resource holders,
DAO implementation support, and transaction strategies. First-class Hibernate and JDO support with many
IoC convenience features, addressing many typical Hibernate/JDO integration issues. All of these comply
to Spring's generic transaction and DAO exception hierarchies.

* AOP functionality, fully integrated into Spring configuration management. You can AOP-enable any object
managed by Spring, adding aspects such as declarative transaction management. With Spring, you can have
declarative transaction management without EJB... even without JTA, if you're using a single database in
Tomcat (or another web container without JTA support).

* Flexible MVC web application framework, built on core Spring functionality. This dispatcher framework
is highly configurable via strategy interfaces, and accommodates multiple view technologies: e.g. JSP, Tiles,
Velocity, FreeMarker, iText. It comes in a Servlet and a Portlet edition, working closely with the underlying
environment. Note that Spring can easily be combined with third-party web MVC frameworks too: e.g. Struts, JSF.

You can use all of Spring's functionality in any J2EE server, and most of it also in non-managed environments.
A central focus of Spring is to allow for reusable business and data access objects that are not tied to
specific J2EE services. Such objects can be reused across J2EE environments (web or EJB), standalone
applications, test environments, etc without any hassle.

Spring has a layered architecture; all of its functionality builds on lower levels. So you can for example
use the JavaBeans configuration management without using the MVC framework or AOP support. However, if you
use the web MVC framework or the AOP support, you'll find that they build on the configuration framework,
so you can apply your knowledge about it immediately.


2. RELEASE INFO

The Spring Framework 2.5 requires JDK 1.4.2 and J2EE 1.3 (Servlet 2.3, JSP 1.2, JTA 1.0, EJB 2.0). JDK 1.6 is
required for building the framework; for the full build including all aspects, AspectJ is required as well.

Integration is provided with Log4J 1.2, CGLIB 2.1, AspectJ 1.5/1.6, Commons Attributes 2.2, JCA 1.0/1.5,
JMX 1.2, Hibernate 3.1/3.2, TopLink 10/11, JDO 2.0/2.1, JPA 1.0, iBATIS SQL Maps 2.3, Hessian 2.1/3.1,
JAX-RPC 1.1, JAX-WS 2.0/2.1, Quartz 1.5/1.6, EHCache 1.3/1.4, Commons FileUpload 1.2, Velocity 1.5,
FreeMarker 2.3, JasperReports 1.3/2.0, JSTL 1.0/1.1, JSF 1.1/1.2, Struts 1.2/1.3, Tiles 1.2/2.0, etc.

Basic release contents (~6 MB):
* "dist" contains the Spring binary jar files

Contents of the "-with-docs" distribution (~35 MB):
* "dist" contains the Spring binary jar files, as well as corresponding source jars
* "docs" contains the Spring reference documentation in PDF and HTML format, as well as the complete API javadocs

Contents of the "-with-dependencies" distribution (~79 MB):
* "dist" contains the Spring binary jar files, as well as corresponding source jars
* "docs" contains the Spring reference documentation in PDF and HTML format, as well as the complete API javadocs
* "lib" contains all third-party libraries needed for building the framework and/or running the samples
* "src" contains the general Java source files for the framework
* "mock" contains the general Java source files for Spring's mock and testing support classes
* "test" contains the general Java source files for Spring's test suite
* "tiger/src" contains the JDK-1.5-specific Java source files for the framework
* "tiger/mock" contains the JDK-1.5-specific Java source files for Spring's mock and testing support classes
* "tiger/test" contains the JDK-1.5-specific Java source files for Spring's test suite
* "aspectj/src" contains the AspectJ-specific source files for the framework
* "aspectj/test" contains the AspectJ-specific source files for Spring's test suite
* "samples" contains various demo applications

The "lib" directory is only included in the "-with-dependencies" download. Make sure to download this full
distribution ZIP file if you want to run the sample applications and/or build the framework yourself.
Ant build scripts for the framework and the samples are provided. The standard samples can be built with
the included Ant runtime by invoking the corresponding "build.bat" files (see samples subdirectories).

Latest info is available at the public website: http://www.springframework.org
Project info at the SourceForge site: http://sourceforge.net/projects/springframework

The Spring Framework is released under the terms of the Apache Software License (see license.txt).
All libraries included in the "-with-dependencies" download are subject to their respective licenses.
This product includes software developed by the Apache Software Foundation (http://www.apache.org).


3. DISTRIBUTION JAR FILES

The "dist" directory contains the following distinct jar files for use in applications. Both module-specific
jar files and a jar file with all of Spring are provided. The following list specifies the corresponding contents
and third-party dependencies. Libraries in brackets are optional, i.e. just necessary for certain functionality.

FULL JAR (dist):

* "spring" (~2870 KB)
- Convenient jar file combining all standard modules (except for the test module and the Spring MVC support)
- Also includes the AOP Alliance interfaces (as a convenience)!
- Does not include contents of spring-aspects.jar, spring-test.jar and spring-webmvc*.jar!

MODULE JARS (dist/modules):

* "spring-aop" (~320 KB)
- Contents: AOP framework
- Dependencies: spring-core, (spring-beans, AOP Alliance)

* "spring-beans" (~480 KB)
- Contents: JavaBeans support, bean container
- Dependencies: spring-core

* "spring-context" (~465 KB)
- Contents: application context, JNDI, JMX, instrumentation, remoting, scripting, scheduling, validation
- Dependencies: spring-beans, (spring-aop, JMX API, EJB API)

* "spring-context-support" (~95 KB)
- Contents: Quartz and CommonJ scheduling, UI templating, mail, caching
- Dependencies: spring-context, (spring-aop, spring-jdbc)

* "spring-core" (~280 KB)
- Contents: core abstractions and utilities, source-level metadata support, repackaged ASM library
- Dependencies: Commons Logging, (Commons Attributes)

* "spring-jdbc" (~330 KB)
- Contents: JDBC support
- Dependencies: spring-beans, spring-tx

* "spring-jms" (~190 KB)
- Contents: JMS 1.0.2/1.1 support
- Dependencies: spring-beans, spring-tx, JMS API

* "spring-orm" (~370 KB)
- Contents: JDO support, JPA support, Hibernate support, TopLink support, iBATIS support
- Dependencies: spring-jdbc, (spring-web)

* "spring-test" (~180 KB)
- Contents: test context framework, JUnit support, JNDI mocks, Servlet API mocks, Portlet API mocks
- Dependencies: spring-core, (spring-context, spring-jdbc, spring-web, JUnit, Servlet API, Portlet API)

* "spring-tx" (~225 KB)
- Contents: transaction infrastructure, JCA support, DAO support
- Dependencies: spring-core, (spring-aop, spring-context, JTA API, JCA API)

* "spring-web" (~190 KB)
- Contents: web application context, multipart resolver, HTTP-based remoting support
- Dependencies: spring-context, Servlet API, (JSP API, JSTL)

* "spring-webmvc" (~395 KB)
- Contents: framework servlets, web MVC framework, web controllers, web views
- Dependencies: spring-web, (spring-context-support)

* "spring-webmvc-portlet" (~150 KB)
- Contents: framework portlets, portlet MVC framework, portlet controllers
- Dependencies: spring-web, Portlet API, (spring-webmvc)

* "spring-webmvc-struts" (~35 KB)
- Contents: Struts 1.x action support, Tiles 1.x view support
- Dependencies: spring-web, Struts API, (spring-webmvc)

WEAVING JARS (dist/weaving)

* "spring-agent" (~5 KB)
- Contents: Spring's InstrumentationSavingAgent (for InstrumentationLoadTimeWeaver)
- Dependencies: none (for use at JVM startup: "-javaagent:spring-agent.jar")

* "spring-aspects" (~20 KB)
- Contents: AspectJ aspects, e.g. for explicitly linking aspects into an IDE (Eclipse AJDT)
- Dependencies: spring-aop, AspectJ, (spring-tx)

* "spring-tomcat-weaver" (~10 KB)
- Contents: extension of Tomcat's ClassLoader, capable of class instrumentation
- Dependencies: none (for deployment into Tomcat's "server/lib" directory)


4. WHERE TO START?

Documentation can be found in the "docs" directory (depending on distribution zip):
* the Spring reference documentation
* the Spring MVC step-by-step tutorial

Documented sample applications can be found in "samples" (depending on distribution zip):
* imagedb
* jpetstore
* petclinic
* petportal

The Image Database sample is a simple one-screen image management web app that illustrates various
Spring-integrated technologies: C3P0 as connection pool, BLOB/CLOB handling with MySQL and Oracle,
Velocity and FreeMarker for web views, scheduling via Quartz and Timer, and mail sending via JavaMail.
Its web layer shows Spring 2.5's annotation-based style for multi-action controllers.
The imagedb sample application requires Java 5+.

The Spring JPetStore is an adapted version of Clinton Begin's JPetStore (available from http://www.ibatis.com).
It leverages Spring's support for the iBATIS SQL Maps to improve the original JPetStore in terms of
internal structure and wiring. On top of a Spring-managed middle tier, it offers two alternative web
tier implementations: one using Spring's web MVC plus JSTL, and one using Struts 1.2 plus JSTL. Furthermore,
it illustrates remoting via 5 different strategies: Hessian, Burlap, HTTP invoker, RMI invoker, and JAX-RPC.
JPetStore comes in a JDK 1.4 version as well as a Java 5+ version (see its readme).

PetClinic features alternative DAO implementations and application configurations for JDBC, Hibernate
and JPA, with HSQLDB and MySQL as target databases. The default PetClinic configuration is JDBC on HSQL,
which also demonstrates Spring's JMX export. The Spring distribution comes with all required jar files
to be able to build and run the Hibernate and JPA versions. PetClinic's web layer shows Spring 2.5's
annotation-based controller implementation style. PetClinic requires Java 5+.

PetPortal is Spring's primary sample for Portlet environments. It shows a couple of portlets combined
on a portal page, including multi-mode portlets and form-based portlets. All of the portlets are compliant
with JSR-168 (Portlet specification 1.0) and are implemented in Spring 2.5's annotation-based controller
style (except for the welcome portlet which is a hosted GenericPortlet). PetPortal requires Java 5+
and comes with instructions for deployment to Apache JetSpeed (see its readme).
