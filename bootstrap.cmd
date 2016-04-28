@echo off
Rmdir .\Tools\ /s /q
NuGet.exe install MSBuildTasks -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive
NuGet.exe install NUnit -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive
NuGet.exe install NUnit.Runners -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive
NuGet.exe install Wix.Toolset.2015 -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive