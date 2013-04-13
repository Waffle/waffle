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


Troubleshooting the project setup
--------------------------------
If Eclipse complains that there are build path errors for your projects or msbuild or ant do not work, try the steps below to fix the errors and get your project up and running. (Note that these steps are geared towards Windows setup and may vary for different operating systems)

If you are running on Windows Vista or later, you'll want to run the command prompt in administrator mode. If you're on XP, just run the command prompt normally.


* Set up ant

Download apache ant https://ant.apache.org/bindownload.cgi and unzip it to whatever directory you choose. You'll need to update your path variables to get ant to work. Let's say you've placed ant at C:\ant. Your path variables can be set up as follows. Just make sure to replace the parentheses with the proper java install directory.
```
set ANT_HOME=C:\ant
set JAVA_HOME=(C:\jdk1.7.0 or whatever your home java directory is)
PATH=%PATH%C:\ant\bin
```
I would recommend setting these variables in your system environment variables so you don't need to reset them every time you restart the command prompt. Make sure to restart the command prompt after setting system variables. If it's set up right, type `ant` on the command line. You should get this:```
```
Buildfile: build.xml does not exist!
Build failed                        
```
If you get that, you're good to go.

If it's not linked properly, command will say this
```
'ant' is not recognized as an internal or external command,
operable program or batch file.
```


* Get NUnit

Go to http://nunit.org/. Under downloads, download and run the .msi. A typical install will do the trick. There are no additional steps needed for setting NUinit up.


* Get MSBuildTasks

Go to https://code.google.com/p/msbuildtasks/downloads/list and download the .msi. Let it run and it'll set up for you. You won't need to do anything else with this.


* Set up the WiX Toolset

You'll need WiX version 3.5. The build will accept nothing but this. Go to https://wix.codeplex.com/releases/view/60102 and download the .msi. It will handle the setup for you so all you have to do is download and run.


* Setting up Git

If you don't have it already, you'll need to setup git. Download from http://git-scm.com/downloads.

During the installation, the wizard will offer to adjust your PATH environment. Select the second option "Run Git from the Windows Command Prompt". This will set up the proper path variables the command prompt and the msbuild need to run properly. The third option also works.



* Building the project with `build all`

Now you should have everything properly set up and linked in your PATH. Give your command prompt a restart to make sure it recognizes all the new variables.

Navigate to the top folder of where you saved your Waffle source code. For me, the top folder is `E:\waffle 1.5 source`. run `build all` on the command line and let it work it's magic. I've placed some error messages below to help troubleshoot. The error messages show up as bright red text so they're easy to spot.


* Build error messages

If NUnit fails, you'll get this message
```
Missing NUnit, expected in
```


If git fails, you'll get a message like this
```
E:\Waffle.proj(2,11): error MSB4019: The imported project "C:\Program Files\MSBuild\MSBuildCommunityTasks\MSBuild.Community.Tasks.Targets" was not found. Confirm that the path in the <Import> declaration is correct, and that the file exists on disk.
```

If WiX is missing, you'll get this message
```
"E:\waffle 1.5 source\Waffle.proj" (all target) (1) ->
"E:\waffle 1.5 source\Waffle.sln" (Clean target) (2) ->
(Clean target) ->  E:\waffle 1.5 source\Source\WindowsAuthProviderMergeModule\WindowsAuthProviderMergeModule.wixproj(107,11): error MSB4019: The imported project "C:\Program Files\MSBuild\Microsoft\WiX\v3.x\Wix.targets" was not found. Confirm that the path in the <Import> declaration is correct, and that the file exists on disk.
```

* Where did my jar files go?

The jar files should show up in `(waffle source)\target\Release\Waffle\Bin` but it doesn't always seem to work that way. If they aren't there, never fear. There is another way to get them.

Navigate to your waffle directory then `(waffle source)\Source\JNA\bin`. now run `ant`.


If you try to run an ant buildfile before running build all, you might get a message like this
```
BUILD FAILED
E:\waffle 1.5 source\Source\JNA\build.xml:16: The following error occurred while executing this line:
E:\waffle 1.5 source\Source\JNA\build.xml:4: The following error occurred while executing this line:
E:\waffle 1.5 source\Source\JNA\waffle-jna\build.xml:5: The following error occurred while executing this line:
E:\waffle 1.5 source\Source\JNA\build\build.xml:6: Missing product.version
```
Ant requires you to run `build all` first so it properly sets the version number. If `build all` fails, you'll end up with a message like this if you try to run ant. Go back and rerun `build all` from the previous step or fix any errors it has.


There is another error you might get:
```
BUILD FAILED
E:\Source\JNA\build.xml:16: The following error occurred while executing this line:
E:\Source\JNA\build.xml:5: The following error occurred while executing this line:
E:\Source\JNA\build\build.xml:232: The following error occurred while executing this line:
E:\Source\JNA\build\build.xml:127: Unit Tests failed
```
There's a problem with the Unit Tests, and ant requires them to complete properly before it will build your jars. If you see this error message, instead of `ant`, run `ant -DskipTests=true`. This tells ant to skip the tests and your jar files will build properly. Your jars will show up in `(waffle source)\Source\JNA\bin`.

And there you go! There are your jars.

* Eclipse still is telling me I have build path errors!

The solution to that is actually pretty simple. Eclipse is kinda dumb and doesn't import all the lib files you made during `build all`, despite them being in the proper working directory. So here's what to do.

Click on the project named `thirdparty` and hit F5. This will refresh the project. All the other projects rely on this one, so it needs to have the _lib folder recognized by Eclipse. At first it doesn't look like it worked, but notice that the progress bar in the bottom right corner is going nuts. Eclipse is refreshing and rebuilding all linked projects. Once that's done, that should solve the Eclipse issue.
