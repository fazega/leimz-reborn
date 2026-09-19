. "$PSScriptRoot\..\common.ps1"
$classes = "$PSScriptRoot\build\classes"
$tests = "$PSScriptRoot\build\tests"
New-Item -ItemType Directory -Force $tests | Out-Null
& (Get-JavaTool 'javac') -encoding UTF-8 -cp "$classes;$PSScriptRoot\lib\*" -d $tests "$PSScriptRoot\tests\MapGeometryTest.java" "$PSScriptRoot\tests\NetworkQueueTest.java" "$PSScriptRoot\tests\CharacterMotionTest.java" "$PSScriptRoot\tests\PlayerSessionTest.java"
if ($LASTEXITCODE -ne 0) { throw 'Geometry test compilation failed.' }
& (Get-JavaTool 'java') '-Djava.awt.headless=true' -cp "$tests;$classes;$PSScriptRoot\lib\*" MapGeometryTest
if ($LASTEXITCODE -ne 0) { throw 'Geometry tests failed.' }
& (Get-JavaTool 'java') '-Djava.awt.headless=true' -cp "$tests;$classes;$PSScriptRoot\lib\*" NetworkQueueTest
if ($LASTEXITCODE -ne 0) { throw 'Network queue tests failed.' }
& (Get-JavaTool 'java') '-Djava.awt.headless=true' -cp "$tests;$classes;$PSScriptRoot\lib\*" CharacterMotionTest "$PSScriptRoot\data\Images\Persos\reborn\adventurer.png"
if ($LASTEXITCODE -ne 0) { throw 'Character animation tests failed.' }

& (Get-JavaTool 'java') '-Djava.awt.headless=true' -cp "$tests;$classes;$PSScriptRoot\lib\*" PlayerSessionTest
if ($LASTEXITCODE -ne 0) { throw 'Player session tests failed.' }
