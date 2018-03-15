# Issues specifying AD groups with Spring-security
----

## Question

Using Waffle (Tomcat/JAAS), how do I limit access to a group. 

Say I have an AD  structure with a group named "LocalDevelopers"

How do I limit access to that group?

## Answer

With a plain security-constraint. Waffle inserts every group name as a "role".
The group should be pre-fixed with the domain name.

```xml
<security-constraint>
    <display-name>Waffle Security Constraint</display-name>
    <web-resource-collection>
      <web-resource-name>Protected Area</web-resource-name>
      <url-pattern>/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
      <role-name>MyDomain\LocalDevelopers</role-name>
    </auth-constraint>
</security-constraint>
```
