@echo off
Rmdir .nuget\packages\ /s /q
NuGet.exe install MSBuildTasks        -OutputDirectory .nuget\packages\ -NonInteractive -Version 1.5.0.235
NuGet.exe install NUnit               -OutputDirectory .nuget\packages\ -NonInteractive -Version 3.14.0
NuGet.exe install NUnit.ConsoleRunner -OutputDirectory .nuget\packages\ -NonInteractive -Version 3.17.0
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference = 'Stop'; $pkgId = 'WixToolset.Sdk'; $pkgIdLower = $pkgId.ToLowerInvariant(); $pkgVersion = '4.0.6'; $pkgFolder = Join-Path '.nuget\packages' ($pkgId + '.' + $pkgVersion); $pkgFile = Join-Path '.nuget\packages' ($pkgId + '.' + $pkgVersion + '.nupkg'); Invoke-WebRequest -UseBasicParsing -Uri ('https://api.nuget.org/v3-flatcontainer/' + $pkgIdLower + '/' + $pkgVersion + '/' + $pkgIdLower + '.' + $pkgVersion + '.nupkg') -OutFile $pkgFile; Expand-Archive -Path $pkgFile -DestinationPath $pkgFolder -Force; Remove-Item -Path $pkgFile -Force"
