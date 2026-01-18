# Image Upload E2E Test Script
# This script tests the image upload functionality for houses.

Write-Host "=== Image Upload End-to-End Test ===" -ForegroundColor Cyan

# 0. Configuration
$baseUrl = "http://localhost:8082/api/v1"
$tempImage = "test_image_temp.jpg"

# Create a dummy image for testing if it doesn't exist
if (-not (Test-Path $tempImage)) {
    # improved dummy image creation (using .NET for a valid JPEG header if possible, or just text)
    # For simplicity, we'll create a text file but name it .jpg - backend validation is simple enough to pass basic checks unless it does deep inspection
    # Update: Backend uses ImageIO/Thumbnails, so it NEEDS to be a real image. 
    # Let's try to just download a placeholder image or skip if fail
    try {
        Invoke-WebRequest -Uri "https://via.placeholder.com/150" -OutFile $tempImage
        Write-Host "✓ Created temporary test image: $tempImage" -ForegroundColor Green
    } catch {
        Write-Host "Please provide a valid image file named '$tempImage' in the current directory for this test." -ForegroundColor Red
        exit
    }
}

# 1. Login
Write-Host "`n1. Logging in..." -ForegroundColor Yellow
if (-not (Test-Path "test_login.json")) {
    Write-Host "Error: test_login.json not found!" -ForegroundColor Red
    exit
}
$loginBody = Get-Content test_login.json -Raw
try {
    $loginResponse = Invoke-WebRequest -Uri "$baseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = ($loginResponse.Content | ConvertFrom-Json).token
    $headers = @{ "Authorization" = "Bearer $token" }
    Write-Host "✓ Login successful" -ForegroundColor Green
} catch {
    Write-Host "✗ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# 2. Get existing house ID (or create one)
Write-Host "`n2. Getting a House ID..." -ForegroundColor Yellow
try {
    $housesResponse = Invoke-WebRequest -Uri "$baseUrl/houses" -Method GET -Headers $headers
    $houses = ($housesResponse.Content | ConvertFrom-Json).data.data
    $houseId = $null
    
    if ($houses.Count -gt 0) {
        $houseId = $houses[0].id
        Write-Host "✓ Found existing House ID: $houseId" -ForegroundColor Green
    } else {
        # Create a house if none exist
        Write-Host "  No houses found. Creating one..." -ForegroundColor Yellow
        $createHouseBody = @{
            propertyCode = "TEST-IMG-HOUSE"
            name = "Test Image House"
            description = "House for image testing"
            houseType = "APARTMENT"
            streetAddress = "123 Image St"
            district = "Kinondoni"
            region = "Dar es Salaam"
            country = "Tanzania"
            totalFloors = 1
            yearBuilt = 2024
            monthlyCommonCharges = 0
            hasParking = $true
        } | ConvertTo-Json
        $createResponse = Invoke-WebRequest -Uri "$baseUrl/houses" -Method POST -Body $createHouseBody -ContentType "application/json" -Headers $headers
        $houseId = ($createResponse.Content | ConvertFrom-Json).data.id
        Write-Host "✓ Created new House ID: $houseId" -ForegroundColor Green
    }
} catch {
    Write-Host "✗ Failed to get/create house: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# 3. Upload Image
Write-Host "`n3. Uploading Image to House $houseId..." -ForegroundColor Yellow
try {
    $boundary = [System.Guid]::NewGuid().ToString()
    $LF = "`r`n"
    
    $fileBytes = [System.IO.File]::ReadAllBytes($tempImage)
    $fileEnc = [System.Text.Encoding]::GetEncoding('iso-8859-1').GetString($fileBytes)
    
    $bodyLines = (
        "--$boundary",
        "Content-Disposition: form-data; name=`"file`"; filename=`"$tempImage`"",
        "Content-Type: image/jpeg",
        "",
        "$fileEnc",
        "--$boundary",
        "Content-Disposition: form-data; name=`"caption`"",
        "",
        "Test Living Room",
        "--$boundary",
        "Content-Disposition: form-data; name=`"isPrimary`"",
        "",
        "true",
        "--$boundary--"
    ) -join $LF
    
    $uploadHeaders = $headers.Clone()
    $uploadHeaders["Content-Type"] = "multipart/form-data; boundary=$boundary"
    
    $uploadResponse = Invoke-WebRequest -Uri "$baseUrl/houses/$houseId/images" -Method POST -Body $bodyLines -Headers $uploadHeaders
    $uploadResult = ($uploadResponse.Content | ConvertFrom-Json)
    
    if ($uploadResult.success) {
        Write-Host "✓ Image uploaded successfully!" -ForegroundColor Green
        $imageId = $uploadResult.data.id
        $imageUrl = $uploadResult.data.imageUrl
        Write-Host "  Image ID: $imageId" -ForegroundColor Cyan
        Write-Host "  URL: $imageUrl" -ForegroundColor Cyan
    } else {
        Write-Host "✗ Upload failed: $($uploadResult.message)" -ForegroundColor Red
        exit
    }
} catch {
    Write-Host "✗ Upload request failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails) { Write-Host "  Details: $($_.ErrorDetails.Message)" -ForegroundColor Red }
    exit
}

# 4. Verify Image in House Details
Write-Host "`n4. Verifying Image in House Details..." -ForegroundColor Yellow
try {
    $houseResponse = Invoke-WebRequest -Uri "$baseUrl/houses/$houseId" -Method GET -Headers $headers
    $houseData = ($houseResponse.Content | ConvertFrom-Json).data
    
    $foundImage = $houseData.images | Where-Object { $_.id -eq $imageId }
    
    if ($foundImage) {
        Write-Host "✓ Image found in house details!" -ForegroundColor Green
        Write-Host "  Primary URL: $($houseData.primaryImageUrl)" -ForegroundColor Cyan
    } else {
        Write-Host "✗ Image NOT found in house details." -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Failed to verify house details: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Image Upload Test Complete ===" -ForegroundColor Cyan
