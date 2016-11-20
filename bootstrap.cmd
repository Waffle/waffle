@echo off
Rmdir .\Tools\ /s /q
NuGet.exe install MSBuildTasks -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive
NuGet.exe install NUnit -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive -Version 3.4.1
NuGet.exe install NUnit.ConsoleRunner -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive -Version 3.4.1
NuGet.exe install WiX -Pre -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive