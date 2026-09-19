. "$PSScriptRoot\..\common.ps1"
$classes = "$PSScriptRoot\build\classes"
$tests = "$PSScriptRoot\build\tests"
New-Item -ItemType Directory -Force $tests | Out-Null
& (Get-JavaTool 'javac') -encoding UTF-8 -cp "$classes;$PSScriptRoot\lib\*" -d $tests "$PSScriptRoot\tests\MapBoundaryTest.java"
if ($LASTEXITCODE -ne 0) { throw 'Server test compilation failed.' }
& (Get-JavaTool 'java') -cp "$tests;$classes;$PSScriptRoot\lib\*" MapBoundaryTest
if ($LASTEXITCODE -ne 0) { throw 'Server boundary tests failed.' }
