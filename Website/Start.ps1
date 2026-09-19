. "$PSScriptRoot\..\common.ps1"
& (Get-Setting 'node' 'node') "$PSScriptRoot\server.cjs"
