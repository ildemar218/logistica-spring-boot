# Arranca el sistema de logística leyendo las credenciales de MariaDB desde .env
# Uso (desde la carpeta logistica):  powershell -ExecutionPolicy Bypass -File .\iniciar.ps1

Set-Location $PSScriptRoot

if (-not (Test-Path ".env")) {
    Write-Host "Falta el archivo .env. Copia .env.example como .env y escribe tus credenciales." -ForegroundColor Red
    exit 1
}

Get-Content ".env" | ForEach-Object {
    $linea = $_.Trim()
    if ($linea -and -not $linea.StartsWith("#") -and $linea.Contains("=")) {
        $nombre, $valor = $linea.Split("=", 2)
        Set-Item -Path "Env:$($nombre.Trim())" -Value $valor.Trim()
    }
}

Write-Host "Iniciando en http://localhost:8080 ... (Ctrl+C para detener)" -ForegroundColor Cyan
.\mvnw.cmd spring-boot:run
