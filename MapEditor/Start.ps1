param([switch]$Build)
. "$PSScriptRoot\..\common.ps1"
if ($Build -or -not (Test-Path "$PSScriptRoot\build\classes\com\loop\Base.class")) {
    & "$RepoRoot\Build.ps1" -Component MapEditor
}
New-Item -ItemType Directory -Force "$PSScriptRoot\logs" | Out-Null
$arguments = @('-Dfile.encoding=UTF-8', "`"-Djava.library.path=$PSScriptRoot\lib`"", '-cp', "`"$PSScriptRoot\build\classes;$PSScriptRoot\lib\*`"", 'com.loop.Base')
$process = Start-Process (Get-JavaTool 'javaw') -ArgumentList $arguments -WorkingDirectory $PSScriptRoot -WindowStyle Normal -PassThru -RedirectStandardOutput "$PSScriptRoot\logs\editor.log" -RedirectStandardError "$PSScriptRoot\logs\editor-error.log"
$process.Id | Set-Content "$PSScriptRoot\logs\editor.pid"
Write-Host "Map editor opened: PID $($process.Id)."
