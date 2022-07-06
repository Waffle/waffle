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

Add `waffle-jna.jar`, `caffeine`, `jna.jar`, `jna-platform.jar`, and `slf4j-api.jar` to your `CLASSPATH` or, if you use Maven, add the following to your `pom.xml`.

- Use specific versions as bundled in waffle-distro

``` xml
<properties>
    <waffle.version>3.1.0</waffle.version>
</properties>

<dependency>
    <groupId>com.github.waffle</groupId>
    <artifactId>waffle-jna</artifactId>
    <version>${waffle.version}</version>
</dependency>
```

Create a new instance of `waffle.windows.auth.impl.WindowsAuthProviderImpl`.
