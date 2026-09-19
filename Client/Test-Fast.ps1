. "$PSScriptRoot\..\common.ps1"
$classes = "$PSScriptRoot\build\classes"
$tests = "$PSScriptRoot\build\tests"
New-Item -ItemType Directory -Force $tests | Out-Null
& (Get-JavaTool 'javac') -encoding UTF-8 -cp "$classes;$PSScriptRoot\lib\*" -d $tests "$PSScriptRoot\tests\MapGeometryTest.java"
if ($LASTEXITCODE -ne 0) { throw 'Geometry test compilation failed.' }
& (Get-JavaTool 'java') '-Djava.awt.headless=true' -cp "$tests;$classes;$PSScriptRoot\lib\*" MapGeometryTest
if ($LASTEXITCODE -ne 0) { throw 'Geometry tests failed.' }
