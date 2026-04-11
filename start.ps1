$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendPath = Join-Path $repoRoot "backend"
$dashboardPath = Join-Path $repoRoot "dashboard"

if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
    throw "npm is not installed or not available in PATH."
}

if (-not (Test-Path $backendPath -PathType Container)) {
    throw "Backend folder not found at: $backendPath"
}

if (-not (Test-Path $dashboardPath -PathType Container)) {
    throw "Dashboard folder not found at: $dashboardPath"
}

$backendEnvPath = Join-Path $backendPath ".env"
if (-not (Test-Path $backendEnvPath -PathType Leaf)) {
    throw "Missing backend env file at: $backendEnvPath"
}

$backendEnv = Get-Content $backendEnvPath -Raw
if ($backendEnv -match '<YOUR_DB_PASSWORD>') {
    Write-Host "DATABASE_URL in backend/.env still has <YOUR_DB_PASSWORD>."

    $dbPassword = $env:SUPABASE_DB_PASSWORD
    if ([string]::IsNullOrWhiteSpace($dbPassword)) {
        $secureDbPassword = Read-Host "Enter Supabase DB password" -AsSecureString
        $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureDbPassword)
        try {
            $dbPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
        }
        finally {
            [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
        }
    }

    if ([string]::IsNullOrWhiteSpace($dbPassword)) {
        throw "Supabase DB password is required to complete DATABASE_URL in backend/.env"
    }

    $encodedDbPassword = [Uri]::EscapeDataString($dbPassword)
    $backendEnv = $backendEnv -replace '<YOUR_DB_PASSWORD>', $encodedDbPassword
    Set-Content -Path $backendEnvPath -Value $backendEnv -Encoding UTF8
}

$backendEnv = Get-Content $backendEnvPath -Raw
if ([regex]::IsMatch($backendEnv, '(?m)^\s*DATABASE_URL\s*=\s*"?\s*"?\s*$') -or $backendEnv -match '<YOUR_DB_PASSWORD>') {
    throw "backend/.env DATABASE_URL is incomplete. Add your Supabase DB password in DATABASE_URL and rerun ./start.ps1"
}

Write-Host "Starting backend server..."
Start-Process -FilePath "powershell.exe" -ArgumentList "-NoExit", "-Command", "Set-Location '$backendPath'; npm run dev"

Write-Host "Starting dashboard server..."
Start-Process -FilePath "powershell.exe" -ArgumentList "-NoExit", "-Command", "Set-Location '$dashboardPath'; npm run dev"

Write-Host "Services started in separate terminals."
Write-Host "Backend: http://localhost:8080"
Write-Host "Dashboard: check Vite output (usually http://localhost:5173)"