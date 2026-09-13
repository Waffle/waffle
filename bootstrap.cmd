@echo off
if exist .nuget\packages\ rmdir .nuget\packages\ /s /q

NuGet.exe install MSBuildTasks        -OutputDirectory .nuget\packages\ -NonInteractive -Version 1.5.0.235
NuGet.exe install NUnit               -OutputDirectory .nuget\packages\ -NonInteractive -Version 4.6.1
NuGet.exe install NUnit.ConsoleRunner -OutputDirectory .nuget\packages\ -NonInteractive -Version 3.22.0

dotnet tool restore
