# Full End-to-End Test Script (Registration to Contract PDF)
# This script executes the entire lifecycle of a landlord managing a property.

Write-Host "=== Rental Management System - Full End-to-End Test ===" -ForegroundColor Cyan
$baseUrl = "http://localhost:8082/api/v1"
$ErrorActionPreference = "Stop"

# Helper to generate random string
function Get-RandomString {
    return -join ((65..90) + (97..122) | Get-Random -Count 5 | % {[char]$_})
}

# 1. Register a NEW Landlord
Write-Host "`n1. Registering new Landlord..." -ForegroundColor Yellow
$rand = Get-RandomString
$username = "landlord_$rand"
$email = "landlord_$rand@test.com"
$password = "Pass@123"

$registerBody = @{
    username = $username
    password = $password
    firstName = "Test"
    lastName = "Landlord"
    email = $email
    phoneNumber = "+2557" + (Get-Random -Minimum 10000000 -Maximum 99999999)
    nationalId = "NID-" + (Get-Random)
} | ConvertTo-Json

try {
    $regResponse = Invoke-WebRequest -Uri "$baseUrl/auth/register-landlord" -Method POST -Body $registerBody -ContentType "application/json"
    Write-Host "✓ Registered as $username ($email)" -ForegroundColor Green
} catch {
    Write-Error "Registration Failed: $($_.Exception.Message)"
}

# 2. Login
Write-Host "`n2. Logging in..." -ForegroundColor Yellow
$loginBody = @{
    username = $username
    password = $password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest -Uri "$baseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = ($loginResponse.Content | ConvertFrom-Json).token
    $headers = @{ "Authorization" = "Bearer $token" }
    Write-Host "✓ Login Successful" -ForegroundColor Green
} catch {
    Write-Error "Login Failed: $($_.Exception.Message)"
}

# 3. Create House
Write-Host "`n3. Creating House..." -ForegroundColor Yellow
$houseBody = @{
    propertyCode = "HS-" + $rand
    name = "Violet Heights"
    description = "Luxury apartments"
    houseType = "APARTMENT"
    streetAddress = "123 Ocean Road"
    district = "Kinondoni"
    region = "Dar es Salaam"
    country = "Tanzania"
    totalFloors = 5
    yearBuilt = 2022
    monthlyCommonCharges = 50000
    hasParking = $true
    hasSecurity = $true
} | ConvertTo-Json

try {
    $houseRes = Invoke-WebRequest -Uri "$baseUrl/houses" -Method POST -Body $houseBody -ContentType "application/json" -Headers $headers
    $houseId = ($houseRes.Content | ConvertFrom-Json).data.id
    Write-Host "✓ House Created (ID: $houseId)" -ForegroundColor Green
} catch {
    Write-Error "House Creation Failed: $($_.Exception.Message)"
}

# 4. Upload House Image
Write-Host "`n4. Uploading House Image..." -ForegroundColor Yellow
# Create a dummy image file
$dummyImg = "temp_house.jpg"
if (-not (Test-Path $dummyImg)) {
    try { Invoke-WebRequest "https://via.placeholder.com/300" -OutFile $dummyImg } catch { Set-Content $dummyImg "DummyContent" }
}

$boundary = [System.Guid]::NewGuid().ToString()
$LF = "`r`n"
$fileBytes = [System.IO.File]::ReadAllBytes($dummyImg)
$fileEnc = [System.Text.Encoding]::GetEncoding('iso-8859-1').GetString($fileBytes)

$bodyLines = (
    "--$boundary",
    "Content-Disposition: form-data; name=`"file`"; filename=`"$dummyImg`"",
    "Content-Type: image/jpeg",
    "",
    "$fileEnc",
    "--$boundary",
    "Content-Disposition: form-data; name=`"caption`"",
    "",
    "Front View",
    "--$boundary",
    "Content-Disposition: form-data; name=`"isPrimary`"",
    "",
    "true",
    "--$boundary--"
) -join $LF

$uploadHeaders = $headers.Clone()
$uploadHeaders["Content-Type"] = "multipart/form-data; boundary=$boundary"

try {
    Invoke-WebRequest -Uri "$baseUrl/houses/$houseId/images" -Method POST -Body $bodyLines -Headers $uploadHeaders
    Write-Host "✓ Image Uploaded" -ForegroundColor Green
} catch {
    Write-Warning "Image Upload Failed (might be due to dummy file format not being real JPG)"
}

# 5. Create Room
Write-Host "`n5. Creating Room..." -ForegroundColor Yellow
$roomBody = @{
    houseId = $houseId
    roomNumber = "A-101"
    floor = 1
    roomType = "TWO_BEDROOM"
    monthlyRent = 750000
    description = "Pool view"
} | ConvertTo-Json

