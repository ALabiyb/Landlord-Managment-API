# Complete End-to-End API Testing Script - CORRECTED
# Full workflow: Register → Login → House → Room → Tenant → Lease

Write-Host "=== Complete End-to-End Workflow Testing ===" -ForegroundColor Cyan

# 1. Login
Write-Host "`n1. Logging in..." -ForegroundColor Yellow
$loginBody = Get-Content test_login.json -Raw
$loginResponse = Invoke-WebRequest -Uri http://localhost:8082/api/v1/auth/login -Method POST -Body $loginBody -ContentType "application/json"
$token = ($loginResponse.Content | ConvertFrom-Json).token
$headers = @{ "Authorization" = "Bearer $token" }
Write-Host "✓ Login successful" -ForegroundColor Green

# 2. Get house ID
Write-Host "`n2. Getting house ID..." -ForegroundColor Yellow
$housesResponse = Invoke-WebRequest -Uri http://localhost:8082/api/v1/houses -Method GET -Headers $headers
$houseId = (($housesResponse.Content | ConvertFrom-Json).data.data[0]).id
Write-Host "✓ House ID: $houseId" -ForegroundColor Green

# 3. Create a room (WITH houseId)
Write-Host "`n3. Creating a room..." -ForegroundColor Yellow
$roomBody = @{
  houseId = $houseId
  roomNumber = "101"
  monthlyRent = 350000
  size = "45.5 sqm"
  description = "Cozy one bedroom apartment with balcony"
} | ConvertTo-Json

try {
  $roomResponse = Invoke-WebRequest -Uri "http://localhost:8082/api/v1/rooms" -Method POST -Body $roomBody -ContentType "application/json" -Headers $headers
  $roomId = ($roomResponse.Content | ConvertFrom-Json).data.id
  Write-Host "✓ Room created successfully!" -ForegroundColor Green
  Write-Host "  Room ID: $roomId" -ForegroundColor Cyan
  Write-Output $roomResponse.Content
} catch {
  Write-Host "✗ Room creation failed" -ForegroundColor Red
  $errorResponse = $_.ErrorDetails.Message
  Write-Output $errorResponse
  $roomId = $null
}

# 4. Register a tenant
Write-Host "`n4. Registering a tenant..." -ForegroundColor Yellow
$tenantBody = @{
  firstName = "Jane"
  lastName = "Smith"
  email = "jane.smith@example.com"
  phoneNumber = "+255723456789"
  nationalId = "19920303-98765-98765-98"
  emergencyContact = "+255734567890"
  occupation = "Software Engineer"
} | ConvertTo-Json

try {
  $tenantResponse = Invoke-WebRequest -Uri http://localhost:8082/api/v1/tenants -Method POST -Body $tenantBody -ContentType "application/json" -Headers $headers
  $tenantId = ($tenantResponse.Content | ConvertFrom-Json).data.id
  Write-Host "✓ Tenant created successfully!" -ForegroundColor Green
  Write-Host "  Tenant ID: $tenantId" -ForegroundColor Cyan
  Write-Output $tenantResponse.Content
} catch {
  Write-Host "✗ Tenant creation failed" -ForegroundColor Red
  $errorResponse = $_.ErrorDetails.Message
  Write-Output $errorResponse
  $tenantId = $null
}

# 5. Create a lease
if ($roomId -and $tenantId) {
  Write-Host "`n5. Creating a lease..." -ForegroundColor Yellow
  $leaseBody = @{
    roomId = $roomId
    tenantId = $tenantId
    startDate = "2025-01-01"
    endDate = "2025-12-31"
    monthlyRent = 350000
    securityDeposit = 700000
    paymentDueDay = 5
    terms = "Standard lease terms and conditions for one year"
  } | ConvertTo-Json

  try {
    $leaseResponse = Invoke-WebRequest -Uri http://localhost:8082/api/v1/leases -Method POST -Body $leaseBody -ContentType "application/json" -Headers $headers
    $leaseId = ($leaseResponse.Content | ConvertFrom-Json).data.id
    Write-Host "✓ Lease created successfully!" -ForegroundColor Green
    Write-Host "  Lease ID: $leaseId" -ForegroundColor Cyan
    Write-Output $leaseResponse.Content
  } catch {
    Write-Host "✗ Lease creation failed" -ForegroundColor Red
    $errorResponse = $_.ErrorDetails.Message
    Write-Output $errorResponse
  }
} else {
  Write-Host "`n5. Skipping lease creation (room or tenant missing)" -ForegroundColor Yellow
}

# 6. Get updated dashboard stats
Write-Host "`n6. Getting updated dashboard stats..." -ForegroundColor Yellow
$dashboardResponse = Invoke-WebRequest -Uri http://localhost:8082/api/v1/dashboard/stats -Method GET -Headers $headers
Write-Host "✓ Dashboard stats retrieved" -ForegroundColor Green
Write-Output $dashboardResponse.Content

# 7. List all leases
if ($leaseId) {
  Write-Host "`n7. Listing all leases..." -ForegroundColor Yellow
  try {
    $leasesResponse = Invoke-WebRequest -Uri http://localhost:8082/api/v1/leases -Method GET -Headers $headers
    Write-Host "✓ Leases retrieved" -ForegroundColor Green
    Write-Output $leasesResponse.Content
  } catch {
    Write-Host "✗ Failed to retrieve leases" -ForegroundColor Red
  }
}

Write-Host "`n=== End-to-End Testing Complete ===" -ForegroundColor Cyan
Write-Host "`nSummary:" -ForegroundColor Yellow
Write-Host "  House ID: $houseId" -ForegroundColor Cyan
Write-Host "  Room ID: $roomId" -ForegroundColor Cyan
Write-Host "  Tenant ID: $tenantId" -ForegroundColor Cyan
if ($leaseId) {
  Write-Host "  Lease ID: $leaseId" -ForegroundColor Cyan
}
