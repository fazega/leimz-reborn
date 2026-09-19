param([switch]$Check)

. "$PSScriptRoot/common.ps1"
$toolsDirectory = Join-Path $RepoRoot '.tools'
New-Item -ItemType Directory -Force $toolsDirectory | Out-Null
$javaFormatter = Join-Path $toolsDirectory 'google-java-format-1.7.jar'
$expectedHash = '0894ee02019ee8b4acd6df09fb50bac472e7199e1a5f041f8da58d08730694aa'
if (-not (Test-Path -LiteralPath $javaFormatter)) {
    Invoke-WebRequest 'https://repo.maven.apache.org/maven2/com/google/googlejavaformat/google-java-format/1.7/google-java-format-1.7-all-deps.jar' -OutFile $javaFormatter
}
if ((Get-FileHash -LiteralPath $javaFormatter -Algorithm SHA256).Hash -ne $expectedHash) {
    throw 'Java formatter checksum mismatch. Remove .tools/google-java-format-1.7.jar and retry.'
}
$modulePath = Join-Path $toolsDirectory 'PSScriptAnalyzer/1.25.0/PSScriptAnalyzer.psd1'
if (-not (Test-Path -LiteralPath $modulePath)) {
    Save-Module PSScriptAnalyzer -RequiredVersion 1.25.0 -Path $toolsDirectory -Repository PSGallery -Force
}
Import-Module $modulePath -Force
$prettier = Join-Path $RepoRoot 'node_modules/prettier/bin/prettier.cjs'
if (-not (Test-Path -LiteralPath $prettier)) {
    throw 'Install formatting dependencies first: npm ci --ignore-scripts'
}

Push-Location $RepoRoot
try {
    $javaFiles = @(Get-ChildItem Client, Server, MapEditor -Recurse -Filter '*.java' |
            Where-Object { $_.FullName -notmatch '[\\/]build[\\/]' } |
            Sort-Object FullName |
            ForEach-Object { [IO.Path]::GetRelativePath($RepoRoot, $_.FullName).Replace('\', '/') })
    $argumentFile = Join-Path $toolsDirectory 'java-format.args'
    [IO.File]::WriteAllLines($argumentFile, $javaFiles, [Text.UTF8Encoding]::new($false))
    $formatterOptions = @(if ($Check) { '--dry-run'; '--set-exit-if-changed' } else { '--replace' })
    & (Get-JavaTool java) -jar $javaFormatter --aosp --skip-sorting-imports --skip-removing-unused-imports @formatterOptions '@.tools/java-format.args'
    if ($LASTEXITCODE -ne 0) { throw 'Java formatting failed. Run ./Format.ps1 to apply formatting.' }
    Write-Host "Java: $($javaFiles.Count) files formatted consistently."

    $scripts = @(Get-ChildItem -Path '.', 'Client', 'Server', 'MapEditor', 'Website' -Filter '*.ps1' -File)
    $unformatted = @()
    foreach ($script in $scripts) {
        $original = [IO.File]::ReadAllText($script.FullName).Replace("`r`n", "`n")
        $formatted = (Invoke-Formatter -ScriptDefinition $original).Replace("`r`n", "`n").TrimEnd() + "`n"
        if ($original -ne $formatted) {
            if ($Check) { $unformatted += $script.FullName }
            else { [IO.File]::WriteAllText($script.FullName, $formatted, [Text.UTF8Encoding]::new($false)) }
        }
    }
    if ($unformatted.Count) { throw "PowerShell formatting required: $($unformatted -join ', '). Run ./Format.ps1." }
    Write-Host "PowerShell: $($scripts.Count) files formatted consistently."

    $prettierMode = if ($Check) { '--check' } else { '--write' }
    & (Get-Setting node 'node') $prettier $prettierMode .
    if ($LASTEXITCODE -ne 0) { throw 'Prettier formatting failed. Run ./Format.ps1 to apply formatting.' }
}
finally {
    Pop-Location
}
