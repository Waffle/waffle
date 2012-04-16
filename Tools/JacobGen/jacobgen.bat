@echo off
setlocal

set JRE=%JAVA_HOME%\bin\java.exe
set JACOBGEN_HOME=%~dp0

set CLASSPATH=%JACOBGEN_HOME%jacobgen.jar;%JACOBGEN_HOME%lib\viztool.jar;%JACOBGEN_HOME%lib\samskivert.jar

rem if NOT "%PROCESSOR_ARCHITEW6432%"=="" (
rem  set PROCESSOR_ARCHITECTURE=%PROCESSOR_ARCHITEW6432%
rem )

set PATH=%JACOBGEN_HOME%\%PROCESSOR_ARCHITECTURE%;%PATH%

"%JRE%" -Xint com.jacob.jacobgen.Jacobgen %1 %2 %3 %4 %5
endlocal
