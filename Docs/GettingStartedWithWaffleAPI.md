Getting Started with Waffle API
===============================

This library publishes several Windows authentication, authorization and membership interfaces.

Getting Started in C#
---------------------

Reference `Waffle.Windows.AuthProvider.dll`. Create a new instance of `Waffle.Windows.AuthProvider.WindowsAuthProviderImpl`.

``` csharp
WindowsAuthProviderImpl waffle = new WindowsAuthProviderImpl();
IWindowsAccount admin = waffle.LookupAccount("Administrator");
Console.WriteLine(admin.SidString);
Console.WriteLine(admin.Fqn);
```

Getting Started with VBScript and COM
-------------------------------------

Register the authentication provider DLL as follows. 

``` shell
regasm.exe /codebase /tlb:Waffle.Windows.AuthProvider.tlb Waffle.Windows.AuthProvider.dll
```

Create a new instance of `Waffle.Windows.AuthProvider.WindowsAuthProviderImpl`.

``` vbscript
set windowsAuthProviderImpl = CreateObject("Waffle.Windows.AuthProvider")
```

Getting Started in Java
-----------------------

Add `waffle-jna.jar`, `jna-3.5.0.jar`, `platform-3.5.0.jar`, `guava-13.0.jar`, and `slf4j-api-1.6.6.jar` to your `CLASSPATH` or, if you use Maven, add the following to your `pom.xml`.

``` xml
<properties>
    <waffle.version>1.5</waffle.version>
</properties>

<dependency>
    <groupId>com.github.dblock.waffle</groupId>
    <artifactId>waffle-jna</artifactId>
    <version>${waffle.version}</version>
</dependency>
```

Create a new instance of `waffle.windows.auth.impl.WindowsAuthProviderImpl`.
