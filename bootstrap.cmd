@echo off
Rmdir .nuget\packages\ /s /q
NuGet.exe install MSBuildTasks        -OutputDirectory .nuget\packages\ -NonInteractive -Version 1.5.0.235
NuGet.exe install NUnit               -OutputDirectory .nuget\packages\ -NonInteractive -Version 3.13.0
NuGet.exe install NUnit.ConsoleRunner -OutputDirectory .nuget\packages\ -NonInteractive -Version 3.12.0
NuGet.exe install WiX                 -OutputDirectory .nuget\packages\ -NonInteractive -Version 4.0.0.5512-pre -Pre
