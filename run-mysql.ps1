$ErrorActionPreference = 'Stop'
Set-Location -Path $PSScriptRoot
$env:DB_USER = if ($env:DB_USER) { $env:DB_USER } else { 'root' }
$env:DB_PASSWORD = if ($null -ne $env:DB_PASSWORD) { $env:DB_PASSWORD } else { '' }
$mvnCommand = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvnCommand) {
    & mvn spring-boot:run '-Dspring-boot.run.profiles=mysql'
} elseif (Test-Path 'C:\dev\apache-maven-3.10.0\bin\mvn.cmd') {
    & 'C:\dev\apache-maven-3.10.0\bin\mvn.cmd' spring-boot:run '-Dspring-boot.run.profiles=mysql'
} else {
    Write-Error 'Khong tim thay Maven. Them Maven bin vao PATH.'
}
