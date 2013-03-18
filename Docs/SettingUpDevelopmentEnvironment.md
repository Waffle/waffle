Setting Up a Development Environment
====================================

You're encouraged to contribute to WAFFLE. The latest information about this project and source code can be found at [https://github.com/dblock/waffle](https://github.com/dblock/waffle)

Contributing
------------

Fork the project. Make pull requests. Bonus points for topic branches.

Source Code
-----------

The C# implementation is in a Visual Studio solution file, `Waffle.sln`. For Java, use Eclipse and import the projects under `Source`. 

Development Environment
-----------------------

* Git
* Microsoft Visual Studio 2008 for C#
* Eclipse for Java
* ANT for Command-Line Java Build
* Windows Installer Xml (WIX) 3.5 for Windows Merge-Modules

Building the Project
--------------------

You can build all C# code in Visual Studio and all Java code in Eclipse. To build the entire project, including this documentation use MSBuild from command line and type `build all`. This builds both Debug and Release .NET binaries as well as a final .zip package with all artifacts. 

```
c:\source\Waffle\trunk> build all
```

Output is placed in the `target\Debug|Release` directories. 


Running the demos within Eclipse
--------------------------------

To run the demos within the IDE, you can simply: Right Click on "StartEmbeddedJetty" > Run As (or Debug As) > Java Appliction

![start-jetty-in-eclipse](https://raw.github.com/ryantxu/waffle/8643a0de5b5ef4bf5de645a866bb1ae3c8b833f8/Docs/imgs/eclipse-start-jetty.png)

To change which demo you are running, change:
```
String path = "../demo/waffle-filter";
```

