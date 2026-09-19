. "$PSScriptRoot\..\common.ps1"
$classes = "$PSScriptRoot\build\classes"
$tests = "$PSScriptRoot\build\tests"
New-Item -ItemType Directory -Force $tests | Out-Null
& (Get-JavaTool 'javac') -encoding UTF-8 -cp "$classes;$PSScriptRoot\lib\*" -d $tests "$PSScriptRoot\tests\AccountCharacterTest.java"
if ($LASTEXITCODE -ne 0) { throw 'Account test compilation failed.' }
$dbUrl = Get-Setting 'dbUrl' $env:LEIMZ_DB_URL
$dbUser = Get-Setting 'dbUser' $env:LEIMZ_DB_USER
$dbPassword = Get-Setting 'dbPassword' $env:LEIMZ_DB_PASSWORD
& (Get-JavaTool 'java') "-Dleimz.db.url=$dbUrl" "-Dleimz.db.user=$dbUser" "-Dleimz.db.password=$dbPassword" -cp "$tests;$classes;$PSScriptRoot\lib\*" AccountCharacterTest
if ($LASTEXITCODE -ne 0) { throw 'Account character tests failed.' }
