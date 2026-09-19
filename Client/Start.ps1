param([switch]$Build)
. "$PSScriptRoot\..\common.ps1"
if ($Build -or -not (Test-Path "$PSScriptRoot\build\classes\com\client\gamestates\Base.class")) { & "$RepoRoot\Build.ps1" Client }
New-Item -ItemType Directory -Force "$PSScriptRoot\logs" | Out-Null
$port = Get-Setting 'gamePort' 1500
$argsList = @('-Dfile.encoding=ISO-8859-15', "-Dleimz.port=$port", "`"-Djava.library.path=$PSScriptRoot\lib`"", '-cp', "`"$PSScriptRoot\build\classes;$PSScriptRoot\lib\*`"", 'com.client.gamestates.Base')
$process = Start-Process (Get-JavaTool 'javaw') -ArgumentList $argsList -WorkingDirectory $PSScriptRoot -WindowStyle Normal -PassThru -RedirectStandardOutput "$PSScriptRoot\logs\client.log" -RedirectStandardError "$PSScriptRoot\logs\client-error.log"
$process.Id | Set-Content "$PSScriptRoot\logs\client.pid"
Write-Host "Client opened: PID $($process.Id). Demo login: player / leimz-local."
