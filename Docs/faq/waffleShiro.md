How can I use waffle with Apache Shiro?
=============================================

See the classes in the `waffle-shiro` sub project. They provide Realms and Filters for use in [Apache Shiro](http://shiro.apache.org/).

The `waffle.shiro.GroupMappingWaffleRealm` provides a User/Password Realm that uses waffle.

The `waffle.shiro.negotiate.NegotiateAuthenticationFilter` and `waffle.shiro.negotiate.NegotiateAuthenticationRealm`
provide SingleSignOn (see shiro.ini notes in `DynamicAuthenticationFilter` javadocs regarding SSOCookie config).

The `waffle.shiro.dynamic.DynamicAuthenticationFilter` and `waffle.shiro.dynamic.DynamicAuthenticationStrategy` provide
a way for a client to select which authentication type is used at runtime.
