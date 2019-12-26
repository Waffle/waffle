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

Add `waffle-jna-2.1.0.jar`, `caffeine-2.8.0`, `jna-5.5.0.jar`, `jna-platform-5.5.0.jar`, and `slf4j-api-2.0.0-alpha1.jar` to your `CLASSPATH` or, if you use Maven, add the following to your `pom.xml`.

- For latest snapshot instead use `waffle-jna-2.1.1-SNAPSHOT`, `caffeine-2.8.0.jar`, `jna-5.5.0.jar`, `jna-platform-5.5.0.jar` and `slf4j-api-2.0.0-alpha1.jar`.

``` xml
<properties>
    <waffle.version>1.8.2</waffle.version>
</properties>

<dependency>
    <groupId>com.github.waffle</groupId>
    <artifactId>waffle-jna</artifactId>
    <version>${waffle.version}</version>
</dependency>
```

Create a new instance of `waffle.windows.auth.impl.WindowsAuthProviderImpl`.
