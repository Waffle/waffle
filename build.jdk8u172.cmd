@echo off
@setlocal
if "%JDK172_HOME%" == "" (
    set JDK127_HOME=d:\Dev\Java\jdk1.8.0_172
)
@set JAVA_HOME=%JDK127_HOME%
@set PATH=%JAVA_HOME%;%PATH%

@call build.cmd all

@endlocal
@echo on
