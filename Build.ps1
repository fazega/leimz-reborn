param([ValidateSet('All','Server','Client')][string]$Component = 'All')
. "$PSScriptRoot\common.ps1"
$javac = Get-JavaTool 'javac'
$components = if ($Component -eq 'All') { @('Server','Client') } else { @($Component) }
foreach ($name in $components) {
    $folder = Join-Path $RepoRoot $name
    $build = Join-Path $folder 'build'
    $classes = Join-Path $build 'classes'
    New-Item -ItemType Directory -Force -Path $classes | Out-Null
    $sources = Get-ChildItem -LiteralPath "$folder\src\main\java" -Filter '*.java' -Recurse | ForEach-Object { '"' + $_.FullName.Replace('\','/') + '"' }
    $list = Join-Path $build 'sources.txt'
    [IO.File]::WriteAllLines($list, $sources, [Text.UTF8Encoding]::new($false))
    & $javac '-encoding' 'UTF-8' '-source' '8' '-target' '8' '-cp' "$folder\lib\*" '-d' $classes "@$list"
    if ($LASTEXITCODE -ne 0) { throw "$name compilation failed." }
    Write-Host "$name compiled successfully."
}
