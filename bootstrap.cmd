@echo off
Rmdir .\Tools\ /s /q
NuGet.exe install MSBuildTasks -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive
NuGet.exe install NUnit -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive
NuGet.exe install NUnit.Runners -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive
NuGet.exe install Wix.Toolset -OutputDirectory .\Tools\ -ExcludeVersion -NonInteractive