@echo off

if "%~1"=="" ( 
 call :Usage
 goto :EOF
)

pushd "%~dp0"
setlocal ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION

for /D %%n in ( "%ProgramFiles%\NUnit*" ) do (
 set NUnitDir=%%~n
)

if NOT EXIST "%NUnitDir%" (
 echo Missing NUnit, expected in %NUnitDir%
 exit /b -1
)

set VisualStudioCmd=%ProgramFiles%\Microsoft Visual Studio 8.0\VC\vcvarsall.bat

if EXIST "%VisualStudioCmd%" ( 
 call "%VisualStudioCmd%"
)

set FrameworkVersion=v2.0.50727
set FrameworkDir=%SystemRoot%\Microsoft.NET\Framework

PATH=%FrameworkDir%\%FrameworkVersion%;%NUnitDir%;%SvnDir%;%DoxygenDir%;%JAVA_HOME%\bin;%PATH%
msbuild.exe Waffle.proj /t:%*
if NOT %ERRORLEVEL%==0 exit /b %ERRORLEVEL%
popd
endlocal
exit /b 0
goto :EOF

:Usage
echo  Syntax:
echo.
echo   build [target] /p:Configuration=[Debug (default),Release]
echo.
echo  Target:
echo.
echo   all : build everything
echo.
echo  Examples:
echo.
echo   build all
echo   build all /p:Configuration=Release
goto :EOF
