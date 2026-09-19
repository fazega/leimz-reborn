. "$PSScriptRoot\..\common.ps1"
$testDir = "$PSScriptRoot\build\tests"
New-Item -ItemType Directory -Force $testDir | Out-Null
& (Get-JavaTool 'javac') -encoding UTF-8 -cp "$PSScriptRoot\build\classes;$PSScriptRoot\lib\*" -d $testDir "$PSScriptRoot\tests\AnimationPreviewTest.java"
if ($LASTEXITCODE -ne 0) { throw 'Animation preview compilation failed.' }
$previewArgs = @("`"-Dtest.output=$testDir`"", "`"-Djava.library.path=$PSScriptRoot\lib`"", '-cp', "`"$testDir;$PSScriptRoot\build\classes;$PSScriptRoot\lib\*`"", 'AnimationPreviewTest')
$preview = Start-Process (Get-JavaTool 'java') -ArgumentList $previewArgs -WorkingDirectory $PSScriptRoot -WindowStyle Hidden -PassThru -RedirectStandardOutput "$testDir\preview.log" -RedirectStandardError "$testDir\preview-error.log"
if (-not $preview.WaitForExit(15000)) { $preview.Kill(); throw 'Animation preview timed out.' }
if ($preview.ExitCode -ne 0) { throw 'Animation preview failed.' }
Write-Host "Animation contact sheet: $testDir\animation-proof.png"
