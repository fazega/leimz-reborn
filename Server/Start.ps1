param([switch]$Build)
. "$PSScriptRoot\..\common.ps1"
if ($Build -or -not (Test-Path "$PSScriptRoot\build\classes\com\server\core\Main.class")) { & "$RepoRoot\Build.ps1" Server }
$dbUrl = Get-Setting 'dbUrl' $env:LEIMZ_DB_URL
$dbUser = Get-Setting 'dbUser' $env:LEIMZ_DB_USER
$dbPassword = Get-Setting 'dbPassword' $env:LEIMZ_DB_PASSWORD
if (-not $dbUrl -or -not $dbUser -or -not $dbPassword) { throw 'Configure database settings in local.settings.json first; see Server/README.md.' }
$port = Get-Setting 'gamePort' 1500
if (Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue) { throw "Port $port is already in use." }
New-Item -ItemType Directory -Force "$PSScriptRoot\logs" | Out-Null
$argsList = @('-Dfile.encoding=ISO-8859-15', "-Dleimz.port=$port", "`"-Dleimz.db.url=$dbUrl`"", "`"-Dleimz.db.user=$dbUser`"", "`"-Dleimz.db.password=$dbPassword`"", '-cp', "`"$PSScriptRoot\build\classes;$PSScriptRoot\lib\*`"", 'com.server.core.Main')
$process = Start-Process (Get-JavaTool 'java') -ArgumentList $argsList -WorkingDirectory $PSScriptRoot -WindowStyle Hidden -PassThru -RedirectStandardOutput "$PSScriptRoot\logs\server.log" -RedirectStandardError "$PSScriptRoot\logs\server-error.log"
$process.Id | Set-Content "$PSScriptRoot\logs\server.pid"
Write-Host "Server started: PID $($process.Id), localhost:$port. Logs: Server/logs."
