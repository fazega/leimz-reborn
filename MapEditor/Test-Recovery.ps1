param([switch]$Build)
. "$PSScriptRoot\..\common.ps1"
& "$PSScriptRoot\Test-Content.ps1"
if ($Build -or -not (Test-Path "$PSScriptRoot\build\classes\com\loop\Base.class")) {
    & "$RepoRoot\Build.ps1" -Component MapEditor
}
$classes = "$PSScriptRoot\build\test-classes"
New-Item -ItemType Directory -Force $classes | Out-Null
& (Get-JavaTool 'javac') -encoding UTF-8 -cp "$PSScriptRoot\build\classes;$PSScriptRoot\lib\*" -d $classes "$PSScriptRoot\src\test\java\EditorRecoveryTest.java"
if ($LASTEXITCODE -ne 0) { throw 'Editor test compilation failed.' }
Push-Location $PSScriptRoot
try {
    & (Get-JavaTool 'java') "-Djava.library.path=$PSScriptRoot\lib" -cp "$classes;$PSScriptRoot\build\classes;$PSScriptRoot\lib\*" EditorRecoveryTest
    if ($LASTEXITCODE -ne 0) { throw 'Editor recovery test failed.' }
}
finally { Pop-Location }
