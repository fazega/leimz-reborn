$ErrorActionPreference = 'Stop'
$manifest = Get-Content "$PSScriptRoot\data-provenance.json" -Raw | ConvertFrom-Json
foreach ($entry in $manifest.files) {
    $file = Join-Path $PSScriptRoot $entry.path
    if ((Get-FileHash -LiteralPath $file -Algorithm SHA256).Hash -ne $entry.sha256) {
        throw "Recovered data changed: $($entry.path)"
    }
    if ($entry.path -ne 'data/Maps/types_tiles.xml' -and $entry.sha256 -ne $entry.archiveSha256) {
        throw "Unexpected archive difference: $($entry.path)"
    }
}
# Undo only the documented path substitutions and verify the exact archive bytes.
$xml = [IO.File]::ReadAllText("$PSScriptRoot\data\Maps\types_tiles.xml")
if ($manifest.pathChanges.Count -ne 11) { throw 'Expected exactly eleven portable image paths.' }
foreach ($change in $manifest.pathChanges) { $xml = $xml.Replace($change.after, $change.before) }
$hash = [Security.Cryptography.SHA256]::Create()
try {
    $encoding = [Text.UTF8Encoding]::new($true)
    [byte[]]$bytes = $encoding.GetPreamble() + $encoding.GetBytes($xml)
    $actual = [BitConverter]::ToString($hash.ComputeHash($bytes)).Replace('-', '').ToLower()
}
finally { $hash.Dispose() }
$expected = ($manifest.files | Where-Object path -eq 'data/Maps/types_tiles.xml').archiveSha256
if ($actual -ne $expected) { throw 'Tile catalogue has differences beyond the eleven image paths.' }
Write-Host "Editor content verified: $($manifest.files.Count) files; eleven path-only substitutions."
