$ErrorActionPreference = 'Stop'
$RepoRoot = $PSScriptRoot
$settingsPath = Join-Path $RepoRoot 'local.settings.json'
$Settings = if (Test-Path -LiteralPath $settingsPath) { Get-Content -LiteralPath $settingsPath -Raw | ConvertFrom-Json } else { [pscustomobject]@{} }
function Get-Setting([string]$Name, $Default) {
    if ($Settings.PSObject.Properties.Name -contains $Name -and $Settings.$Name) { return $Settings.$Name }
    return $Default
}
function Get-JavaTool([string]$Name) {
    $jdk = Get-Setting 'javaHome' $env:JAVA_HOME
    if ($jdk) { $tool = Join-Path $jdk "bin\$Name.exe"; if (Test-Path -LiteralPath $tool) { return $tool } }
    $found = Get-Command $Name -ErrorAction SilentlyContinue
    if ($found) { return $found.Source }
    throw 'Install JDK 8 and set JAVA_HOME or javaHome in local.settings.json.'
}
