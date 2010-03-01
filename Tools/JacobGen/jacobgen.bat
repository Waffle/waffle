@echo off
setlocal

REM run this from the root directory of the Jacobgen project
REM it will spit out the interface classes for a dll you pass in as a parameter
REM sample command line while sitting in the JACOBGEN project directory
REM
REM The following command built a sample in the jacob directory I have 
REM installed near my jacobgen project directory.
REM $ docs/run_jacobgen.bat -destdir:"..\jacob\samples" -listfile:"jacobgenlog.txt" -package:com.jacobgen.microsoft.msword "C:\Program Files\Microsoft Office\OFFICE11\MSWORD.OLB"
REM 
REM

set JRE=%JAVA_HOME%\bin\java.exe
set JACOBGEN_HOME=.\
set CLASSPATH=%CLASSPATH%;%JACOBGEN_HOME%jacobgen.jar;%JACOBGEN_HOME%lib\viztool.jar;%JACOBGEN_HOME%lib\samskivert.jar

REM put the dll in the path where we can find it
set PATH=%PATH%;%JACOBGEN_HOME%\x86

"%JRE%" -Xint com.jacob.jacobgen.Jacobgen %1 %2 %3 %4 %5
endlocal