try {
    $roomRes = Invoke-WebRequest -Uri "$baseUrl/houses/$houseId/rooms" -Method POST -Body $roomBody -ContentType "application/json" -Headers $headers
    $roomId = ($roomRes.Content | ConvertFrom-Json).data.id
    Write-Host "✓ Room Created (ID: $roomId)" -ForegroundColor Green
} catch {
    Write-Error "Room Creation Failed: $($_.Exception.Message)"
}

# 6. Register Tenant
Write-Host "`n6. Registering Tenant..." -ForegroundColor Yellow
$tenantBody = @{
    firstName = "Jane"
    lastName = "Tenant"
    email = "jane_$rand@example.com"
    phoneNumber = "+2556" + (Get-Random -Minimum 10000000 -Maximum 99999999)
    nationalId = "TID-" + (Get-Random)
    emergencyContactName = "John Parent"
    emergencyContactPhone = "+255700000000"
} | ConvertTo-Json

try {
    $tenantRes = Invoke-WebRequest -Uri "$baseUrl/tenants" -Method POST -Body $tenantBody -ContentType "application/json" -Headers $headers
    $tenantId = ($tenantRes.Content | ConvertFrom-Json).data.id
    Write-Host "✓ Tenant Registered (ID: $tenantId)" -ForegroundColor Green
} catch {
    Write-Error "Tenant Registration Failed: $($_.Exception.Message)"
}

# 7. Create Contract Template
Write-Host "`n7. Creating Contract Template..." -ForegroundColor Yellow
$templateBody = @{
    name = "Standard Lease"
    description = "Default residential lease"
    content = "LEASE AGREEMENT`n`nLandlord: {{landlordName}}`nTenant: {{tenantName}}`nProperty: {{houseAddress}}, Room {{roomNumber}}`n`nRent: {{rentAmount}} TZS per month.`nStart Date: {{leaseStartDate}}`nEnd Date: {{leaseEndDate}}`n`nSigned: ___________________"
} | ConvertTo-Json

try {
    $tplRes = Invoke-WebRequest -Uri "$baseUrl/contract-templates" -Method POST -Body $templateBody -ContentType "application/json" -Headers $headers
    $templateId = ($tplRes.Content | ConvertFrom-Json).data.id
    Write-Host "✓ Template Created (ID: $templateId)" -ForegroundColor Green
} catch {
    Write-Error "Template Creation Failed: $($_.Exception.Message)"
}

# 8. Create Lease
Write-Host "`n8. Creating Lease..." -ForegroundColor Yellow
$leaseBody = @{
    roomId = $roomId
    tenantId = $tenantId
    startDate = (Get-Date).ToString("yyyy-MM-dd") # Starts TODAY so notification check catches it next run
    endDate = (Get-Date).AddYears(1).ToString("yyyy-MM-dd")
    monthlyRent = 750000
    paymentPeriod = "MONTHLY"
    paymentDueDay = (Get-Date).Day
} | ConvertTo-Json

try {
    $leaseRes = Invoke-WebRequest -Uri "$baseUrl/leases" -Method POST -Body $leaseBody -ContentType "application/json" -Headers $headers
    $leaseId = ($leaseRes.Content | ConvertFrom-Json).data.id
    Write-Host "✓ Lease Created (ID: $leaseId)" -ForegroundColor Green
} catch {
    Write-Error "Lease Creation Failed: $($_.Exception.Message)"
}

# 9. Generate PDF Contract
Write-Host "`n9. Generating PDF Contract..." -ForegroundColor Yellow
try {
    $pdfRes = Invoke-WebRequest -Uri "$baseUrl/leases/$leaseId/generate-contract?templateId=$templateId" -Method POST -Headers $headers
    $docUrl = ($pdfRes.Content | ConvertFrom-Json).data.contractDocumentUrl
    Write-Host "✓ Contract Generated!" -ForegroundColor Green
    Write-Host "  Document URL: $docUrl" -ForegroundColor Cyan
} catch {
    # If the response was 200 OK but handled as error by PowerShell default behavior for some return types, check status
    if ($_.Response.StatusCode -eq 200) {
         Write-Host "✓ Contract Generated (Response Code 200)" -ForegroundColor Green
    } else {
        Write-Error "PDF Generation Failed: $($_.Exception.Message)"
    }
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Cyan
Write-Host "You have successfully traversed the full flow from registration to contract generation."
Write-Host "Any Due Date notifications will be generated by the scheduler (check docker logs)."
