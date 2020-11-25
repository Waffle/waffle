@echo off
Rmdir .nuget\packages\ /s /q
NuGet.exe install MSBuildTasks        -OutputDirectory .nuget\packages\ -NonInteractive -Version 1.5.0.214
NuGet.exe install NUnit               -OutputDirectory .nuget\packages\ -NonInteractive -Version 3.5.0
NuGet.exe install NUnit.ConsoleRunner -OutputDirectory .nuget\packages\ -NonInteractive -Version 3.5.0
NuGet.exe install WiX                 -OutputDirectory .nuget\packages\ -NonInteractive -Version 4.0.0.4506-pre -Pre
