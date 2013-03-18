Waffle for Java Maven-Based Projects
------------------------------------

If you use maven, all you need to do is to add the following to your pom.xml:

  <properties>
        <waffle.version>1.5</waffle.version>
  </properties>

  <dependency>
        <groupId>com.github.dblock.waffle</groupId>
        <artifactId>waffle-jna</artifactId>
        <version>${waffle.version}</version>
    </dependency>
If you intend to use waffle on top of Spring implementation, you should add also:

  <dependency>
        <groupId>com.github.dblock.waffle</groupId>
        <artifactId>waffle-spring-security3</artifactId>
        <version>${waffle.version}</version>
    </dependency>            
In the example above spring3 is in use. If you use Spring 2.x, change it respectively.
