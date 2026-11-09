$ErrorActionPreference = 'Stop'
Set-Location -Path $PSScriptRoot
$mvnCommand = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvnCommand) {
    & mvn spring-boot:run
} elseif (Test-Path 'C:\dev\apache-maven-3.10.0\bin\mvn.cmd') {
    & 'C:\dev\apache-maven-3.10.0\bin\mvn.cmd' spring-boot:run
} else {
    Write-Error 'Khong tim thay Maven. Them Maven bin vao PATH hoac chinh run-demo.ps1 theo duong dan cai Maven.'
}
