param([switch]$Borders)
. "$PSScriptRoot\..\common.ps1"
$classes = "$PSScriptRoot\build\classes"
$testDir = "$PSScriptRoot\build\tests"
New-Item -ItemType Directory -Force $testDir | Out-Null
& (Get-JavaTool 'javac') '-encoding' 'UTF-8' '-cp' "$classes;$PSScriptRoot\lib\*" '-d' $testDir "$PSScriptRoot\tests\RecoverySmokeTest.java"
if ($LASTEXITCODE -ne 0) { throw 'Integration test compilation failed.' }
$port = Get-Setting 'gamePort' 1500

$argsList = @('-Dfile.encoding=ISO-8859-15', "-Dleimz.port=$port", '-Dtest.user=player', '-Dtest.password=leimz-local', "`"-Dtest.output=$testDir`"", "`"-Djava.library.path=$PSScriptRoot\lib`"", '-cp', "`"$testDir;$classes;$PSScriptRoot\lib\*`"", 'RecoverySmokeTest')
if ($Borders) { $argsList = @('-Dtest.borders=true') + $argsList }
$process = Start-Process (Get-JavaTool 'java') -ArgumentList $argsList -WorkingDirectory $PSScriptRoot -WindowStyle Hidden -PassThru -RedirectStandardOutput "$testDir\smoke.log" -RedirectStandardError "$testDir\smoke-error.log"
if (-not $process.WaitForExit(60000)) { $process.Kill(); throw 'Integration test timed out. See Client/build/tests.' }
$log = Get-Content "$testDir\smoke.log" -Raw
if ($log -notmatch 'BARREL_BLOCKED' -or $log -notmatch 'VERIFIED_NPC_LOADED_AFTER_POSITION_SYNC') { throw 'Recovery integration test failed. See Client/build/tests logs.' }
Write-Host 'PASS: demo login, map rendering, barrel collision and NPC discovery.'
if ($log -notmatch 'VERIFIED_CHAT_FOCUS_AND_SHORTCUT_RELEASE' -or $log -notmatch 'VERIFIED_RESIZE=1280x800') { throw 'Chat focus or resizing regression failed.' }
if ($Borders -and $log -notmatch 'VERIFIED_ALL_EIGHT_BORDER_VIEWS') { throw 'Border visual test failed.' }
