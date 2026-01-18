# Comprehensive API Testing Script
# This script tests all major endpoints of the Landlord Management API

Write-Host "=== Tanzania Landlord Management API - Comprehensive Testing ===" -ForegroundColor Cyan

# 1. Login and get token
Write-Host "`n1. Testing Login..." -ForegroundColor Yellow
$loginBody = Get-Content test_login.json -Raw
$loginResponse = Invoke-WebRequest -Uri http://localhost:8082/api/v1/auth/login -Method POST -Body $loginBody -ContentType "application/json"
$token = ($loginResponse.Content | ConvertFrom-Json).token
Write-Host "✓ Login successful" -ForegroundColor Green

# 2. Get existing house ID
Write-Host "`n2. Listing Houses..." -ForegroundColor Yellow
$headers = @{ "Authorization" = "Bearer $token" }
$housesResponse = Invoke-WebRequest -Uri http://localhost:8082/api/v1/houses -Method GET -Headers $headers
$houses = ($housesResponse.Content | ConvertFrom-Json).data
$houseId = $houses[0].id
Write-Host "✓ Found $($houses.Count) house(s)" -ForegroundColor Green
Write-Output $housesResponse.Content

# 3. Add a room to the house
Write-Host "`n3. Adding Room to House..." -ForegroundColor Yellow
$roomBody = @{
  roomNumber = "101"
  floor = 1
  roomType = "ONE_BEDROOM"
  monthlyRent = 350000
  size = 45.5
  description = "Cozy one bedroom apartment"
  status = "VACANT"
} | ConvertTo-Json
$roomResponse = Invoke-WebRequest -Uri "http://localhost:8082/api/v1/houses/$houseId/rooms" -Method POST -Body $roomBody -ContentType "application/json" -Headers $headers
Write-Host "✓ Room created successfully" -ForegroundColor Green
Write-Output $roomResponse.Content
$roomId = ($roomResponse.Content | ConvertFrom-Json).data.id

# 4. List rooms for the house
Write-Host "`n4. Listing Rooms for House..." -ForegroundColor Yellow
$roomsResponse = Invoke-WebRequest -Uri "http://localhost:8082/api/v1/houses/$houseId/rooms" -Method GET -Headers $headers
Write-Host "✓ Retrieved rooms list" -ForegroundColor Green
Write-Output $roomsResponse.Content

# 5. Get Dashboard Stats
Write-Host "`n5. Getting Dashboard Statistics..." -ForegroundColor Yellow
$dashboardResponse = Invoke-WebRequest -Uri http://localhost:8082/api/v1/dashboard/stats -Method GET -Headers $headers
Write-Host "✓ Dashboard stats retrieved" -ForegroundColor Green
Write-Output $dashboardResponse.Content

# 6. Test Refresh Token
Write-Host "`n6. Testing Refresh Token..." -ForegroundColor Yellow
$refreshToken = ($loginResponse.Content | ConvertFrom-Json).refreshToken
$refreshBody = @{ refreshToken = $refreshToken } | ConvertTo-Json
$refreshResponse = Invoke-WebRequest -Uri http://localhost:8082/api/v1/auth/refresh -Method POST -Body $refreshBody -ContentType "application/json"
Write-Host "✓ Token refreshed successfully" -ForegroundColor Green
Write-Output $refreshResponse.Content

Write-Host "`n=== All Tests Completed Successfully ===" -ForegroundColor Cyan
