param([switch]$Integration)
. "$PSScriptRoot\common.ps1"
& "$RepoRoot\Build.ps1"
& "$RepoRoot\Client\Test-Fast.ps1"
& "$RepoRoot\Server\Test-Fast.ps1"
$node = Get-Setting 'node' 'node'
& $node --test "$RepoRoot\Test-Content.cjs"
if ($LASTEXITCODE -ne 0) { throw 'Content preservation tests failed.' }
& "$RepoRoot\MapEditor\Test-Content.ps1"
& $node --test "$RepoRoot\Website\tests\website.test.cjs"
if ($LASTEXITCODE -ne 0) { throw 'Website tests failed.' }
if ($Integration) {
    & "$RepoRoot\Client\Test-Recovery.ps1"
    & "$RepoRoot\MapEditor\Test-Recovery.ps1"
}
Write-Host 'All requested checks passed.'
